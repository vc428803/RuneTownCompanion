package com.runetown.lifeprogression.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.runetown.lifeprogression.domain.goal.Goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Life Progression")
public class GoalCompletionController {

    private final GoalRegistry goalRegistry;

    public GoalCompletionController(GoalRegistry goalRegistry) {
        this.goalRegistry = goalRegistry;
    }

    @PostMapping("/{goalId}/completion")
    @Operation(
            summary = "Complete a goal",
            description = "Completes a READY_TO_COMPLETE goal and returns "
                    + "the authoritative GoalStatus.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goal completed"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Goal is not ready to complete")
    })
    public GoalCompletionResponse completeGoal(
            @Parameter(example = "goal-demo")
            @PathVariable("goalId") String goalId) {

        Goal goal = goalRegistry.findGoal(goalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Goal not found"))
                .goal();

        try {
            goal.complete();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage(),
                    exception);
        }

        goalRegistry.save(goal);

        return new GoalCompletionResponse(goal.getId(), goal.getStatus());
    }
}
