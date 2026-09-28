package com.runetown.lifeprogression.persistence;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.runetown.lifeprogression.api.GoalRegistry;
import com.runetown.lifeprogression.domain.collection.CollectionEntry;
import com.runetown.lifeprogression.domain.goal.CompletionCriterion;
import com.runetown.lifeprogression.domain.goal.Goal;
import com.runetown.lifeprogression.domain.goal.LifeArchetype;

@Component
class DemoGoalInitializer implements ApplicationRunner {

    private final GoalRegistry goalRegistry;

    DemoGoalInitializer(GoalRegistry goalRegistry) {
        this.goalRegistry = goalRegistry;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (goalRegistry.findGoal("goal-demo").isPresent()) {
            return;
        }

        Goal goal = new Goal(
                "goal-demo",
                "Publish the first article",
                LifeArchetype.CREATOR);
        CompletionCriterion criterion =
                new CompletionCriterion("Publish the article");
        goal.addCompletionCriterion(criterion);
        goalRegistry.register(
                goal,
                new CollectionEntry(
                        "collection-demo",
                        "Published work",
                        "Evidence collected for the demo goal",
                        LocalDate.now()),
                Map.of("criterion-demo", criterion));
    }
}
