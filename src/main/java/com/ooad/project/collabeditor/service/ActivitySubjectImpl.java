package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ActivitySubjectImpl implements ActivitySubject {

    private final List<ActivityObserver> observers = new CopyOnWriteArrayList<>();

    @Override
    public void registerObserver(ActivityObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(ActivityObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(ActivityEvent event) {
        for (ActivityObserver observer : observers) {
            observer.onActivityEvent(event);
        }
    }

    @Override
    public void publishEvent(String eventType, User user, Long documentId) {
        notifyObservers(new ActivityEvent(eventType, user, documentId));
    }
}
