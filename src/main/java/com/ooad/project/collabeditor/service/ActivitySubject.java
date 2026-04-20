package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.User;

public interface ActivitySubject {
    void registerObserver(ActivityObserver observer);
    void removeObserver(ActivityObserver observer);
    void notifyObservers(ActivityEvent event);
    void publishEvent(String eventType, User user, Long documentId);
}
