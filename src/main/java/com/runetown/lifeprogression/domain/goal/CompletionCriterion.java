package com.runetown.lifeprogression.domain.goal;

public class CompletionCriterion {

    private final String description;
    private boolean completed;

    public CompletionCriterion(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Completion criterion description cannot be blank");
        }

        this.description = description;
        this.completed = false;
    }

    public void complete() {
        this.completed = true;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }
}