package com.runetown.lifeprogression.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "completion_criteria")
class CompletionCriterionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goal_id", nullable = false)
    private GoalJpaEntity goal;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "position", nullable = false)
    private int position;

    @OneToOne(
            mappedBy = "criterion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private EvidenceJpaEntity evidence;

    protected CompletionCriterionJpaEntity() {
    }

    CompletionCriterionJpaEntity(
            String id,
            String description,
            boolean completed,
            int position) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.position = position;
    }

    void attachTo(GoalJpaEntity goal) {
        this.goal = goal;
    }

    void replaceState(boolean completed, String evidenceDescription, String source) {
        this.completed = completed;
        if (evidenceDescription == null && source == null) {
            evidence = null;
            return;
        }
        if (evidence == null) {
            evidence = new EvidenceJpaEntity(this, evidenceDescription, source);
        } else {
            evidence.replaceState(evidenceDescription, source);
        }
    }

    String getId() {
        return id;
    }

    String getDescription() {
        return description;
    }

    boolean isCompleted() {
        return completed;
    }

    int getPosition() {
        return position;
    }

    EvidenceJpaEntity getEvidence() {
        return evidence;
    }
}
