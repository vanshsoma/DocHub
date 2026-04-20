package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.Document;
import com.ooad.project.collabeditor.model.DocumentStatus;
import com.ooad.project.collabeditor.model.EditOperation;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.DocumentRepository;
import com.ooad.project.collabeditor.repository.EditOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private EditOperationRepository editOperationRepository;

    @Autowired
    private ActivitySubject activitySubject;

    public Document createDocument(String title, String initialContent, User owner) {
        Document document = new Document(title, initialContent, owner);
        document = documentRepository.save(document);
        activitySubject.publishEvent("CREATE_DOCUMENT", owner, document.getDocumentId());
        return document;
    }

    public Optional<Document> getDocument(Long documentId) {
        return documentRepository.findById(documentId);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAllByOrderByDocumentIdDesc();
    }

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }
    
    public void openForEditing(Long documentId, User user) {
        Optional<Document> docOpt = getDocument(documentId);
        if (docOpt.isPresent()) {
            Document doc = docOpt.get();
            if (doc.getStatus() == DocumentStatus.DRAFT || doc.getStatus() == DocumentStatus.EDITING) {
                doc.setStatus(DocumentStatus.EDITING);
                saveDocument(doc);
                activitySubject.publishEvent("OPEN_DOCUMENT", user, documentId);
            }
        }
    }
    
    public void lockDocument(Long documentId) {
        Optional<Document> docOpt = getDocument(documentId);
        docOpt.ifPresent(doc -> {
            doc.setStatus(DocumentStatus.LOCKED);
            saveDocument(doc);
        });
    }

    public void archiveDocument(Long documentId, User adminUser) {
        Optional<Document> docOpt = getDocument(documentId);
        docOpt.ifPresent(doc -> {
            doc.setStatus(DocumentStatus.ARCHIVED);
            saveDocument(doc);
            activitySubject.publishEvent("ARCHIVE_DOCUMENT", adminUser, documentId);
        });
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        List<EditOperation> ops = editOperationRepository.findByDocument_DocumentIdOrderByTimestampAsc(documentId);
        editOperationRepository.deleteAll(ops);
        documentRepository.deleteById(documentId);
    }
}
