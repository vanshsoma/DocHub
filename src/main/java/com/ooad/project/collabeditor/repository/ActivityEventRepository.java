package com.ooad.project.collabeditor.repository;

import com.ooad.project.collabeditor.model.ActivityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityEventRepository extends CrudRepository<ActivityEvent, Long> {
    List<ActivityEvent> findAllByOrderByTimestampDesc();
    long countByEventType(String eventType);
    List<ActivityEvent> findByUser_UserId(Long userId);
}
