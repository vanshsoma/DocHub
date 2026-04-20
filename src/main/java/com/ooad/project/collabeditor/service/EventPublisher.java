package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Observer Pattern (Behavioral) — Subject / Publisher
 *
 * EventPublisher is the Subject in the Observer pattern. It maintains a list
 * of all registered EventObservers and notifies each one when an event occurs.
 *
 * Spring automatically injects ALL beans that implement EventObserver into
 * the `observers` list — so adding a new observer requires zero changes here.
 *
 * Services (AuthService, DocumentService, CollaborationService) depend only
 * on EventPublisher, not on any specific observer (e.g. ActivityTracker).
 * This achieves loose coupling between event producers and consumers.
 */
@Service
public class EventPublisher {

    /**
     * Spring injects all @Service beans that implement EventObserver here.
     * Currently: [ActivityTracker]
     */
    @Autowired
    private List<EventObserver> observers;

    /**
     * Publishes an event to all registered observers.
     *
     * @param eventType  e.g. "LOGIN", "EDIT", "CREATE_DOCUMENT"
     * @param user       the user who triggered the event
     * @param documentId the affected document (null for login events)
     */
    public void publish(String eventType, User user, Long documentId) {
        for (EventObserver observer : observers) {
            observer.onEvent(eventType, user, documentId);
        }
    }
}
