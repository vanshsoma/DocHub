package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.ActivityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.StreamSupport;

/**
 * Observer Pattern (Behavioral) — Concrete Observer
 *
 * ActivityTracker implements EventObserver. The EventPublisher (Subject)
 * calls onEvent() on this class whenever a system event is published.
 * It persists every event to the database for audit logging and metrics.
 */
@Service
public class ActivityTracker implements EventObserver {

    @Autowired
    private ActivityEventRepository activityEventRepository;

    /**
     * Observer callback — called by EventPublisher.publish() for every event.
     * Saves the event to the database.
     */
    @Override
    public void onEvent(String eventType, User user, Long documentId) {
        ActivityEvent event = new ActivityEvent(eventType, user, documentId);
        activityEventRepository.save(event);
    }

    public String generateMetrics() {
        Iterable<ActivityEvent> events = activityEventRepository.findAll();
        long totalEvents = StreamSupport.stream(events.spliterator(), false).count();
        long loginEvents = StreamSupport.stream(events.spliterator(), false)
                .filter(e -> e.getEventType().equals("LOGIN")).count();
        long editEvents = StreamSupport.stream(events.spliterator(), false)
                .filter(e -> e.getEventType().equals("EDIT")).count();

        return String.format("Total Events: %d, Logins: %d, Edits: %d", totalEvents, loginEvents, editEvents);
    }
}
