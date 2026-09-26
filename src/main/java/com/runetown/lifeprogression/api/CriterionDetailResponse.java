package com.runetown.lifeprogression.api;

import io.swagger.v3.oas.annotations.media.Schema;

public record CriterionDetailResponse(
        @Schema(example = "criterion-demo")
        String criterionId,

        @Schema(example = "Publish the article")
        String description,

        @Schema(example = "false")
        boolean completed,

        SupportingEvidenceResponse supportingEvidence) {

    public record SupportingEvidenceResponse(
            @Schema(example = "The article is now publicly available")
            String description,

            @Schema(example = "https://example.com/articles/first")
            String source) {
    }
}
