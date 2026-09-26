package com.runetown.lifeprogression.api;

import io.swagger.v3.oas.annotations.media.Schema;

public record EvidenceSubmissionRequest(
        @Schema(
                example = "Published the first article",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(
                example = "The article is now publicly available",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @Schema(
                example = "https://example.com/articles/first",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String source) {
}
