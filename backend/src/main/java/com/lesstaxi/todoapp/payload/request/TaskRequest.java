package com.lesstaxi.todoapp.payload.request;

public class TaskRequest {
  private String title;
  private String description;
  private String status;
  private String assigneeId;

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getAssigneeId() { return assigneeId; }
  public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }
}
