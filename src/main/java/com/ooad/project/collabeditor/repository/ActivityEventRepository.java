package com.ooad.project.collabeditor.repository;

import com.ooad.project.collabeditor.model.ActivityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityEventRepository extends CrudRepository<ActivityEvent, Long> {
}
