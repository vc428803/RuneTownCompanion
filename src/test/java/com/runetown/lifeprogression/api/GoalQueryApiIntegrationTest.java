package com.runetown.lifeprogression.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.evidence.Evidence;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoalQueryApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryGoalRegistry goalRegistry;

    @Test
    void shouldListGoalsWithCriterionProgress() throws Exception {
        Goal goal = new Goal(
                "goal-progress",
                "Complete two steps",
                LifeArchetype.CHALLENGER);
        CompletionCriterion completedCriterion =
                new CompletionCriterion("Complete the first step");
        CompletionCriterion activeCriterion =
                new CompletionCriterion("Complete the second step");
        goal.addCompletionCriterion(completedCriterion);
        goal.addCompletionCriterion(activeCriterion);
        goal.satisfyCriterionWithEvidence(
                completedCriterion,
                new Evidence("First step completed", "progress-log"));
        goalRegistry.register(
                goal,
                collection("collection-progress"),
                Map.of(
                        "criterion-progress-1", completedCriterion,
                        "criterion-progress-2", activeCriterion));

        HttpResponse<String> response = get("/api/goals");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("""
                {"goalId":"goal-progress","title":"Complete two steps","goalStatus":"ACTIVE","completedCriteriaCount":1,"totalCriteriaCount":2}
                """.trim()));
    }

    @Test
    void shouldReturnGoalDetailsWithCriterionIds() throws Exception {
        HttpResponse<String> response = get("/api/goals/goal-demo");

        assertEquals(200, response.statusCode());
        assertEquals(
                """
                {"goalId":"goal-demo","title":"Publish the first article","lifeArchetype":"CREATOR","goalStatus":"ACTIVE","criteria":[{"criterionId":"criterion-demo","description":"Publish the article","completed":false}]}
                """.trim(),
                response.body());
    }

    @Test
    void shouldReturnCriterionDetailsWithOptionalSupportingEvidence()
            throws Exception {

        HttpResponse<String> withoutEvidence = get(
                "/api/goals/goal-demo/criteria/criterion-demo");

        assertEquals(200, withoutEvidence.statusCode());
        assertEquals(
                """
                {"criterionId":"criterion-demo","description":"Publish the article","completed":false,"supportingEvidence":null}
                """.trim(),
                withoutEvidence.body());

        Goal goal = new Goal(
                "goal-read-evidence",
                "Document an achievement",
                LifeArchetype.SCHOLAR);
        CompletionCriterion criterion =
                new CompletionCriterion("Publish supporting notes");
        goal.addCompletionCriterion(criterion);
        goal.satisfyCriterionWithEvidence(
                criterion,
                new Evidence(
                        "Notes are publicly available",
                        "https://example.test/notes"));
        goalRegistry.register(
                goal,
                collection("collection-read-evidence"),
                Map.of("criterion-read-evidence", criterion));

        HttpResponse<String> withEvidence = get(
                "/api/goals/goal-read-evidence/criteria/criterion-read-evidence");

        assertEquals(200, withEvidence.statusCode());
        assertEquals(
                """
                {"criterionId":"criterion-read-evidence","description":"Publish supporting notes","completed":true,"supportingEvidence":{"description":"Notes are publicly available","source":"https://example.test/notes"}}
                """.trim(),
                withEvidence.body());
    }

    private CollectionEntry collection(String id) {
        return new CollectionEntry(
                id,
                "Evidence",
                "Accepted evidence",
                LocalDate.now());
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path))
                .GET()
                .build();

        return HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString());
    }
}
