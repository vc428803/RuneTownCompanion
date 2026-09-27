# RuneTown Companion Roadmap

## Completed

- Java 21、Spring Boot、Maven Wrapper 與 springdoc Swagger UI 的本機可執行環境。
- Player 等級與經驗值基礎模型。
- EvidenceCandidate 經資格規則轉為 Evidence，並加入 CollectionEntry。
- 合格 Evidence 可滿足 CompletionCriterion，觸發 Goal 重新評估並進入 `READY_TO_COMPLETE`；已完成 criterion 不允許替換 supporting Evidence。
- `POST /api/goals/{goalId}/criteria/{criterionId}/evidence` 已透過本機 in-memory registry 跑通上述流程，成功與 qualification 失敗情境皆有 HTTP 整合測試及 Swagger 實測。
- Mobile MVP read API 已可列出 Goals、讀取 Goal Detail 與 Criterion Detail；包含 criterion 完成數、LifeArchetype、registry 提供的 criterionId，以及可為 null 的 supporting Evidence，並已有 HTTP 整合測試與 Swagger 實測。
- Goal completion API 已完成：`POST /api/goals/{goalId}/completion` 僅允許 `READY_TO_COMPLETE` Goal 呼叫既有 `Goal.complete()` 轉為 `COMPLETED`，並回傳 backend authoritative GoalStatus；不存在回傳 404，狀態不合法或重複完成回傳 409，已有 HTTP 整合測試與 OpenAPI 文件。
- Goal、CompletionCriterion、GoalStatus、LifeArchetype、Milestone、LifeDimension、ImpactRule 與 GameImpactType 核心模型。
- Goal 可由玩家明確確認為 `COMPLETED`，已完成 Goal 可建立帶有 LifeDimension 分類的 Milestone。
- ImpactRule 可判斷 Milestone 是否包含全部必要 LifeDimension，且完整與缺少維度情境已有測試。

## Current State

- Backend 已提供 Mobile MVP 所需的 Goal list／detail、Criterion detail、Evidence submission 與 Goal completion HTTP APIs；mobile completion API blocker 已移除。
- Goal completion 沿用 in-memory registry，不包含 Milestone processing、ImpactRule、World State、game reward、AI、persistence 或 authentication。

## Next Step

- 切回 `RuneTownCompanionMobile`，依 `MOBILE_API_CONTRACT.md` 串接 Goal completion endpoint，將既有確認 modal、loading／disabled UI 與 `COMPLETED` 狀態畫面接上 backend response 及 404／409 錯誤處理。

## 已確立的重要 Domain 設計原則

- 成長來自真實人生進程，不以重複刷取或戰鬥 EXP 取代。
- CompletionCriterion 只代表完成資格；Goal 的正式完成保留玩家確認步驟。
- 一個 CompletionCriterion 第一版只接受一筆 supporting Evidence，完成後不可覆蓋。
- 多樣的現實 Goal 收斂為有限的 LifeArchetype、LifeDimension 與 GameImpactType。
- 狀態轉換由 domain object 維護；application/API layer 只協調既有 domain 行為。
- 可變規則以 policy／rule 表達，避免把資格與影響邏輯綁死在應用流程中。

## Backlog / Known Issues

- in-memory registry 僅供本機 vertical slice 驗證，尚無 persistence、跨重啟資料保留或正式 repository abstraction。
- Evidence API 的錯誤格式仍沿用 Spring 預設回應，409 等錯誤未包含 domain 原因文字。
- 尚未建立 Milestone 套用 ImpactRule 並產生遊戲影響結果的應用層流程。
- Goal、Milestone、Evidence、Collection 等 domain 測試覆蓋仍有限，`CollectionEntryTest` 為空、`AppTest` 仍偏樣板。
- 多個 domain constructor 的 null、空值與識別碼 invariant 尚未一致化。
- Player EXP 模型尚未與 Life Progression 主線整合，亦未處理一次跨多級等邊界情況。
- README 尚未反映目前的 Life Progression 與本機 Swagger 操作方式。
