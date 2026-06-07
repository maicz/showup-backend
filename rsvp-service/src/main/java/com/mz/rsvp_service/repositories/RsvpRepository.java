package com.mz.rsvp_service.repositories;

import com.mz.rsvp_service.entities.Rsvp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RsvpRepository extends JpaRepository<Rsvp, Long> {
    
    @Query("SELECT r.status as status, COUNT(r) as count FROM Rsvp r WHERE r.eventId = :eventId GROUP BY r.status")
    List<EventRsvpCount> getRsvpCountsByEventId(@Param("eventId") Long eventId);

    Optional<Rsvp> findByEventIdAndUserId(Long eventId, Long userId);
    List<Rsvp> findByEventId(Long eventId);
    List<Rsvp> findByUserId(Long userId);

    interface EventRsvpCount {
        String getStatus();
        Long getCount();
    }
}
