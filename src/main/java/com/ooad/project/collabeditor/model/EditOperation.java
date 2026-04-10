package com.ooad.project.collabeditor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "edit_operations")
public class EditOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationId;

    private LocalDateTime timestamp;
    
    @Lob
    private String newContent;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private int appliedVersion;

    public EditOperation() {}

    public EditOperation(Document document, User user, String newContent, int appliedVersion) {
        this.document = document;
        this.user = user;
        this.newContent = newContent;
        this.appliedVersion = appliedVersion;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public Long getOperationId() { return operationId; }
    public void setOperationId(Long operationId) { this.operationId = operationId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getNewContent() { return newContent; }
    public void setNewContent(String newContent) { this.newContent = newContent; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getAppliedVersion() { return appliedVersion; }
    public void setAppliedVersion(int appliedVersion) { this.appliedVersion = appliedVersion; }
}
