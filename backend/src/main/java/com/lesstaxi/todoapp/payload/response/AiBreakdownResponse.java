package com.lesstaxi.todoapp.payload.response;

public class AiBreakdownResponse {
    private String subtasks;

    public AiBreakdownResponse(String subtasks) {
        this.subtasks = subtasks;
    }

    public String getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(String subtasks) {
        this.subtasks = subtasks;
    }
}
