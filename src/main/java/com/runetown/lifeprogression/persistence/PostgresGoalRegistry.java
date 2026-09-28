package com.runetown.lifeprogression.persistence;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.runetown.lifeprogression.api.GoalRegistry;
import com.runetown.lifeprogression.api.InMemoryGoalRegistry;
import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;

@Component
@Primary
@Profile("!in-memory")
@Transactional
public class PostgresGoalRegistry implements GoalRegistry {

    private final GoalJpaRepository repository;
    private final GoalPersistenceMapper mapper = new GoalPersistenceMapper();

    public PostgresGoalRegistry(GoalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void register(
            Goal goal,
            CollectionEntry collectionEntry,
            Map<String, CompletionCriterion> criteriaById) {
        requireCollectionEntry(collectionEntry);
        if (repository.existsById(goal.getId())) {
            throw new IllegalStateException(
                    "Goal is already registered: " + goal.getId());
        }
        repository.saveAndFlush(mapper.toEntity(goal, criteriaById));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InMemoryGoalRegistry.GoalCriterionTarget> find(
            String goalId,
            String criterionId) {
        return repository.findAggregateById(goalId)
                .map(mapper::toDomain)
                .map(target -> toCriterionTarget(target, criterionId))
                .orElseGet(Optional::empty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InMemoryGoalRegistry.GoalTarget> findAllGoals() {
        return repository.findAllAggregates().stream()
                .map(mapper::toDomain)
                .sorted(Comparator.comparing(target -> target.goal().getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InMemoryGoalRegistry.GoalTarget> findGoal(String goalId) {
        return repository.findAggregateById(goalId).map(mapper::toDomain);
    }

    @Override
    public void save(Goal goal) {
        GoalJpaEntity entity = repository.findAggregateById(goal.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Goal is not registered: " + goal.getId()));
        mapper.updateEntity(goal, entity);
        repository.saveAndFlush(entity);
    }

    private Optional<InMemoryGoalRegistry.GoalCriterionTarget>
            toCriterionTarget(
                    InMemoryGoalRegistry.GoalTarget target,
                    String criterionId) {
        CompletionCriterion criterion = target.criteriaById().get(criterionId);
        if (criterion == null) {
            return Optional.empty();
        }
        return Optional.of(new InMemoryGoalRegistry.GoalCriterionTarget(
                target.goal(),
                criterion,
                transientCollection(target.goal().getId())));
    }

    private CollectionEntry transientCollection(String goalId) {
        return new CollectionEntry(
                "collection-" + goalId,
                "Goal evidence",
                "Transient collection view for persisted Goal evidence",
                LocalDate.now());
    }

    private void requireCollectionEntry(CollectionEntry collectionEntry) {
        if (collectionEntry == null) {
            throw new IllegalArgumentException(
                    "Collection entry cannot be null");
        }
    }
}
