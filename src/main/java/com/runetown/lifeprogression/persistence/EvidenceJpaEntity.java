package com.runetown.lifeprogression.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "evidence")
class EvidenceJpaEntity {

    @Id
    @Column(name = "criterion_id", nullable = false, length = 100)
    private String criterionId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criterion_id", nullable = false)
    private CompletionCriterionJpaEntity criterion;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "source", length = 1000)
    private String source;

    protected EvidenceJpaEntity() {
    }

    EvidenceJpaEntity(
            CompletionCriterionJpaEntity criterion,
            String description,
            String source) {
        this.criterion = criterion;
        this.description = description;
        this.source = source;
    }

    void replaceState(String description, String source) {
        this.description = description;
        this.source = source;
    }

    String getDescription() {
        return description;
    }

    String getSource() {
        return source;
    }
}
