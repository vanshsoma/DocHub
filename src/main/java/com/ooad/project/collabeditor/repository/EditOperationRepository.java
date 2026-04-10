package com.ooad.project.collabeditor.repository;

import com.ooad.project.collabeditor.model.EditOperation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EditOperationRepository extends CrudRepository<EditOperation, Long> {
    List<EditOperation> findByDocument_DocumentIdOrderByTimestampAsc(Long documentId);
}
