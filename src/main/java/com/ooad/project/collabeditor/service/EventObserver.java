package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.User;

/**
 * Observer Pattern (Behavioral) — Observer Interface
 *
 * Any class that wants to react to system events (login, edit, create, etc.)
 * must implement this interface. The Subject (EventPublisher) holds a list
 * of all registered EventObservers and calls onEvent() on each of them
 * whenever an event occurs.
 *
 * Current observers:
 *  - ActivityTracker: persists events to the database
 *
 * To add a new observer (e.g. email alerts, audit log): simply create a new
 * @Service class that implements EventObserver — Spring auto-registers it.
 */
public interface EventObserver {

    /**
     * Called by the EventPublisher when a significant system event occurs.
     *
     * @param eventType  e.g. "LOGIN", "EDIT", "CREATE_DOCUMENT", "ARCHIVE_DOCUMENT"
     * @param user       the user who triggered the event
     * @param documentId the affected document ID (null for non-document events like LOGIN)
     */
    void onEvent(String eventType, User user, Long documentId);
}
