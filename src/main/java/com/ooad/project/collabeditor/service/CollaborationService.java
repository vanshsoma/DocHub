package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.Document;
import com.ooad.project.collabeditor.model.DocumentStatus;
import com.ooad.project.collabeditor.model.EditOperation;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.DocumentRepository;
import com.ooad.project.collabeditor.repository.EditOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CollaborationService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EditOperationRepository editOperationRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ActivitySubject activitySubject;

    /**
     * Attempts to apply an edit. Returns the updated version if successful, or -1 if conflicted.
     */
    public synchronized int applyEdit(Long documentId, User user, String newContent, int baseVersion) {
        Optional<Document> docOpt = documentRepository.findById(documentId);
        if (docOpt.isEmpty()) return -1;
        
        Document doc = docOpt.get();
        if (doc.getStatus() == DocumentStatus.LOCKED || doc.getStatus() == DocumentStatus.ARCHIVED) {
            return -1; // Cannot edit locked/archived
        }

        // Version check for logical conflicts
        if (doc.getVersion() != baseVersion) {
            // State diagram: Mismatch detected -> Locked
            documentService.lockDocument(documentId);
            
            // To prevent terrible UX in simple collaboration, we'll automatically resolve it (Last Writer Wins)
            // State diagram: CollaborationService.resolveConflict() -> Editing
            doc = resolveConflict(documentId, newContent);
            
            // Record operation anyway
            EditOperation op = new EditOperation(doc, user, newContent, doc.getVersion());
            editOperationRepository.save(op);
            activitySubject.publishEvent("EDIT", user, documentId);
            
            return doc.getVersion();
        }

        // No conflict, apply edit
        doc.updateContent(newContent);
        doc = documentRepository.save(doc);

        // Record operation
        EditOperation op = new EditOperation(doc, user, newContent, doc.getVersion());
        editOperationRepository.save(op);

        activitySubject.publishEvent("EDIT", user, documentId);

        return doc.getVersion();
    }

    public synchronized Document resolveConflict(Long documentId, String resolvedContent) {
        Optional<Document> docOpt = documentRepository.findById(documentId);
        if (docOpt.isEmpty()) return null;

        Document doc = docOpt.get();
        doc.updateContent(resolvedContent);
        doc.setStatus(DocumentStatus.EDITING); // Unlock
        return documentRepository.save(doc);
    }
}
