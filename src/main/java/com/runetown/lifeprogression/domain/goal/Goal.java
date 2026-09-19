package com.runetown.lifeprogression.domain.goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Goal {

    private final String id;
    private final String title;
    private final LifeArchetype archetype;

    private GoalStatus status;

    private final List<CompletionCriterion> completionCriteria;

    public Goal(String id, String title, LifeArchetype archetype) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Goal id cannot be blank");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Goal title cannot be blank");
        }

        if (archetype == null) {
            throw new IllegalArgumentException("Life archetype cannot be null");
        }

        this.id = id;
        this.title = title;
        this.archetype = archetype;
        this.status = GoalStatus.ACTIVE;
        this.completionCriteria = new ArrayList<>();
    }

    public void addCompletionCriterion(CompletionCriterion criterion) {
        if (criterion == null) {
            throw new IllegalArgumentException(
                    "Completion criterion cannot be null");
        }

        completionCriteria.add(criterion);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LifeArchetype getArchetype() {
        return archetype;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public List<CompletionCriterion> getCompletionCriteria() {
        return Collections.unmodifiableList(completionCriteria);
    }
}