package com.runetown.lifeprogression.persistence;

import java.util.ArrayList;
import java.util.List;

import com.runetown.lifeprogression.domain.goal.GoalStatus;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "goals")
class GoalJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "archetype", nullable = false, length = 32)
    private LifeArchetype archetype;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private GoalStatus status;

    @OneToMany(
            mappedBy = "goal",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("position ASC")
    private List<CompletionCriterionJpaEntity> criteria = new ArrayList<>();

    protected GoalJpaEntity() {
    }

    GoalJpaEntity(
            String id,
            String title,
            LifeArchetype archetype,
            GoalStatus status) {
        this.id = id;
        this.title = title;
        this.archetype = archetype;
        this.status = status;
    }

    void addCriterion(CompletionCriterionJpaEntity criterion) {
        criteria.add(criterion);
        criterion.attachTo(this);
    }

    String getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    LifeArchetype getArchetype() {
        return archetype;
    }

    GoalStatus getStatus() {
        return status;
    }

    void setStatus(GoalStatus status) {
        this.status = status;
    }

    List<CompletionCriterionJpaEntity> getCriteria() {
        return criteria;
    }
}
