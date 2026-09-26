package com.runetown.lifeprogression.application;

import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.evidence.EvidenceCandidate;
import com.runetown.lifeprogression.domain.evidence.EvidenceQualificationPolicy;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;

public class ProcessEvidenceCandidateService {

    private final EvidenceQualificationPolicy qualificationPolicy;

    public ProcessEvidenceCandidateService(
            EvidenceQualificationPolicy qualificationPolicy) {

        this.qualificationPolicy = qualificationPolicy;
    }

    public boolean process(
            EvidenceCandidate candidate,
            CollectionEntry collectionEntry) {

        if (!qualificationPolicy.qualifies(candidate)) {
            return false;
        }

        Evidence evidence = new Evidence(
                candidate.getDescription(),
                candidate.getSource()
        );

        collectionEntry.addEvidence(evidence);

        return true;
    }

    public boolean process(
            EvidenceCandidate candidate,
            CollectionEntry collectionEntry,
            Goal goal,
            CompletionCriterion criterion) {

        if (collectionEntry == null) {
            throw new IllegalArgumentException(
                    "Collection entry cannot be null");
        }

        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }

        if (criterion == null) {
            throw new IllegalArgumentException(
                    "Completion criterion cannot be null");
        }

        if (!qualificationPolicy.qualifies(candidate)) {
            return false;
        }

        Evidence evidence = new Evidence(
                candidate.getDescription(),
                candidate.getSource()
        );

        goal.satisfyCriterionWithEvidence(criterion, evidence);
        collectionEntry.addEvidence(evidence);

        return true;
    }
}
