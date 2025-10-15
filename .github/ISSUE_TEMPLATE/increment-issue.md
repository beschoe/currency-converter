---
name: Increment Issue
about: Define and request implementation of a roadmap increment (INC-<n>)
title: "INC-<n>: <short goal>"
labels: [increment]
assignees: []
---

<!--
INSTRUCTIONS
1. Replace <n> with increment number from docs/ROADMAP.md.
2. Keep scope minimal; defer extras to a future increment.
3. Do NOT duplicate global guardrails (see .github/copilot-instructions.md).
4. Add a Definition of Done item for updating ROADMAP.md (status + follow-ups) and mirror it in the Acceptance Checklist.
5. Only add #github-pull-request_copilot-coding-agent at the VERY END when ready for automated implementation.
-->

## Increment ID
INC-<n>

## Goal (One Sentence)
<!-- Single declarative outcome; avoid conjunctions and vague adjectives. -->
<clear, outcome-oriented statement>

## Scope
<!-- Each bullet = one discrete deliverable (verb first). Must map directly to roadmap intent. Internal refactors ONLY if indispensable; else move to Follow-up. -->
- <bullet>
- <bullet>

## Out of Scope
<!-- List tempting adjacent concerns intentionally excluded to prevent scope creep. -->
- <bullet>
- <bullet>

## Definition of Done
<!-- Each item MUST be: Observable (verification method implicit or explicit), Deterministic, Aligned with guardrails. Include functional behavior, error path handling, logging/reporting expectations, quality gates, doc updates. -->
1. 
2. 
3. 
4. ROADMAP.md updated in this PR (set status; revise description if needed; add follow-up notes)

## Tests (Add / Update)
<!-- List concrete cases: happy path, at least one failure path, one boundary. Name target modules/functions when known. State mocking boundary (LLM boundary only). -->
- New test modules or cases:
- Mock strategy (LLM boundary only):

## Dependency Changes (If Any)
<!-- Explicit Add / Remove / Upgrade or state “None”. Provide rationale + risk/footprint note. -->
- Add: (name + reason)
- Remove: (name + reason)

## Migration / Refactor Notes
<!-- File moves, renames, API shape changes, transitional compatibility steps. State 'None' if not applicable. -->
- <data transformations? file moves? none>

## Risks / Assumptions
<!-- Each Risk paired with mitigation. Assumptions captured from clarification phase. -->
- Risk: <description & mitigation>
- Assumption: <description>

## Acceptance Checklist (Execution)
<!-- Mirror Definition of Done items for implementation tracking. -->
- [ ] <mirror DoD item 1>
- [ ] <mirror DoD item 2>
- [ ] <mirror DoD item 3>
- [ ] ROADMAP.md updated (status + follow-ups)

## Links / References
<!-- Roadmap anchor, guardrails, prior increment (if relevant). -->
- Roadmap: docs/ROADMAP.md#inc-<n-lowercase-anchor>
- Guardrails: .github/copilot-instructions.md
- Prior increment: <link or none>

## Follow-up (Future Increment Candidates)
- <possible enhancement>

## Checklist (Author)
- [ ] Increment matches ROADMAP entry (status set to In Progress on start)
- [ ] Scope minimal & aligned
- [ ] DoD items testable
- [ ] Tests specified
- [ ] Dependencies justified (or none)
- [ ] No overlap with another open increment
- [ ] Roadmap update DoD item included

## Checklist (Reviewer)
- [ ] DoD unambiguous
- [ ] No hidden scope creep
- [ ] Test plan adequate
- [ ] Dependencies minimal
- [ ] Roadmap update present & accurate

<!-- Uncomment ONLY when ready to trigger Coding Agent implementation.
#github-pull-request_copilot-coding-agent
-->
