package com.ooad.project.collabeditor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    private String title;

    @Lob
    private String content;

    private int version;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Document() {
        this.version = 1;
        this.status = DocumentStatus.DRAFT;
    }

    public Document(String title, String content, User owner) {
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.version = 1;
        this.status = DocumentStatus.DRAFT;
    }

    // Getters and Setters
    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.version++;
    }
}
