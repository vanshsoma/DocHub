package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.ActivityEvent;

public interface ActivityObserver {
    void onActivityEvent(ActivityEvent event);
}
