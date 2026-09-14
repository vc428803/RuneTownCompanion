package com.runetown.lifeprogression.domain.evidence;

public interface EvidenceQualificationPolicy {

    boolean qualifies(EvidenceCandidate candidate);
}