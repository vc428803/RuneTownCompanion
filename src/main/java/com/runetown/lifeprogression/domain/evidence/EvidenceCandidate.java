package com.runetown.lifeprogression.domain.evidence;

import java.time.LocalDateTime;

public class EvidenceCandidate {

    private final String title;
    private final String description;
    private final String source;
    private final LocalDateTime occurredAt;

    public EvidenceCandidate(
            String title,
            String description,
            String source,
            LocalDateTime occurredAt) {

        this.title = title;
        this.description = description;
        this.source = source;
        this.occurredAt = occurredAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}