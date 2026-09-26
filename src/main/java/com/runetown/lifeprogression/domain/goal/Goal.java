package com.runetown.lifeprogression.domain.goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.milestone.LifeDimension;
import com.runetown.lifeprogression.domain.milestone.Milestone;

public class Goal {

    private final String id;
    private final String title;
    private final LifeArchetype archetype;

    // Goal 目前所處的生命週期狀態
    private GoalStatus status;
    // Goal (目標)
    // └── completionCriteria: List<CompletionCriterion>
    // ├── CompletionCriterion #1 "跑完一場馬拉松"
    // ├── CompletionCriterion #2 "讀完10本書"
    // └── CompletionCriterion #3 "存到10萬元"
    // final 代表這個清單的參照一旦被賦值後不能再換成另一個清單(但清單裡的內容還是可以新增/刪除)
    private final List<CompletionCriterion> completionCriteria;

    public Goal(String id, String title, LifeArchetype archetype) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Goal id cannot be blank");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Goal title cannot be blank");
        }

        if (archetype == null) {
            throw new IllegalArgumentException("Life archetype cannot be null");
        }

        this.id = id;
        this.title = title;
        this.archetype = archetype;

        // 新建立的 Goal 預設為進行中
        this.status = GoalStatus.ACTIVE;

        this.completionCriteria = new ArrayList<>();
    }

    public void addCompletionCriterion(CompletionCriterion criterion) {

        if (criterion == null) {
            throw new IllegalArgumentException(
                    "Completion criterion cannot be null");
        }

        completionCriteria.add(criterion);

        // 新增條件後重新判斷 Goal 狀態
        evaluateCompletionStatus();
    }

    public void satisfyCriterionWithEvidence(
            CompletionCriterion criterion,
            Evidence evidence) {

        if (criterion == null) {
            throw new IllegalArgumentException(
                    "Completion criterion cannot be null");
        }

        if (evidence == null) {
            throw new IllegalArgumentException(
                    "Evidence cannot be null");
        }

        if (!completionCriteria.contains(criterion)) {
            throw new IllegalArgumentException(
                    "Completion criterion does not belong to this goal");
        }

        criterion.satisfyWith(evidence);
        evaluateCompletionStatus();
    }

    public void evaluateCompletionStatus() {

        // 已完成、已放棄、暫停中的 Goal
        // 不讓系統自動重新改變它的狀態
        if (status == GoalStatus.COMPLETED
                || status == GoalStatus.ABANDONED
                || status == GoalStatus.PAUSED) {
            return;
        }

        // 沒有任何完成條件時，
        // 不允許 Goal 自動進入 READY_TO_COMPLETE
        if (completionCriteria.isEmpty()) {
            status = GoalStatus.ACTIVE;
            return;
        }

        // 核心 Business Rule：
        // 所有 CompletionCriterion 都完成，
        // Goal 才具有「可完成資格」
        boolean allCompleted = completionCriteria.stream()
                .allMatch(CompletionCriterion::isCompleted);

        status = allCompleted
                ? GoalStatus.READY_TO_COMPLETE
                : GoalStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LifeArchetype getArchetype() {
        return archetype;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public List<CompletionCriterion> getCompletionCriteria() {

        // 不直接暴露可修改的 List，
        // 避免外部任意新增 / 刪除 criterion
        return Collections.unmodifiableList(completionCriteria);
    }

    // 正式完成 Goal
    public void complete() {

        if (status != GoalStatus.READY_TO_COMPLETE) {
            throw new IllegalStateException(
                    "Goal must be READY_TO_COMPLETE before completion");
        }

        status = GoalStatus.COMPLETED;
    }

    // 將已完成的 Goal 轉成 Milestone
    public Milestone createMilestone(
            String milestoneId,
            Set<LifeDimension> dimensions) {

        // 只有正式完成的 Goal 才能建立 Milestone
        if (status != GoalStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Only completed goals can create milestones");
        }

        return new Milestone(
                milestoneId,
                id,
                title,
                archetype,
                dimensions);
    }
}
