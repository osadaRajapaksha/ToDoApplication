package com.lesstaxi.todoapp.services;

import com.lesstaxi.todoapp.exceptions.ResourceNotFoundException;
import com.lesstaxi.todoapp.exceptions.UnauthorizedException;
import com.lesstaxi.todoapp.models.EStatus;
import com.lesstaxi.todoapp.models.Task;
import com.lesstaxi.todoapp.payload.request.TaskRequest;
import com.lesstaxi.todoapp.repositories.TaskRepository;
import com.lesstaxi.todoapp.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks(UserDetailsImpl userDetails) {
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_ADMIN".equals(role)) {
            return taskRepository.findAll();
        } else {
            return taskRepository.findByCreatorIdOrAssignedUserId(userDetails.getId(), userDetails.getId());
        }
    }

    public Task createTask(TaskRequest taskRequest, UserDetailsImpl userDetails) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCreatorId(userDetails.getId());
        
        if (taskRequest.getAssigneeId() != null && taskRequest.getAssigneeId().equals(userDetails.getId())) {
            task.setAssignedUserId(userDetails.getId());
        }
        
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(String id, TaskRequest taskRequest, UserDetailsImpl userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        boolean isCreator = task.getCreatorId() != null && task.getCreatorId().equals(userDetails.getId());
        boolean isAssignee = task.getAssignedUserId() != null && task.getAssignedUserId().equals(userDetails.getId());

        if (!"ROLE_ADMIN".equals(role) && !isCreator && !isAssignee) {
            throw new UnauthorizedException("Unauthorized to update this task's status.");
        }

        try {
            task.setStatus(EStatus.valueOf(taskRequest.getStatus()));
            task.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(task);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status provided.");
        }
    }

    public Task updateTask(String id, TaskRequest taskRequest, UserDetailsImpl userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        boolean isCreator = task.getCreatorId() != null && task.getCreatorId().equals(userDetails.getId());

        if (!"ROLE_ADMIN".equals(role) && !isCreator) {
            throw new UnauthorizedException("Unauthorized to update this task.");
        }

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public void deleteTask(String id, UserDetailsImpl userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        boolean isCreator = task.getCreatorId() != null && task.getCreatorId().equals(userDetails.getId());

        if (!"ROLE_ADMIN".equals(role) && !isCreator) {
            throw new UnauthorizedException("Unauthorized to delete this task.");
        }

        taskRepository.deleteById(id);
    }

    public Task assignTask(String id, TaskRequest taskRequest, UserDetailsImpl userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String assigneeId = taskRequest.getAssigneeId();

        if ("ROLE_ADMIN".equals(role)) {
            task.setAssignedUserId(assigneeId);
        } else {
            if ((task.getAssignedUserId() == null || task.getAssignedUserId().isEmpty()) && assigneeId.equals(userDetails.getId())) {
                task.setAssignedUserId(assigneeId);
            } else {
                throw new UnauthorizedException("Unauthorized to assign this task.");
            }
        }
        
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
}
