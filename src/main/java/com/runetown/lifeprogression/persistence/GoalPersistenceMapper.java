package com.runetown.lifeprogression.persistence;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.runetown.lifeprogression.api.InMemoryGoalRegistry;
import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;
import com.runetown.lifeprogression.domain.goal.GoalStatus;

final class GoalPersistenceMapper {

    GoalJpaEntity toEntity(
            Goal goal,
            Map<String, CompletionCriterion> criteriaById) {
        Map<CompletionCriterion, String> idsByCriterion =
                criterionIdsByIdentity(goal, criteriaById);
        GoalJpaEntity entity = new GoalJpaEntity(
                goal.getId(),
                goal.getTitle(),
                goal.getArchetype(),
                goal.getStatus());

        List<CompletionCriterion> criteria = goal.getCompletionCriteria();
        for (int position = 0; position < criteria.size(); position++) {
            CompletionCriterion criterion = criteria.get(position);
            CompletionCriterionJpaEntity criterionEntity =
                    new CompletionCriterionJpaEntity(
                            idsByCriterion.get(criterion),
                            criterion.getDescription(),
                            criterion.isCompleted(),
                            position);
            copyEvidenceState(criterion, criterionEntity);
            entity.addCriterion(criterionEntity);
        }
        return entity;
    }

    void updateEntity(Goal goal, GoalJpaEntity entity) {
        if (!entity.getId().equals(goal.getId())) {
            throw new IllegalArgumentException("Goal id cannot be changed");
        }
        if (!entity.getTitle().equals(goal.getTitle())
                || entity.getArchetype() != goal.getArchetype()) {
            throw new IllegalStateException(
                    "Persisted Goal identity fields do not match the domain object");
        }

        List<CompletionCriterion> criteria = goal.getCompletionCriteria();
        List<CompletionCriterionJpaEntity> entities = entity.getCriteria();
        if (criteria.size() != entities.size()) {
            throw new IllegalStateException(
                    "Completion criteria cannot be added or removed during an update");
        }

        for (int position = 0; position < criteria.size(); position++) {
            CompletionCriterion criterion = criteria.get(position);
            CompletionCriterionJpaEntity criterionEntity = entities.get(position);
            if (criterionEntity.getPosition() != position
                    || !criterionEntity.getDescription()
                            .equals(criterion.getDescription())) {
                throw new IllegalStateException(
                        "Completion criterion order or description has changed");
            }
            copyEvidenceState(criterion, criterionEntity);
        }
        entity.setStatus(goal.getStatus());
    }

    InMemoryGoalRegistry.GoalTarget toDomain(GoalJpaEntity entity) {
        Goal goal = new Goal(
                entity.getId(),
                entity.getTitle(),
                entity.getArchetype());
        Map<String, CompletionCriterion> criteriaById = new LinkedHashMap<>();

        for (CompletionCriterionJpaEntity criterionEntity
                : entity.getCriteria()) {
            CompletionCriterion criterion = new CompletionCriterion(
                    criterionEntity.getDescription());
            goal.addCompletionCriterion(criterion);
            criteriaById.put(criterionEntity.getId(), criterion);
        }

        for (CompletionCriterionJpaEntity criterionEntity
                : entity.getCriteria()) {
            if (!criterionEntity.isCompleted()) {
                if (criterionEntity.getEvidence() != null) {
                    throw invalidState(entity, "incomplete criterion has evidence");
                }
                continue;
            }

            CompletionCriterion criterion = criteriaById.get(
                    criterionEntity.getId());
            EvidenceJpaEntity evidenceEntity = criterionEntity.getEvidence();
            if (evidenceEntity == null) {
                criterion.complete();
                goal.evaluateCompletionStatus();
            } else {
                goal.satisfyCriterionWithEvidence(
                        criterion,
                        new Evidence(
                                evidenceEntity.getDescription(),
                                evidenceEntity.getSource()));
            }
        }

        restoreAndValidateGoalStatus(entity, goal);
        return new InMemoryGoalRegistry.GoalTarget(
                goal,
                Collections.unmodifiableMap(criteriaById));
    }

    private Map<CompletionCriterion, String> criterionIdsByIdentity(
            Goal goal,
            Map<String, CompletionCriterion> criteriaById) {
        if (criteriaById == null) {
            throw new IllegalArgumentException(
                    "Completion criteria cannot be null");
        }
        Map<CompletionCriterion, String> idsByCriterion =
                new IdentityHashMap<>();
        criteriaById.forEach((id, criterion) -> {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException(
                        "Completion criterion id cannot be blank");
            }
            idsByCriterion.put(criterion, id);
        });
        if (idsByCriterion.size() != goal.getCompletionCriteria().size()
                || !idsByCriterion.keySet().containsAll(
                        goal.getCompletionCriteria())) {
            throw new IllegalArgumentException(
                    "Criterion ids must match the Goal completion criteria");
        }
        return idsByCriterion;
    }

    private void copyEvidenceState(
            CompletionCriterion criterion,
            CompletionCriterionJpaEntity entity) {
        Evidence evidence = criterion.getSupportingEvidence();
        entity.replaceState(
                criterion.isCompleted(),
                evidence == null ? null : evidence.getDescription(),
                evidence == null ? null : evidence.getSource());
    }

    private void restoreAndValidateGoalStatus(
            GoalJpaEntity entity,
            Goal goal) {
        if (entity.getStatus() == GoalStatus.COMPLETED) {
            if (goal.getStatus() != GoalStatus.READY_TO_COMPLETE) {
                throw invalidState(
                        entity,
                        "completed Goal is not ready after criterion rehydration");
            }
            goal.complete();
        }
        if (goal.getStatus() != entity.getStatus()) {
            throw invalidState(
                    entity,
                    "stored status does not match reconstructed domain state");
        }
    }

    private IllegalStateException invalidState(
            GoalJpaEntity entity,
            String reason) {
        return new IllegalStateException(
                "Invalid persisted Goal " + entity.getId() + ": " + reason);
    }
}
