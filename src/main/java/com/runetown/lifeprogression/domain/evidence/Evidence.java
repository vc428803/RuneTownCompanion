package com.runetown.lifeprogression.domain.evidence;

public class Evidence {

    private final String description;
    private final String source;

    public Evidence(String description, String source) {
        this.description = description;
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }
}