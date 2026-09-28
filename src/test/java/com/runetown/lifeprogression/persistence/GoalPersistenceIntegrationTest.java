package com.runetown.lifeprogression.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.runetown.lifeprogression.api.GoalRegistry;
import com.runetown.lifeprogression.api.InMemoryGoalRegistry;
import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;
import com.runetown.lifeprogression.domain.goal.GoalStatus;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

@SpringBootTest
class GoalPersistenceIntegrationTest {

    private static final String GOAL_ID = "test-persistence-goal";
    private static final String CRITERION_ID = "test-persistence-criterion";

    @Autowired
    private GoalRegistry goalRegistry;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void removeTestAggregate() {
        jdbcTemplate.update("DELETE FROM goals WHERE id = ?", GOAL_ID);
    }

    @Test
    void shouldPersistRehydrateAndUpdateGoalAggregate() {
        assertTrue(goalRegistry instanceof PostgresGoalRegistry);

        Goal goal = new Goal(
                GOAL_ID,
                "Persist an achievement",
                LifeArchetype.SCHOLAR);
        CompletionCriterion criterion =
                new CompletionCriterion("Publish durable evidence");
        goal.addCompletionCriterion(criterion);
        goal.satisfyCriterionWithEvidence(
                criterion,
                new Evidence(
                        "Persistence integration verified",
                        "postgresql-integration-test"));

        goalRegistry.register(
                goal,
                new CollectionEntry(
                        "test-persistence-collection",
                        "Persistence evidence",
                        "Transient collection required by the current API",
                        LocalDate.now()),
                Map.of(CRITERION_ID, criterion));

        InMemoryGoalRegistry.GoalTarget reloaded = goalRegistry
                .findGoal(GOAL_ID)
                .orElseThrow();
        CompletionCriterion reloadedCriterion =
                reloaded.criteriaById().get(CRITERION_ID);

        assertEquals(GoalStatus.READY_TO_COMPLETE, reloaded.goal().getStatus());
        assertTrue(reloadedCriterion.isCompleted());
        assertNotNull(reloadedCriterion.getSupportingEvidence());
        assertEquals(
                "Persistence integration verified",
                reloadedCriterion.getSupportingEvidence().getDescription());
        assertThrows(
                IllegalStateException.class,
                () -> reloaded.goal().satisfyCriterionWithEvidence(
                        reloadedCriterion,
                        new Evidence("Duplicate", "integration-test")));

        reloaded.goal().complete();
        goalRegistry.save(reloaded.goal());

        InMemoryGoalRegistry.GoalTarget completedReload = goalRegistry
                .findGoal(GOAL_ID)
                .orElseThrow();
        assertEquals(GoalStatus.COMPLETED, completedReload.goal().getStatus());
        assertEquals(
                1,
                jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM evidence WHERE criterion_id = ?",
                        Integer.class,
                        CRITERION_ID));
    }
}
