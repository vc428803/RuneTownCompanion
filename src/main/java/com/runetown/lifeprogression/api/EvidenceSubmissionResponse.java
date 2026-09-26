package com.runetown.lifeprogression.api;

import com.runetown.lifeprogression.domain.goal.GoalStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record EvidenceSubmissionResponse(
        @Schema(example = "true")
        boolean accepted,

        @Schema(example = "goal-demo")
        String goalId,

        @Schema(example = "criterion-demo")
        String criterionId,

        @Schema(example = "true")
        boolean criterionCompleted,

        @Schema(example = "READY_TO_COMPLETE")
        GoalStatus goalStatus) {
}
