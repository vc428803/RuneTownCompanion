package com.runetown.lifeprogression.api;

import java.util.List;

import com.runetown.lifeprogression.domain.goal.GoalStatus;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

import io.swagger.v3.oas.annotations.media.Schema;

public record GoalDetailResponse(
        @Schema(example = "goal-demo")
        String goalId,

        @Schema(example = "Publish the first article")
        String title,

        @Schema(example = "CREATOR")
        LifeArchetype lifeArchetype,

        @Schema(example = "ACTIVE")
        GoalStatus goalStatus,

        List<CriterionSummaryResponse> criteria) {

    public record CriterionSummaryResponse(
            @Schema(example = "criterion-demo")
            String criterionId,

            @Schema(example = "Publish the article")
            String description,

            @Schema(example = "false")
            boolean completed) {
    }
}
