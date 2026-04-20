package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.ActivityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    /** Returns all events ordered by most recent first. */
    public List<ActivityEvent> getAllEvents() {
        return activityEventRepository.findAllByOrderByTimestampDesc();
    }

    /** Returns a structured map of metrics for the admin dashboard. */
    public Map<String, Long> getMetricsMap() {
        Iterable<ActivityEvent> events = activityEventRepository.findAll();
        long totalEvents = StreamSupport.stream(events.spliterator(), false).count();
        long loginEvents  = activityEventRepository.countByEventType("LOGIN");
        long editEvents   = activityEventRepository.countByEventType("EDIT");
        long createEvents = activityEventRepository.countByEventType("CREATE_DOCUMENT");
        long archiveEvents= activityEventRepository.countByEventType("ARCHIVE_DOCUMENT");
        long openEvents   = activityEventRepository.countByEventType("OPEN_DOCUMENT");

        Map<String, Long> metrics = new LinkedHashMap<>();
        metrics.put("totalEvents",  totalEvents);
        metrics.put("logins",       loginEvents);
        metrics.put("edits",        editEvents);
        metrics.put("creates",      createEvents);
        metrics.put("archives",     archiveEvents);
        metrics.put("opens",        openEvents);
        return metrics;
    }

    /** Legacy plain-text metrics (kept for backward compatibility). */
    public String generateMetrics() {
        Map<String, Long> m = getMetricsMap();
        return String.format("Total Events: %d, Logins: %d, Edits: %d",
                m.get("totalEvents"), m.get("logins"), m.get("edits"));
    }
}

