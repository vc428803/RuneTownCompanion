package com.runetown.lifeprogression.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;
import com.runetown.lifeprogression.domain.goal.GoalStatus;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("in-memory")
class GoalCompletionApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryGoalRegistry goalRegistry;

    @Test
    void readyGoalShouldBecomeCompleted() throws Exception {
        Goal goal = registerGoal("goal-ready", true);

        HttpResponse<String> response = complete("goal-ready");

        assertEquals(200, response.statusCode());
        assertEquals(
                """
                {"goalId":"goal-ready","goalStatus":"COMPLETED"}
                """.trim(),
                response.body());
        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
    }

    @Test
    void activeGoalShouldNotBeCompleted() throws Exception {
        Goal goal = registerGoal("goal-active", false);

        HttpResponse<String> response = complete("goal-active");

        assertEquals(409, response.statusCode());
        assertEquals(GoalStatus.ACTIVE, goal.getStatus());
    }

    @Test
    void missingGoalShouldReturnNotFound() throws Exception {
        HttpResponse<String> response = complete("goal-missing");

        assertEquals(404, response.statusCode());
    }

    @Test
    void completedGoalShouldRejectDuplicateCompletion() throws Exception {
        Goal goal = registerGoal("goal-completed", true);
        assertEquals(200, complete("goal-completed").statusCode());

        HttpResponse<String> response = complete("goal-completed");

        assertEquals(409, response.statusCode());
        assertEquals(GoalStatus.COMPLETED, goal.getStatus());
    }

    private Goal registerGoal(String goalId, boolean satisfyCriterion) {
        Goal goal = new Goal(goalId, "Test goal", LifeArchetype.CHALLENGER);
        CompletionCriterion criterion = new CompletionCriterion("Test criterion");
        goal.addCompletionCriterion(criterion);
        if (satisfyCriterion) {
            goal.satisfyCriterionWithEvidence(
                    criterion,
                    new Evidence("Criterion completed", "integration-test"));
        }
        goalRegistry.register(
                goal,
                new CollectionEntry(
                        "collection-" + goalId,
                        "Evidence",
                        "Integration test evidence",
                        LocalDate.now()),
                Map.of("criterion-" + goalId, criterion));
        return goal;
    }

    private HttpResponse<String> complete(String goalId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://127.0.0.1:" + port
                                + "/api/goals/" + goalId + "/completion"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString());
    }
}
