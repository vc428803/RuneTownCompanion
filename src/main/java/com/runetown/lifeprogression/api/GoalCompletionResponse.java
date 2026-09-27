package com.runetown.lifeprogression.api;

import com.runetown.lifeprogression.domain.goal.GoalStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record GoalCompletionResponse(
        @Schema(example = "goal-demo")
        String goalId,

        @Schema(example = "COMPLETED")
        GoalStatus goalStatus) {
}
