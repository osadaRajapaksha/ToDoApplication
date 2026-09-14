package com.lesstaxi.todoapp.controllers;

import com.lesstaxi.todoapp.payload.request.AiBreakdownRequest;
import com.lesstaxi.todoapp.payload.response.AiBreakdownResponse;
import com.lesstaxi.todoapp.payload.response.MessageResponse;
import com.lesstaxi.todoapp.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    GeminiService geminiService;

    @PostMapping("/breakdown")
    public ResponseEntity<?> breakDownTask(@RequestBody AiBreakdownRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Task title is required."));
        }

        String subtasks = geminiService.generateSubtasks(request.getTitle(), request.getDescription() != null ? request.getDescription() : "");
        
        return ResponseEntity.ok(new AiBreakdownResponse(subtasks));
    }
}
