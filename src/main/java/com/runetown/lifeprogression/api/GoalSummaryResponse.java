package com.runetown.lifeprogression.api;

import com.runetown.lifeprogression.domain.goal.GoalStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record GoalSummaryResponse(
        @Schema(example = "goal-demo")
        String goalId,

        @Schema(example = "Publish the first article")
        String title,

        @Schema(example = "ACTIVE")
        GoalStatus goalStatus,

        @Schema(example = "0")
        long completedCriteriaCount,

        @Schema(example = "1")
        int totalCriteriaCount) {
}
