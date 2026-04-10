package com.ooad.project.collabeditor.repository;

import com.ooad.project.collabeditor.model.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
    List<Document> findAllByOrderByDocumentIdDesc();
}
