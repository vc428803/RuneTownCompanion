package com.runetown.lifeprogression.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;
import com.runetown.lifeprogression.domain.goal.GoalStatus;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoalEvidenceApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryGoalRegistry goalRegistry;

    @Test
    void qualifiedEvidenceShouldSatisfyLastCriterionAndReadyGoal()
            throws Exception {

        Goal goal = new Goal("goal-success", "Publish", LifeArchetype.CREATOR);
        CompletionCriterion criterion = new CompletionCriterion("Publish article");
        goal.addCompletionCriterion(criterion);
        CollectionEntry collection = collection("collection-success");
        goalRegistry.register(
                goal,
                collection,
                Map.of("criterion-success", criterion));

        HttpResponse<String> response = postEvidence(
                "goal-success",
                "criterion-success",
                """
                {
                  "title": "Article published",
                  "description": "The article is publicly available",
                  "source": "https://example.test/article"
                }
                """);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"accepted\":true"));
        assertTrue(response.body().contains(
                "\"goalStatus\":\"READY_TO_COMPLETE\""));
        assertTrue(criterion.isCompleted());
        assertNotNull(criterion.getSupportingEvidence());
        assertEquals(GoalStatus.READY_TO_COMPLETE, goal.getStatus());
        assertEquals(1, collection.getEvidences().size());
    }

    @Test
    void rejectedEvidenceShouldNotChangeCriterionOrGoal()
            throws Exception {

        Goal goal = new Goal("goal-rejected", "Read", LifeArchetype.SCHOLAR);
        CompletionCriterion criterion = new CompletionCriterion("Finish book");
        goal.addCompletionCriterion(criterion);
        CollectionEntry collection = collection("collection-rejected");
        goalRegistry.register(
                goal,
                collection,
                Map.of("criterion-rejected", criterion));

        HttpResponse<String> response = postEvidence(
                "goal-rejected",
                "criterion-rejected",
                """
                {
                  "title": "",
                  "description": "Finished the book",
                  "source": "reading-log"
                }
                """);

        assertEquals(422, response.statusCode());
        assertTrue(response.body().contains("\"accepted\":false"));
        assertTrue(response.body().contains("\"goalStatus\":\"ACTIVE\""));
        assertFalse(criterion.isCompleted());
        assertNull(criterion.getSupportingEvidence());
        assertEquals(GoalStatus.ACTIVE, goal.getStatus());
        assertTrue(collection.getEvidences().isEmpty());
    }

    private CollectionEntry collection(String id) {
        return new CollectionEntry(
                id,
                "Evidence",
                "Accepted evidence",
                LocalDate.now());
    }

    private HttpResponse<String> postEvidence(
            String goalId,
            String criterionId,
            String body) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://127.0.0.1:" + port
                                + "/api/goals/" + goalId
                                + "/criteria/" + criterionId
                                + "/evidence"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString());
    }
}
