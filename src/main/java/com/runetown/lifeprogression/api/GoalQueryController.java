package com.runetown.lifeprogression.api;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Life Progression")
public class GoalQueryController {

    private final InMemoryGoalRegistry goalRegistry;

    public GoalQueryController(InMemoryGoalRegistry goalRegistry) {
        this.goalRegistry = goalRegistry;
    }

    @GetMapping
    @Operation(summary = "List goals")
    public List<GoalSummaryResponse> getGoals() {
        return goalRegistry.findAllGoals().stream()
                .map(target -> toSummary(target.goal()))
                .toList();
    }

    @GetMapping("/{goalId}")
    @Operation(summary = "Get goal details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Goal found"),
            @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public GoalDetailResponse getGoal(
            @Parameter(example = "goal-demo")
            @PathVariable("goalId") String goalId) {

        InMemoryGoalRegistry.GoalTarget target = goalRegistry.findGoal(goalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Goal not found"));

        List<GoalDetailResponse.CriterionSummaryResponse> criteria =
                target.criteriaById().entrySet().stream()
                        .sorted(Comparator.comparing(entry -> entry.getKey()))
                        .map(entry -> new GoalDetailResponse.CriterionSummaryResponse(
                                entry.getKey(),
                                entry.getValue().getDescription(),
                                entry.getValue().isCompleted()))
                        .toList();

        Goal goal = target.goal();
        return new GoalDetailResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getArchetype(),
                goal.getStatus(),
                criteria);
    }

    @GetMapping("/{goalId}/criteria/{criterionId}")
    @Operation(summary = "Get completion criterion details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Criterion found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal or criterion not found")
    })
    public CriterionDetailResponse getCriterion(
            @Parameter(example = "goal-demo")
            @PathVariable("goalId") String goalId,
            @Parameter(example = "criterion-demo")
            @PathVariable("criterionId") String criterionId) {

        InMemoryGoalRegistry.GoalCriterionTarget target = goalRegistry
                .find(goalId, criterionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Goal or completion criterion not found"));

        CompletionCriterion criterion = target.criterion();
        Evidence evidence = criterion.getSupportingEvidence();
        CriterionDetailResponse.SupportingEvidenceResponse supportingEvidence =
                evidence == null
                        ? null
                        : new CriterionDetailResponse.SupportingEvidenceResponse(
                                evidence.getDescription(),
                                evidence.getSource());

        return new CriterionDetailResponse(
                criterionId,
                criterion.getDescription(),
                criterion.isCompleted(),
                supportingEvidence);
    }

    private GoalSummaryResponse toSummary(Goal goal) {
        long completedCriteriaCount = goal.getCompletionCriteria().stream()
                .filter(CompletionCriterion::isCompleted)
                .count();

        return new GoalSummaryResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getStatus(),
                completedCriteriaCount,
                goal.getCompletionCriteria().size());
    }
}
