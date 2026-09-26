package com.runetown.lifeprogression.api;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.runetown.lifeprogression.application.ProcessEvidenceCandidateService;
import com.runetown.lifeprogression.domain.evidence.EvidenceCandidate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Life Progression")
public class GoalEvidenceController {

    private final InMemoryGoalRegistry goalRegistry;
    private final ProcessEvidenceCandidateService evidenceService;

    public GoalEvidenceController(
            InMemoryGoalRegistry goalRegistry,
            ProcessEvidenceCandidateService evidenceService) {

        this.goalRegistry = goalRegistry;
        this.evidenceService = evidenceService;
    }

    @PostMapping("/{goalId}/criteria/{criterionId}/evidence")
    @Operation(
            summary = "Submit evidence for a completion criterion",
            description = "Qualifies evidence, satisfies the selected criterion, "
                    + "and returns the latest GoalStatus.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evidence accepted"),
            @ApiResponse(responseCode = "404", description = "Goal or criterion not found"),
            @ApiResponse(responseCode = "409", description = "Criterion already completed"),
            @ApiResponse(responseCode = "422", description = "Evidence did not qualify")
    })
    public ResponseEntity<EvidenceSubmissionResponse> submitEvidence(
            @Parameter(example = "goal-demo")
            @PathVariable("goalId") String goalId,
            @Parameter(example = "criterion-demo")
            @PathVariable("criterionId") String criterionId,
            @RequestBody EvidenceSubmissionRequest request) {

        InMemoryGoalRegistry.GoalCriterionTarget target = goalRegistry
                .find(goalId, criterionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Goal or completion criterion not found"));

        boolean accepted;
        try {
            accepted = evidenceService.process(
                    new EvidenceCandidate(
                            request.title(),
                            request.description(),
                            request.source(),
                            LocalDateTime.now()),
                    target.collectionEntry(),
                    target.goal(),
                    target.criterion());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage(),
                    exception);
        }

        EvidenceSubmissionResponse response = new EvidenceSubmissionResponse(
                accepted,
                goalId,
                criterionId,
                target.criterion().isCompleted(),
                target.goal().getStatus());

        return accepted
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(response);
    }
}
