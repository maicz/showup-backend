package com.mz.rsvp_service.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRsvpsSummary {
    private Long eventId;
    private Map<String, Long> counts;
    private List<RsvpResponse> rsvps;
}
