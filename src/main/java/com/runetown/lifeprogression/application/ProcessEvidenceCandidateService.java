package com.runetown.lifeprogression.application;

import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.evidence.EvidenceCandidate;
import com.runetown.lifeprogression.domain.evidence.EvidenceQualificationPolicy;

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
}