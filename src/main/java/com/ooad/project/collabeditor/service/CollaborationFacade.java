package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Facade Pattern (Structural)
 *
 * CollaborationFacade provides a simplified, unified interface to the
 * collaboration subsystem. It hides the complexity of coordinating
 * CollaborationService and UserRepository from the WebSocketController,
 * reducing coupling and keeping the controller focused on messaging concerns.
 *
 * Subsystems hidden behind this facade:
 *  - UserRepository     (user resolution)
 *  - CollaborationService (edit application + conflict resolution)
 */
@Service
public class CollaborationFacade {

    @Autowired
    private CollaborationService collaborationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Handles a full edit request end-to-end:
     *  1. Resolves the User from userId
     *  2. Delegates to CollaborationService to apply the edit with conflict detection
     *
     * @return new document version on success,
     *         -1 if the document is locked/archived (conflict),
     *         -2 if the user was not found
     */
    public int handleEdit(Long documentId, Long userId, String newContent, int baseVersion) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return -2; // User not found
        }
        return collaborationService.applyEdit(documentId, user, newContent, baseVersion);
    }
}
