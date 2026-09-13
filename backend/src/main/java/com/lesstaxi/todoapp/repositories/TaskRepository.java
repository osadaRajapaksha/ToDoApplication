package com.lesstaxi.todoapp.repositories;

import com.lesstaxi.todoapp.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
  List<Task> findByCreatorIdOrAssignedUserId(String creatorId, String assignedUserId);
}
