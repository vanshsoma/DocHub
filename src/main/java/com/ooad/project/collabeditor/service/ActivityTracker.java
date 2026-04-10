package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.ActivityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ActivityTracker {

    @Autowired
    private ActivityEventRepository activityEventRepository;

    public void logEvent(String eventType, User user, Long documentId) {
        ActivityEvent event = new ActivityEvent(eventType, user, documentId);
        activityEventRepository.save(event);
    }

    public String generateMetrics() {
        Iterable<ActivityEvent> events = activityEventRepository.findAll();
        long totalEvents = StreamSupport.stream(events.spliterator(), false).count();
        long loginEvents = StreamSupport.stream(events.spliterator(), false).filter(e -> e.getEventType().equals("LOGIN")).count();
        long editEvents = StreamSupport.stream(events.spliterator(), false).filter(e -> e.getEventType().equals("EDIT")).count();

        return String.format("Total Events: %d, Logins: %d, Edits: %d", totalEvents, loginEvents, editEvents);
    }
}
