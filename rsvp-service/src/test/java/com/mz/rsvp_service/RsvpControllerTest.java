package com.mz.rsvp_service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.rsvp_service.dto.RsvpRequest;
import com.mz.rsvp_service.entities.Rsvp;
import com.mz.rsvp_service.repositories.RsvpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RsvpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RsvpRepository rsvpRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SECRET = "showupjwtsecretkeythatmustbeatleast256bitslongforsecurityreasons!";
    private static final String ISSUER = "showup-auth-service";

    @BeforeEach
    public void setup() {
        rsvpRepository.deleteAll();
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
        RsvpRequest request = new RsvpRequest(1L, "YES");

        mockMvc.perform(post("/api/rsvps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateOrUpdateRsvpSuccessAndBpmFlow() throws Exception {
        String token = generateToken(100L, "rsvptester", "tester@example.com");
        RsvpRequest request = new RsvpRequest(1L, "YES");

        String responseContent = mockMvc.perform(post("/api/rsvps")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(1))
                .andExpect(jsonPath("$.userId").value(100))
                .andExpect(jsonPath("$.status").value("YES"))
                .andReturn().getResponse().getContentAsString();

        Rsvp returnedRsvp = objectMapper.readValue(responseContent, Rsvp.class);
        Long rsvpId = returnedRsvp.getId();

        // Query database to check RSVP was saved successfully
        Rsvp rsvpInDb = rsvpRepository.findById(rsvpId).orElseThrow();
        assertEquals("YES", rsvpInDb.getStatus());
        assertEquals(100L, rsvpInDb.getUserId());
    }

    @Test
    public void testCreateRsvpValidationFail() throws Exception {
        String token = generateToken(100L, "rsvptester", "tester@example.com");
        // Invalid status
        RsvpRequest request = new RsvpRequest(1L, "MAYBE_OR_NOT");

        mockMvc.perform(post("/api/rsvps")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    public void testGetEventRsvpsSummary() throws Exception {
        String token = generateToken(100L, "rsvptester", "tester@example.com");
        
        rsvpRepository.save(new Rsvp(1L, 101L, "YES"));
        rsvpRepository.save(new Rsvp(1L, 102L, "YES"));
        rsvpRepository.save(new Rsvp(1L, 103L, "NO"));
        rsvpRepository.save(new Rsvp(1L, 104L, "MAYBE"));

        mockMvc.perform(get("/api/rsvps/events/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1))
                .andExpect(jsonPath("$.counts.YES").value(2))
                .andExpect(jsonPath("$.counts.NO").value(1))
                .andExpect(jsonPath("$.counts.MAYBE").value(1))
                .andExpect(jsonPath("$.rsvps.length()").value(4));
    }

    @Test
    public void testGetMyRsvps() throws Exception {
        String token = generateToken(200L, "john", "john@example.com");

        rsvpRepository.save(new Rsvp(1L, 200L, "YES"));
        rsvpRepository.save(new Rsvp(2L, 200L, "NO"));
        rsvpRepository.save(new Rsvp(3L, 201L, "YES")); // other user

        mockMvc.perform(get("/api/rsvps/my")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
