package com.runetown.lifeprogression.domain.collection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.runetown.lifeprogression.domain.evidence.Evidence;

public class CollectionEntry {

    private final String id;
    private final String title;
    private final String description;
    private final LocalDate acquiredAt;
    private final List<Evidence> evidences;

    public CollectionEntry(
            String id,
            String title,
            String description,
            LocalDate acquiredAt) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank");
        }

        this.id = id;
        this.title = title;
        this.description = description;
        this.acquiredAt = acquiredAt;
        this.evidences = new ArrayList<>();
    }

    public void addEvidence(Evidence evidence) {
        if (evidence == null) {
            throw new IllegalArgumentException("Evidence cannot be null");
        }

        this.evidences.add(evidence);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getAcquiredAt() {
        return acquiredAt;
    }

    public List<Evidence> getEvidences() {
        return Collections.unmodifiableList(evidences);
    }
}