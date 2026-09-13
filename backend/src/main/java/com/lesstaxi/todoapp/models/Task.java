package com.lesstaxi.todoapp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "tasks")
public class Task {
  @Id
  private String id;

  private String title;
  private String description;
  private EStatus status = EStatus.TODO;
  
  private String creatorId;
  private String assignedUserId;
  
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Task() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public EStatus getStatus() { return status; }
  public void setStatus(EStatus status) { this.status = status; }
  public String getCreatorId() { return creatorId; }
  public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
  public String getAssignedUserId() { return assignedUserId; }
  public void setAssignedUserId(String assignedUserId) { this.assignedUserId = assignedUserId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
