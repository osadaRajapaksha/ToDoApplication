package com.lesstaxi.todoapp.controllers;

import com.lesstaxi.todoapp.models.Task;
import com.lesstaxi.todoapp.payload.request.TaskRequest;
import com.lesstaxi.todoapp.payload.response.MessageResponse;
import com.lesstaxi.todoapp.security.services.UserDetailsImpl;
import com.lesstaxi.todoapp.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(taskService.getAllTasks(userDetails));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest taskRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(taskService.createTask(taskRequest, userDetails));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable String id, @RequestBody TaskRequest taskRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(taskService.updateTaskStatus(id, taskRequest, userDetails));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody TaskRequest taskRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTask(@PathVariable String id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        taskService.deleteTask(id, userDetails);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully."));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Task> assignTask(@PathVariable String id, @RequestBody TaskRequest taskRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(taskService.assignTask(id, taskRequest, userDetails));
    }
}
