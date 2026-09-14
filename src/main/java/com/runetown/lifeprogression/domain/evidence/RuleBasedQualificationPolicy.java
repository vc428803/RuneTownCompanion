package com.runetown.lifeprogression.domain.evidence;

public class RuleBasedQualificationPolicy
        implements EvidenceQualificationPolicy {

    @Override
    public boolean qualifies(EvidenceCandidate candidate) {
        return candidate != null
                && candidate.getTitle() != null
                && !candidate.getTitle().isBlank()
                && candidate.getDescription() != null
                && !candidate.getDescription().isBlank();
    }
}