package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.ActivityMetrics;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.ActivityEventRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ActivityTracker implements ActivityObserver {

    private final ActivityEventRepository activityEventRepository;
    private final ActivitySubject activitySubject;

    @Autowired
    public ActivityTracker(ActivityEventRepository activityEventRepository, ActivitySubject activitySubject) {
        this.activityEventRepository = activityEventRepository;
        this.activitySubject = activitySubject;
    }

    @PostConstruct
    public void registerAsObserver() {
        activitySubject.registerObserver(this);
    }

    @Override
    public void onActivityEvent(ActivityEvent event) {
        activityEventRepository.save(event);
    }

    public void logEvent(String eventType, User user, Long documentId) {
        activitySubject.publishEvent(eventType, user, documentId);
    }

    public List<ActivityEvent> getAllEvents() {
        return StreamSupport.stream(activityEventRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public ActivityMetrics generateMetrics() {
        Iterable<ActivityEvent> events = activityEventRepository.findAll();
        long totalEvents = StreamSupport.stream(events.spliterator(), false).count();
        long loginEvents = StreamSupport.stream(events.spliterator(), false)
                .filter(e -> "LOGIN".equals(e.getEventType()))
                .count();
        long editEvents = StreamSupport.stream(events.spliterator(), false)
                .filter(e -> "EDIT".equals(e.getEventType()))
                .count();
        long createDocumentEvents = StreamSupport.stream(events.spliterator(), false)
                .filter(e -> "CREATE_DOCUMENT".equals(e.getEventType()))
                .count();
        long archiveDocumentEvents = StreamSupport.stream(events.spliterator(), false)
                .filter(e -> "ARCHIVE_DOCUMENT".equals(e.getEventType()))
                .count();

        return new ActivityMetrics(totalEvents, loginEvents, editEvents, createDocumentEvents, archiveDocumentEvents);
    }
}
