package com.runetown.lifeprogression.api;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

@Component
public class InMemoryGoalRegistry {

    private final Map<String, RegisteredGoal> goals =
            new ConcurrentHashMap<>();

    public InMemoryGoalRegistry() {
        Goal demoGoal = new Goal(
                "goal-demo",
                "Publish the first article",
                LifeArchetype.CREATOR);
        CompletionCriterion demoCriterion =
                new CompletionCriterion("Publish the article");
        demoGoal.addCompletionCriterion(demoCriterion);

        register(
                demoGoal,
                new CollectionEntry(
                        "collection-demo",
                        "Published work",
                        "Evidence collected for the demo goal",
                        LocalDate.now()),
                Map.of("criterion-demo", demoCriterion));
    }

    public void register(
            Goal goal,
            CollectionEntry collectionEntry,
            Map<String, CompletionCriterion> criteriaById) {

        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }

        if (collectionEntry == null) {
            throw new IllegalArgumentException(
                    "Collection entry cannot be null");
        }

        if (criteriaById == null || criteriaById.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one completion criterion is required");
        }

        criteriaById.forEach((criterionId, criterion) -> {
            if (criterionId == null || criterionId.isBlank()) {
                throw new IllegalArgumentException(
                        "Completion criterion id cannot be blank");
            }

            if (!goal.getCompletionCriteria().contains(criterion)) {
                throw new IllegalArgumentException(
                        "Completion criterion does not belong to this goal");
            }
        });

        RegisteredGoal previous = goals.putIfAbsent(
                goal.getId(),
                new RegisteredGoal(
                        goal,
                        collectionEntry,
                        Map.copyOf(criteriaById)));

        if (previous != null) {
            throw new IllegalStateException(
                    "Goal is already registered: " + goal.getId());
        }
    }

    public Optional<GoalCriterionTarget> find(
            String goalId,
            String criterionId) {

        RegisteredGoal registeredGoal = goals.get(goalId);
        if (registeredGoal == null) {
            return Optional.empty();
        }

        CompletionCriterion criterion =
                registeredGoal.criteriaById().get(criterionId);
        if (criterion == null) {
            return Optional.empty();
        }

        return Optional.of(new GoalCriterionTarget(
                registeredGoal.goal(),
                criterion,
                registeredGoal.collectionEntry()));
    }

    private record RegisteredGoal(
            Goal goal,
            CollectionEntry collectionEntry,
            Map<String, CompletionCriterion> criteriaById) {
    }

    public record GoalCriterionTarget(
            Goal goal,
            CompletionCriterion criterion,
            CollectionEntry collectionEntry) {
    }
}
