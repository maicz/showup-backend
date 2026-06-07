package com.mz.event_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {

    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 100, message = "Event name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Event date is required")
    private Instant eventDate;

    @NotBlank(message = "Venue is required")
    @Size(max = 255, message = "Venue must be less than 255 characters")
    private String venue;

    private Long groupId;

    @NotBlank(message = "Event type is required (e.g. PUBLIC, PRIVATE)")
    private String type = "PUBLIC"; // default type
}
