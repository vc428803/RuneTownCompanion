package com.runetown.lifeprogression.domain.goal;

import com.runetown.lifeprogression.domain.evidence.Evidence;

public class CompletionCriterion {

    private final String description;

    // 紀錄這個完成條件是否已被滿足
    private boolean completed;
    private Evidence supportingEvidence;

    // 這樣一來,Goal 類別就可以透過檢查 completionCriteria 這個清單裡,是否每一個 CompletionCriterion 的 isCompleted() 都回傳 true,來判斷整個目標是否完成。
    public CompletionCriterion(String description) {

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Completion criterion description cannot be blank");
        }

        this.description = description;
        this.completed = false;
        this.supportingEvidence = null;
    }

    public void complete() {

        // 第一版先單純標記為完成
        this.completed = true;
    }

    public void satisfyWith(Evidence evidence) {

        if (completed) {
            throw new IllegalStateException(
                    "Completion criterion is already completed");
        }

        if (evidence == null) {
            throw new IllegalArgumentException(
                    "Supporting evidence cannot be null");
        }

        this.supportingEvidence = evidence;
        this.completed = true;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Evidence getSupportingEvidence() {
        return supportingEvidence;
    }
}
