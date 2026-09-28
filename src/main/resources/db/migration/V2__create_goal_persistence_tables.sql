CREATE TABLE goals (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    archetype VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    CONSTRAINT goals_archetype_check CHECK (archetype IN (
        'GUARDIAN', 'SCHOLAR', 'ARTISAN', 'CREATOR', 'TECHNOMANCER',
        'HEALER', 'MERCHANT', 'RANGER', 'CULTIVATOR', 'COMMANDER',
        'CHALLENGER'
    )),
    CONSTRAINT goals_status_check CHECK (status IN (
        'ACTIVE', 'PAUSED', 'READY_TO_COMPLETE', 'COMPLETED', 'ABANDONED'
    ))
);

CREATE TABLE completion_criteria (
    id VARCHAR(100) PRIMARY KEY,
    goal_id VARCHAR(100) NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    description VARCHAR(500) NOT NULL,
    completed BOOLEAN NOT NULL,
    position INTEGER NOT NULL CHECK (position >= 0),
    CONSTRAINT completion_criteria_goal_position_unique
        UNIQUE (goal_id, position)
);

CREATE INDEX completion_criteria_goal_id_index
    ON completion_criteria(goal_id);

CREATE TABLE evidence (
    criterion_id VARCHAR(100) PRIMARY KEY
        REFERENCES completion_criteria(id) ON DELETE CASCADE,
    description VARCHAR(1000) NOT NULL,
    source VARCHAR(1000)
);
