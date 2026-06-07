package com.mz.rsvp_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsvpRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "RSVP status is required")
    @Pattern(regexp = "^(?i)(YES|NO|MAYBE)$", message = "Status must be YES, NO, or MAYBE")
    private String status;
}
