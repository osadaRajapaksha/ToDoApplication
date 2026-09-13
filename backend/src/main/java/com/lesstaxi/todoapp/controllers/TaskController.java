package com.lesstaxi.todoapp.controllers;

import com.lesstaxi.todoapp.models.EStatus;
import com.lesstaxi.todoapp.models.Task;
import com.lesstaxi.todoapp.payload.request.TaskRequest;
import com.lesstaxi.todoapp.payload.response.MessageResponse;
import com.lesstaxi.todoapp.repositories.TaskRepository;
import com.lesstaxi.todoapp.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  @Autowired
  TaskRepository taskRepository;

  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = userDetails.getAuthorities().iterator().next().getAuthority();
    
    if (role.equals("ROLE_ADMIN")) {
      return ResponseEntity.ok(taskRepository.findAll());
    } else {
      return ResponseEntity.ok(taskRepository.findByCreatorIdOrAssignedUserId(userDetails.getId(), userDetails.getId()));
    }
  }

  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody TaskRequest taskRequest) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
    Task task = new Task();
    task.setTitle(taskRequest.getTitle());
    task.setDescription(taskRequest.getDescription());
    task.setCreatorId(userDetails.getId());
    // Creator can also optionally assign it to themselves upon creation
    if (taskRequest.getAssigneeId() != null && taskRequest.getAssigneeId().equals(userDetails.getId())) {
       task.setAssignedUserId(userDetails.getId());
    }
    
    return ResponseEntity.ok(taskRepository.save(task));
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<?> updateTaskStatus(@PathVariable String id, @RequestBody TaskRequest taskRequest) {
    Optional<Task> taskData = taskRepository.findById(id);
    
    if (taskData.isPresent()) {
      Task _task = taskData.get();
      try {
         _task.setStatus(EStatus.valueOf(taskRequest.getStatus()));
         _task.setUpdatedAt(LocalDateTime.now());
         return ResponseEntity.ok(taskRepository.save(_task));
      } catch (Exception e) {
         return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid status"));
      }
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}/assign")
  public ResponseEntity<?> assignTask(@PathVariable String id, @RequestBody TaskRequest taskRequest) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = userDetails.getAuthorities().iterator().next().getAuthority();
    String assigneeId = taskRequest.getAssigneeId();

    Optional<Task> taskData = taskRepository.findById(id);
    if (taskData.isPresent()) {
      Task _task = taskData.get();
      
      if (role.equals("ROLE_ADMIN")) {
         _task.setAssignedUserId(assigneeId);
      } else {
         if ((_task.getAssignedUserId() == null || _task.getAssignedUserId().isEmpty()) && assigneeId.equals(userDetails.getId())) {
             _task.setAssignedUserId(assigneeId);
         } else {
             return ResponseEntity.badRequest().body(new MessageResponse("Error: Unauthorized to assign this task."));
         }
      }
      _task.setUpdatedAt(LocalDateTime.now());
      return ResponseEntity.ok(taskRepository.save(_task));
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
