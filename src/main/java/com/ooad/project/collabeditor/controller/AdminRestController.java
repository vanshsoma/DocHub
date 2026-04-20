package com.ooad.project.collabeditor.controller;

import com.ooad.project.collabeditor.model.ActivityEvent;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.UserRepository;
import com.ooad.project.collabeditor.service.ActivityTracker;
import com.ooad.project.collabeditor.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UC-04: Activity Log & Metrics Dashboard — Admin Only
 *
 * Exposes:
 *   GET /api/admin/activity          → all events (newest first)
 *   GET /api/admin/metrics           → aggregated counts (login, edit, create…)
 *
 * Both endpoints require the caller to supply ?userId=<id> and that
 * user must have ADMIN role — otherwise 403 Forbidden is returned.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    @Autowired
    private ActivityTracker activityTracker;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    // -----------------------------------------------------------------------
    // GET /api/admin/activity?userId=1
    // Returns the full event log, ordered newest first.
    // -----------------------------------------------------------------------
    @GetMapping("/activity")
    public ResponseEntity<?> getActivityLog(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (!authService.authorize(user, "ADMIN_ONLY")) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }
        List<ActivityEvent> events = activityTracker.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // -----------------------------------------------------------------------
    // GET /api/admin/metrics?userId=1
    // Returns aggregated counts as a JSON object, e.g.:
    //   { totalEvents: 42, logins: 10, edits: 20, creates: 5, archives: 2, opens: 5 }
    // -----------------------------------------------------------------------
    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (!authService.authorize(user, "ADMIN_ONLY")) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }
        Map<String, Long> metrics = activityTracker.getMetricsMap();
        return ResponseEntity.ok(metrics);
    }
}
