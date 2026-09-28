package com.runetown.lifeprogression.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;

public interface GoalRegistry {

    void register(
            Goal goal,
            CollectionEntry collectionEntry,
            Map<String, CompletionCriterion> criteriaById);

    Optional<InMemoryGoalRegistry.GoalCriterionTarget> find(
            String goalId,
            String criterionId);

    List<InMemoryGoalRegistry.GoalTarget> findAllGoals();

    Optional<InMemoryGoalRegistry.GoalTarget> findGoal(String goalId);

    default void save(Goal goal) {
        // In-memory aggregates are already updated by reference.
    }
}
