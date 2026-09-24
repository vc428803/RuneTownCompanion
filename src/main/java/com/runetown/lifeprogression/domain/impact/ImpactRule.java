package com.runetown.lifeprogression.domain.impact;

import java.util.Set;

import com.runetown.lifeprogression.domain.milestone.LifeDimension;

public class ImpactRule {

    // 哪些人生面向會觸發這條規則
    private final Set<LifeDimension> requiredDimensions;

    // 這條規則最後影響哪些遊戲系統
    private final Set<GameImpactType> impactTypes;

    public ImpactRule(
            Set<LifeDimension> requiredDimensions,
            Set<GameImpactType> impactTypes) {

        this.requiredDimensions = Set.copyOf(requiredDimensions);
        this.impactTypes = Set.copyOf(impactTypes);
    }

    // Milestone 是否符合這條 ImpactRule
    public boolean matches(Set<LifeDimension> milestoneDimensions) {
        return milestoneDimensions.containsAll(requiredDimensions);
    }

    public Set<GameImpactType> getImpactTypes() {
        return impactTypes;
    }
}