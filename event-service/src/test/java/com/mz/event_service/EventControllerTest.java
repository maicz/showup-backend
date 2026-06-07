package com.mz.event_service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.event_service.dto.EventCreateRequest;
import com.mz.event_service.entities.Event;
import com.mz.event_service.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SECRET = "showupjwtsecretkeythatmustbeatleast256bitslongforsecurityreasons!";
    private static final String ISSUER = "showup-auth-service";

    @BeforeEach
    public void setup() {
        eventRepository.deleteAll();
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
        EventCreateRequest request = new EventCreateRequest(
                "Mountain Hike",
                "Hike in the mountains",
                Instant.now().plus(1, ChronoUnit.DAYS),
                "Brasov, Romania",
                1L,
                "PUBLIC"
        );

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateEventSuccessAndBpmFlow() throws Exception {
        String token = generateToken(1L, "eventcreator", "creator@example.com");
        Instant eventDate = Instant.now().plus(2, ChronoUnit.DAYS);
        EventCreateRequest request = new EventCreateRequest(
                "Weekend Hike",
                "Hiking in Bucegi Mountains",
                eventDate,
                "Bucegi Mountains",
                1L,
                "PUBLIC"
        );

        String responseContent = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Weekend Hike"))
                .andExpect(jsonPath("$.creatorId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        Event returnedEvent = objectMapper.readValue(responseContent, Event.class);
        Long eventId = returnedEvent.getId();

        // Query database to check event was saved successfully
        Event eventInDb = eventRepository.findById(eventId).orElseThrow();
        assertEquals("PENDING", eventInDb.getStatus());
        assertEquals("Weekend Hike", eventInDb.getName());
    }

    @Test
    public void testCreateEventValidationFail() throws Exception {
        String token = generateToken(1L, "eventcreator", "creator@example.com");
        // Short name, past date
        EventCreateRequest request = new EventCreateRequest(
                "H",
                "Hiking",
                Instant.now().minus(2, ChronoUnit.DAYS),
                "Bucegi Mountains",
                1L,
                "PUBLIC"
        );

        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    public void testGetEventDetailsSuccess() throws Exception {
        String token = generateToken(1L, "eventcreator", "creator@example.com");
        Event event = new Event(
                "Awesome Camp",
                "Summer camp near the lake",
                Instant.now().plus(5, ChronoUnit.DAYS),
                "Lake Como",
                1L,
                null,
                "PUBLIC",
                "ACTIVE"
        );
        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(get("/api/events/" + savedEvent.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Awesome Camp"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
