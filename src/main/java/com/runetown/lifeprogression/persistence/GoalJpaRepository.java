package com.runetown.lifeprogression.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface GoalJpaRepository extends JpaRepository<GoalJpaEntity, String> {

    @EntityGraph(attributePaths = {"criteria", "criteria.evidence"})
    @Query("select distinct goal from GoalJpaEntity goal where goal.id = :id")
    Optional<GoalJpaEntity> findAggregateById(String id);

    @EntityGraph(attributePaths = {"criteria", "criteria.evidence"})
    @Query("select distinct goal from GoalJpaEntity goal")
    List<GoalJpaEntity> findAllAggregates();
}
