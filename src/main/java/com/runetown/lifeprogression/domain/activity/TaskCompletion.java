package com.runetown.lifeprogression.domain.activity;

import java.time.LocalDateTime;

public class TaskCompletion {

    private final String taskId;
    private final String title;
    private final LocalDateTime completedAt;

    public TaskCompletion(
            String taskId,
            String title,
            LocalDateTime completedAt) {

        this.taskId = taskId;
        this.title = title;
        this.completedAt = completedAt;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}