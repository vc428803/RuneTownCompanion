# RuneTown Companion Roadmap

## Completed

- Java 21／Maven 專案骨架與基礎 Player 等級、經驗值模型。
- Evidence Candidate 經資格規則轉為 Evidence，並加入 CollectionEntry 的初始流程。
- Goal、CompletionCriterion、GoalStatus 與 LifeArchetype 領域模型。
- Goal 在所有條件完成後進入 `READY_TO_COMPLETE`，並由玩家明確確認為 `COMPLETED`。
- 已完成 Goal 可建立帶有 LifeDimension 分類的 Milestone。
- ImpactRule 可依 Milestone 是否包含全部必要 LifeDimension，對應 GameImpactType。
- ImpactRule 核心比對測試已完成：包含全部必要維度時 match，缺少任一必要維度時不 match；聚焦測試執行通過。

## Next Step

- 為既有 Goal 流程補上單元測試，驗證 CompletionCriterion 全數完成後進入 `READY_TO_COMPLETE`、玩家確認後成為 `COMPLETED`，以及只有已完成 Goal 能建立 Milestone。

## 已確立的重要 Domain 設計原則

- 成長來自真實人生進程，不以重複刷取或戰鬥 EXP 取代。
- CompletionCriterion 只代表完成資格；Goal 的正式完成保留玩家確認步驟。
- 多樣的現實 Goal 收斂為有限的 LifeArchetype、LifeDimension 與 GameImpactType。
- 狀態轉換由 domain object 維護，集合以不可修改檢視或防禦性拷貝保護。
- 可變規則以 policy／rule 表達，避免把資格與影響邏輯綁死在應用流程中。

## Backlog / Known Issues

- 測試覆蓋仍低：Goal、Milestone、Evidence、Collection 與應用服務缺少有效測試，`CollectionEntryTest` 為空、`AppTest` 仍是樣板。
- Evidence、TaskCompletion、Goal、Milestone 與 ImpactRule 尚未形成端到端流程。
- 尚未建立 Milestone 套用 ImpactRule 並產生遊戲影響結果的應用層流程。
- 多個 domain constructor 的 null、空值與識別碼 invariant 尚未一致化。
- Player EXP 模型仍是早期原型，與 Life Progression 主線尚未整合，且未處理一次跨多級等邊界情況。
- README 尚未反映最近加入的 Milestone 與 ImpactRule。
- 專案缺少 Maven Wrapper，且目前環境沒有可直接呼叫的 Maven CLI，完整測試套件的執行可重現性不足。
