package com.mz.event_service.repositories;

import com.mz.event_service.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByGroupId(Long groupId);
    List<Event> findByCreatorId(Long creatorId);
}
