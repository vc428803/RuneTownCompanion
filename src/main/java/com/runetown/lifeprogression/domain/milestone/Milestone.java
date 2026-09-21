package com.runetown.lifeprogression.domain.milestone;

import java.util.Collections;
import java.util.Set;

import com.runetown.lifeprogression.domain.goal.LifeArchetype;

public class Milestone {

    private final String id;

    // 這個 Milestone 是從哪一個 Goal 產生
    private final String goalId;

    private final String title;

    // 人生大方向，例如 CREATOR / EXPLORER
    private final LifeArchetype archetype;

    // 這個 Milestone 實際影響哪些人生面向
    private final Set<LifeDimension> dimensions;

    // 代表這個 Milestone 是否已正式達成
    private boolean achieved;

    public Milestone(
            String id,
            String goalId,
            String title,
            LifeArchetype archetype,
            Set<LifeDimension> dimensions) {

        this.id = id;
        this.goalId = goalId;
        this.title = title;
        this.archetype = archetype;

        // copyOf 防禦性拷貝，唯讀，不可 .add() 或 .remove()
        this.dimensions = Set.copyOf(dimensions);

        this.achieved = false;
    }

    // Goal 正式完成後，再把 Milestone 標記成已達成
    public void achieve() {
        this.achieved = true;
    }

    public String getId() {
        return id;
    }

    public String getGoalId() {
        return goalId;
    }

    public String getTitle() {
        return title;
    }

    public LifeArchetype getArchetype() {
        return archetype;
    }

    public Set<LifeDimension> getDimensions() {
        return Collections.unmodifiableSet(dimensions);
    }

    public boolean isAchieved() {
        return achieved;
    }
}