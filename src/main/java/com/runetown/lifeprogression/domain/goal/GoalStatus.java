package com.runetown.lifeprogression.domain.goal;

public enum GoalStatus {
    ACTIVE,
    PAUSED,
    // 所有完成條件都已滿足，
    // 但尚未正式結算為 COMPLETED
    READY_TO_COMPLETE,
    // Goal 已正式完成
    COMPLETED,
    // Goal 已放棄
    ABANDONED
}