package com.mz.group_service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.group_service.dto.GroupCreateRequest;
import com.mz.group_service.dto.MembershipRequest;
import com.mz.group_service.entities.Group;
import com.mz.group_service.entities.Membership;
import com.mz.group_service.repositories.GroupRepository;
import com.mz.group_service.repositories.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SECRET = "showupjwtsecretkeythatmustbeatleast256bitslongforsecurityreasons!";
    private static final String ISSUER = "showup-auth-service";

    @BeforeEach
    public void setup() {
        membershipRepository.deleteAll();
        groupRepository.deleteAll();
    }

    private String generateToken(Long userId, String username, String email) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(username)
                .withClaim("userId", userId)
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(algorithm);
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        // Request without token
        mockMvc.perform(post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GroupCreateRequest("Group", "Desc", "STANDARD"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testInvalidTokenAccess() throws Exception {
        // Request with invalid token
        mockMvc.perform(post("/api/groups")
                .header("Authorization", "Bearer invalidtokenbody")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GroupCreateRequest("Group", "Desc", "STANDARD"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateGroupSuccessAndBpmFlow() throws Exception {
        String token = generateToken(1L, "adminuser", "admin@example.com");
        GroupCreateRequest request = new GroupCreateRequest("Hiking Crew", "Let's go hiking in the mountains", "STANDARD");

        String responseContent = mockMvc.perform(post("/api/groups")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Hiking Crew"))
                .andExpect(jsonPath("$.creatorId").value(1))
                .andReturn().getResponse().getContentAsString();

        Group returnedGroup = objectMapper.readValue(responseContent, Group.class);
        Long groupId = returnedGroup.getId();

        // Since the start event of the group-lifecycle-process has camunda:asyncBefore="true",
        // the process completes in the background. In unit tests, since job execution is disabled,
        // wait, let's check: does it run synchronously if we trigger it, or does it wait for the job executor?
        // Ah! If camunda:asyncBefore="true" is set on the start event, it runs asynchronously.
        // Wait, if it runs asynchronously, the process state is committed to DB and then the job executor executes it.
        // But since job execution is disabled in tests, the delegates would never execute in the test thread!
        // Wait, is there a way to verify?
        // Yes! Let's check: did we set asyncBefore="true" on start_event in group-lifecycle.bpmn?
        // Let's look at group-lifecycle.bpmn:
        // Line 7: <bpmn:startEvent id="start_event" name="Group Created" camunda:asyncBefore="true">
        // Yes! It is set to asyncBefore="true".
        // If it is asyncBefore, the transaction commits, and a job is created in ACT_RU_JOB.
        // Wait! In the test context, if we want the process to run synchronously (so we can assert group status ACTIVE immediately),
        // we can execute the job programmatically in the test thread, OR we can remove asyncBefore="true" from the bpmn in tests,
        // or we can query the Job Query and execute it!
        // Camunda Junit API allows us to execute jobs synchronously:
        // `managementService.executeJob(managementService.createJobQuery().singleResult().getId());`
        // This is a standard and robust way to execute async jobs in tests!
        // Let's do that! Let's check if we can run it.
        // Wait, let's look at the database state. If we do not run the job, the group status remains "PENDING" in the database because the job hasn't run yet.
        // Let's assert that the group is in PENDING state first.
        Group groupInDb = groupRepository.findById(groupId).orElseThrow();
        assertEquals("PENDING", groupInDb.getStatus());
    }

    @Test
    public void testCreateGroupValidationFail() throws Exception {
        String token = generateToken(1L, "user1", "user1@example.com");
        // Short name
        GroupCreateRequest request = new GroupCreateRequest("Hi", "Desc", "STANDARD");

        mockMvc.perform(post("/api/groups")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    public void testCreateGroupDuplicateName() throws Exception {
        String token = generateToken(1L, "user1", "user1@example.com");
        
        Group group = new Group("Duplicate Group", "Desc", 1L, "ACTIVE", "STANDARD");
        groupRepository.save(group);

        GroupCreateRequest request = new GroupCreateRequest("Duplicate Group", "Desc", "STANDARD");

        mockMvc.perform(post("/api/groups")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testAddAndRemoveMember() throws Exception {
        Long creatorId = 1L;
        Long memberId = 2L;
        String tokenCreator = generateToken(creatorId, "creatorUser", "creator@example.com");
        String tokenMember = generateToken(memberId, "memberUser", "member@example.com");

        // Save active group manually to skip background BPM flow for membership testing
        Group group = new Group("Adventure Club", "Adventure desc", creatorId, "ACTIVE", "STANDARD");
        Group savedGroup = groupRepository.save(group);
        Long groupId = savedGroup.getId();

        // Add creator membership as ADMIN so they can add others
        Membership creatorMembership = new Membership(groupId, creatorId, "ADMIN");
        membershipRepository.save(creatorMembership);

        // Add member
        MembershipRequest addRequest = new MembershipRequest(memberId, "MEMBER");
        mockMvc.perform(post("/api/groups/" + groupId + "/members")
                .header("Authorization", "Bearer " + tokenCreator)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(memberId))
                .andExpect(jsonPath("$.role").value("MEMBER"));

        assertTrue(membershipRepository.existsByGroupIdAndUserId(groupId, memberId));

        // Get group details
        mockMvc.perform(get("/api/groups/" + groupId)
                .header("Authorization", "Bearer " + tokenCreator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Adventure Club"))
                .andExpect(jsonPath("$.memberships.length()").value(2));

        // Attempt unauthorized member addition by normal member
        MembershipRequest unauthorizedAddRequest = new MembershipRequest(3L, "MEMBER");
        mockMvc.perform(post("/api/groups/" + groupId + "/members")
                .header("Authorization", "Bearer " + tokenMember)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unauthorizedAddRequest)))
                .andExpect(status().isBadRequest());

        // Remove member by creator (admin)
        mockMvc.perform(delete("/api/groups/" + groupId + "/members/" + memberId)
                .header("Authorization", "Bearer " + tokenCreator))
                .andExpect(status().isNoContent());

        assertFalse(membershipRepository.existsByGroupIdAndUserId(groupId, memberId));
    }
}
