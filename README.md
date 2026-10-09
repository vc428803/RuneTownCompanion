# RuneTown Companion

> Life Progression System for the NEXELYTH VR World

RuneTown Companion 是 **NEXELYTH VR World** 的人生進程系統（Life Progression System）。

系統負責將現實生活中的目標、完成條件與成果紀錄轉換為可管理的領域狀態，未來用於影響 VR 世界中的 NPC、任務、區域解鎖與玩家世界狀態。

**核心理念：**

> 成長來自真實進程，而非重複刷取經驗值。

---

# Vision

傳統 RPG 透過擊殺怪物、完成任務取得經驗值成長。

RuneTown Companion 則將玩家在現實世界中的成長歷程轉換成 NEXELYTH 世界內的進程。

例如：

- 完成閱讀計畫
- 通過證照考試
- 完成健身目標
- 建立作品集
- 完成個人成長挑戰

這些真實世界成就將成為驅動虛擬世界變化的來源，而非數值化的刷怪流程。

---

# Architecture Overview

```text
React Native Client
        │
        ▼
Spring Boot REST API
        │
        ▼
Application Port (GoalRegistry)
        │
        ▼
Persistence Adapter
        │
        ▼
Spring Data JPA
        │
        ▼
PostgreSQL
```

透過 Ports & Adapters（Hexagonal Architecture）隔離：

- Business Logic
- Persistence Layer
- Framework Dependencies

避免領域邏輯直接依賴 Spring、JPA 或 PostgreSQL。

---

# Tech Stack

| Layer | Technology |
|---------|---------|
| Language | Java 21 |
| Framework | Spring Boot |
| API | RESTful API |
| Documentation | Swagger / OpenAPI |
| Database | PostgreSQL |
| Persistence | Spring Data JPA |
| Migration | Flyway |
| Architecture | Domain Model + Ports & Adapters |
| Testing | JUnit 5 |
| Integration Testing | Spring Boot Test |
| Client | React Native (Separate Repository) |

---

# Core Domain Model

## Goal Aggregate

RuneTown Companion 以 **Goal** 作為核心 Aggregate Root。

```text
Goal
 ├── LifeArchetype
 ├── CompletionCriterion
 ├── Evidence
 └── GoalStatus
```

### Goal

代表玩家想完成的現實目標。

例如：

- Become Java Backend Developer
- Complete AWS Certification
- Finish Reading Challenge

---

### LifeArchetype

將無限多樣的人生目標映射到有限的人生職系。

範例：

- Scholar
- Craftsman
- Explorer
- Warrior
- Creator

使玩家的成長方向能夠影響 NEXELYTH 世界中的角色定位與世界互動。

---

### CompletionCriterion

定義可驗證的完成條件。

例如：

```text
Read 10 Books
Complete 30 Workout Sessions
Pass AWS Associate Exam
```

只有當所有條件達成後，Goal 才能進入完成候選狀態。

---

### Evidence

用來證明完成條件已被滿足。

範例：

- Certificate
- Screenshot
- URL
- Manual Verification Record

Evidence 不直接完成 Goal，而是作為狀態轉換依據。

---

### GoalStatus

管理目標生命週期。

```text
ACTIVE
READY_TO_COMPLETE
COMPLETED
```

---

# Goal Completion State Machine

```text
ACTIVE
   │
   │ all criteria satisfied
   ▼
READY_TO_COMPLETE
   │
   │ player confirms completion
   ▼
COMPLETED
```

## Design Rationale

將：

- 符合完成資格
- 正式完成目標

視為兩個不同階段。

避免因完成條件更新而直接導致：

```text
ACTIVE
   ▼
COMPLETED
```

造成不可預期的業務行為。

這種 Explicit State Transition 設計可提升：

- 可讀性
- 可測試性
- 領域一致性

---

# REST API

RuneTown Companion 提供 RESTful API：

## Goals

```http
GET /goals
GET /goals/{goalId}
```

查詢：

- Goal 清單
- Goal 詳細資訊
- 完成條件狀態

---

## Evidence

```http
POST /goals/{goalId}/evidences
```

提交完成條件相關證明資料。

---

## Goal Completion

```http
POST /goals/{goalId}/complete
```

由玩家主動完成已符合資格的 Goal。

---

# Persistence Design

採用 Repository Port + Persistence Adapter 設計。

```text
Goal Aggregate
       │
       ▼
GoalRegistry
       │
       ▼
JpaGoalRepositoryAdapter
       │
       ▼
Spring Data JPA
       │
       ▼
PostgreSQL
```

優點：

- Domain 與 JPA 解耦
- Domain 可單獨測試
- Repository 實作可替換
- 降低 Framework Lock-in

---

# Database Migration

資料庫版本管理使用 Flyway。

```text
V1__create_goal_tables.sql
V2__create_evidence_tables.sql
V3__add_goal_status.sql
```

優勢：

- 可追蹤 Schema 變更
- 支援團隊協作
- 確保各環境資料庫一致

---

# Testing Strategy

## Domain Unit Tests

測試：

- Goal 狀態轉換
- Completion Criteria 規則
- Aggregate Consistency
- Domain Invariants

---

## Integration Tests

透過 HTTP 測試驗證：

```text
Controller
     ▼
Application
     ▼
Persistence
     ▼
Database
```

確保 API 行為符合業務需求。

---

# Engineering Principles

| Principle | Purpose |
|------------|------------|
| Domain-Driven Design | 將業務規則集中於領域模型 |
| Ports & Adapters | 隔離基礎設施依賴 |
| Aggregate Consistency | 維持 Goal 內部一致性 |
| Explicit State Transition | 避免狀態混淆 |
| Database Migration | 管理 Schema 演進 |
| Integration Testing | 驗證完整系統行為 |

---

# Future Roadmap

## NEXELYTH World State Integration

RuneTown Companion 未來不直接提供經驗值，而是透過世界狀態影響 VR 世界。

```text
Goal Completion
       │
       ▼
Domain Event
       │
       ▼
Player World State
       │
       ▼
NPC / Quest / Area / Title
       │
       ▼
NEXELYTH VR World
```

---

## Planned Features

### Domain Events

```java
GoalCompletedEvent
```

觸發玩家世界狀態更新。

---

### Player World State

記錄玩家人生進程產生的世界影響。

例如：

- Knowledge Level
- Craftsmanship Level
- Reputation
- Exploration History

---

### World Unlock System

根據玩家真實成長解鎖：

- NPC
- Quest Lines
- Regions
- Titles
- Lore Content

---

### Cross Platform Integration

保持世界狀態與遊戲邏輯解耦：

```text
RuneTown Companion
        │
        ▼
World State API
        │
        ▼
Unity / Web / Mobile
```

使未來可支援：

- Unity VR
- Mobile Client
- Web Dashboard
- Analytics Platform

---

# Repositories

### Backend

**RuneTownCompanion**

https://github.com/vc428803/RuneTownCompanion

---

### Mobile

**RuneTownCompanionMobile**

https://github.com/vc428803/RuneTownCompanionMobile

---

### VR World

**NEXELYTH**

https://github.com/vc428803/NEXELYTH

---

# Project Philosophy

RuneTown Companion 並非傳統任務管理工具。

它的目標是建立一套：

> 真實人生進程 → 領域狀態 → 世界變化

的轉換機制。

當玩家在現實世界成長時，NEXELYTH 世界也會隨之改變。

這不只是遊戲進度。

而是人生進度。
