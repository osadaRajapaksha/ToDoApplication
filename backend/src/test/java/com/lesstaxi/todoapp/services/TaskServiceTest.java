package com.lesstaxi.todoapp.services;

import com.lesstaxi.todoapp.exceptions.ResourceNotFoundException;
import com.lesstaxi.todoapp.exceptions.UnauthorizedException;
import com.lesstaxi.todoapp.models.Task;
import com.lesstaxi.todoapp.payload.request.TaskRequest;
import com.lesstaxi.todoapp.repositories.TaskRepository;
import com.lesstaxi.todoapp.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private UserDetailsImpl adminUser;
    private UserDetailsImpl normalUser;
    private Task task;

    @BeforeEach
    void setUp() {
        adminUser = new UserDetailsImpl("1", "admin", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        normalUser = new UserDetailsImpl("2", "user", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        task = new Task();
        task.setId("task1");
        task.setTitle("Test Task");
        task.setCreatorId("2");
    }

    @Test
    void testGetAllTasksAsAdmin() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks(adminUser);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTasksAsNormalUser() {
        when(taskRepository.findByCreatorIdOrAssignedUserId("2", "2")).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks(normalUser);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findByCreatorIdOrAssignedUserId("2", "2");
    }

    @Test
    void testCreateTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Desc");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask(request, normalUser);

        assertNotNull(created);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteTaskByCreator() {
        when(taskRepository.findById("task1")).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById("task1");

        assertDoesNotThrow(() -> taskService.deleteTask("task1", normalUser));
        verify(taskRepository, times(1)).deleteById("task1");
    }

    @Test
    void testDeleteTaskByAdmin() {
        when(taskRepository.findById("task1")).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById("task1");

        assertDoesNotThrow(() -> taskService.deleteTask("task1", adminUser));
        verify(taskRepository, times(1)).deleteById("task1");
    }

    @Test
    void testDeleteTaskUnauthorized() {
        UserDetailsImpl otherUser = new UserDetailsImpl("3", "other", "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        when(taskRepository.findById("task1")).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedException.class, () -> taskService.deleteTask("task1", otherUser));
        verify(taskRepository, never()).deleteById(anyString());
    }

    @Test
    void testUpdateTaskNotFound() {
        when(taskRepository.findById("invalidId")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> taskService.updateTask("invalidId", new TaskRequest(), normalUser));
    }
}
