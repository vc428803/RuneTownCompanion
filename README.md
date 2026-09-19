# RuneTown Companion

RuneTown Companion 是一個使用 **Java 21** 開發的領域模型原型，目前定位為 **Nexelyth 的人生進程系統（Life Progression）原型**。

> **你不是因為反覆刷取而成長，  
> 而是因為真正生活過而成長。**

---

## 專案方向

Nexelyth 不希望把現實人生做成：

```text
工作 / 讀書 / 運動
        ↓
      經驗值
        ↓
       升級
```

而是：

```text
玩家建立 Goal
      ↓
設定人生職系 / 成長方向
      ↓
設定 Completion Criteria
      ↓
完成 Milestone / Evidence
      ↓
更新 Player World State
      ↓
影響 VR 世界
```

核心原則：

> **成長來自真實進程，而不是刷取。**

---

## 核心領域模型

目前 Life Progression 主要往以下結構調整：

```text
Goal
├─ LifeArchetype
├─ GoalStatus
├─ CompletionCriteria
├─ Milestones
└─ Evidence
       ↓
Player World State
       ↓
NPC / Quest / Area / Title / Collection
```

目前已先建立：

- `Goal`
- `GoalStatus`
- `CompletionCriterion`
- `LifeArchetype`

後續再逐步整合既有的：

- `Evidence`
- `CollectionEntry`
- `TaskCompletion`
- `Goal Completion`

---

## Goal Completion

Goal 不會因為條件全部勾選就立即完成。

```text
ACTIVE
   ↓
所有 Completion Criteria 完成
   ↓
READY_TO_COMPLETE
   ↓
玩家確認
   ↓
COMPLETED
```

Completion Criteria 用來判斷「是否具備完成資格」，  
真正完成 Goal 的最後一步仍由玩家決定。

---

## 人生職系

現實職業不直接決定玩家在 Nexelyth 中的發展。

不同 Goal 會被收斂到有限的人生職系，例如：

- Guardian
- Scholar
- Artisan
- Creator
- Technomancer
- Healer
- Merchant
- Ranger
- Cultivator
- Commander
- Challenger

因此：

```text
無限種類的現實 Goal
        ↓
有限的 LifeArchetype
        ↓
可管理的遊戲世界規則
```

---

## 人生進程 vs 戰鬥進程

```text
                    Nexelyth
                       │
          ┌────────────┴────────────┐
          │                         │
       人生進程                  戰鬥進程
          │                         │
       現實人生                  VR 世界
          │                         │
   你正在成為誰                你如何戰鬥
```

人生進程主要影響：

- NPC Recognition
- Quest Availability
- Area Access
- Identity / Title
- Collection
- World Interaction

而不直接等同於戰鬥能力或傳統 EXP。