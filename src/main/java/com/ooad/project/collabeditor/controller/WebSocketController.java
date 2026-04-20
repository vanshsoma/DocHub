package com.ooad.project.collabeditor.controller;

import com.ooad.project.collabeditor.service.CollaborationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebSocketController {

    /** Facade (Structural Pattern) — single entry point to the collaboration subsystem */
    @Autowired
    private CollaborationFacade collaborationFacade;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Track sessions map: sessionId -> userId
    private final Map<String, Long> activeSessions = new ConcurrentHashMap<>();

    // DTO for Edit requests
    public static class EditMessage {
        public Long userId;
        public String newContent;
        public int baseVersion;
    }

    // DTO for Cursor Indicator
    public static class CursorMessage {
        public String username;
        public String color;
        public int index;
    }

    // DTO for Delta Syncing
    public static class DeltaMessage {
        public Long userId;
        public String deltaJson;
    }

    // DTO for Sync response
    public static class SyncMessage {
        public String status; // SUCCESS, CONFLICT
        public String content;
        public int newVersion;
        public Long sourceUserId;
        
        public SyncMessage(String status, String content, int newVersion, Long sourceUserId) {
             this.status = status; this.content = content; this.newVersion = newVersion; this.sourceUserId = sourceUserId;
        }
    }

    @MessageMapping("/document.register/{documentId}")
    public void registerSession(@DestinationVariable Long documentId, @Payload EditMessage msg, SimpMessageHeaderAccessor headerAccessor) {
        activeSessions.put(headerAccessor.getSessionId(), msg.userId);
        System.out.println("Session Registered for user: " + msg.userId + " on doc: " + documentId);
    }

    @MessageMapping("/document.edit/{documentId}")
    @SendTo("/topic/document/{documentId}")
    public SyncMessage processEdit(@DestinationVariable Long documentId, @Payload EditMessage msg) {
        // Facade handles user resolution + edit application in one call
        int result = collaborationFacade.handleEdit(documentId, msg.userId, msg.newContent, msg.baseVersion);

        if (result == -2) {
            return new SyncMessage("ERROR", "User not found", msg.baseVersion, msg.userId);
        }
        if (result == -1) {
            // Conflict (document locked/archived)
            return new SyncMessage("CONFLICT", msg.newContent, msg.baseVersion, msg.userId);
        }
        // Successfully applied edit
        return new SyncMessage("SUCCESS", msg.newContent, result, msg.userId);
    }

    @MessageMapping("/document.delta/{documentId}")
    @SendTo("/topic/document.delta/{documentId}")
    public DeltaMessage processDelta(@DestinationVariable Long documentId, @Payload DeltaMessage msg) {
        // Forward the operational transformation delta securely to other peers
        return msg;
    }

    @MessageMapping("/document.cursor/{documentId}")
    @SendTo("/topic/document.cursor/{documentId}")
    public CursorMessage processCursor(@DestinationVariable Long documentId, @Payload CursorMessage msg) {
        return msg;
    }
}
