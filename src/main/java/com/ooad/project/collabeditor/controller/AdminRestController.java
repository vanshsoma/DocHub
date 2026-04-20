package com.ooad.project.collabeditor.controller;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.ActivityMetrics;
import com.ooad.project.collabeditor.service.ActivityTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    @Autowired
    private ActivityTracker activityTracker;

    @GetMapping("/activity")
    public ResponseEntity<List<ActivityEvent>> getActivityEvents() {
        return ResponseEntity.ok(activityTracker.getAllEvents());
    }

    @GetMapping("/metrics")
    public ResponseEntity<ActivityMetrics> getActivityMetrics() {
        return ResponseEntity.ok(activityTracker.generateMetrics());
    }
}
