package com.runetown.lifeprogression.domain.impact;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.runetown.lifeprogression.domain.milestone.LifeDimension;

class ImpactRuleTest {

    @Test
    void shouldMatchOnlyWhenMilestoneHasAllRequiredDimensions() {
        ImpactRule rule = new ImpactRule(
                Set.of(LifeDimension.KNOWLEDGE, LifeDimension.CREATION),
                Set.of(GameImpactType.PLAYER_PROGRESSION));

        assertTrue(rule.matches(Set.of(
                LifeDimension.KNOWLEDGE,
                LifeDimension.CREATION)));
        assertFalse(rule.matches(Set.of(LifeDimension.KNOWLEDGE)));
    }
}
