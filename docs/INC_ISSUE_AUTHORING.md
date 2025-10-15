# Increment Issue Authoring & Interaction Guide (obs-dialog)

Version: 2025-09-17
Authoritative companions: `.github/copilot-instructions.md` (guardrails), `docs/ROADMAP.md` (increment scopes), `CONTRIBUTING.md` (workflow), `.github/ISSUE_TEMPLATE/increment-issue.md` (base template).

## 1. Purpose
This guide instructs the GitHub Coding Agent how to draft high-quality Increment Issue Descriptions (e.g., INC-2, INC-3, …) from a minimal human request. It extends the bare template with deeper expectations on clarification, scoping discipline, validation, and interaction workflow.

Terminology (per process resolutions):
- Increment label: full string `INC-<n>` (e.g., `INC-2`).
- Increment number: the numeric component `<n>`.

## 2. When to use this document
Use this guide whenever the user creates a request with a short, minimal spec referencing this document (e.g., "Draft full description per INCREMENT_ISSUE_AUTHORING.md for INC-2"). In these cases, fully observe the content of this document and make sure that the agent MUST NOT immediately create the requested issue description; it must first perform clarification (see Section 5). As part of executing such an issue description task, never execute any code changes or any file changes other than provided in the instructions of this document.

## 3. Source of Truth Hierarchy (Consult in Order)
1. `docs/ROADMAP.md` – definitive increment scope & status.
2. `.github/copilot-instructions.md` – global guardrails, DoD patterns.
3. This guide – process & interaction rules.
4. `CONTRIBUTING.md` – contribution mechanics, test & lint expectations.
5. Existing merged increments’ issues / PRs – precedent patterns (read-only context).
If conflicts emerge, escalate via clarification comment; do not guess silently.

## 4. Output Goal
Produce a finalized Increment Issue Description, and submit it in markdown format (submitting = saving it in directory ./issues under filename pattern `inc-<n>-description.md`), so that:
- Conforms to template structure but enriches each section with precise, testable, atomic statements.
- Avoids restating global guardrails; references them instead.
- Contains only scope that aligns with roadmap; any expansion is flagged as a proposed follow-up, NOT merged into main DoD without approval.
- Optimized to enable an automated implementation pass by Github Coding Agent with zero further domain assumptions.

## 5. Execution Sequence
1. Parse the user request to find increment number (part of increment label) and, if provided, a stated goal and stated constraints.
2. Extract enriching information from `docs/ROADMAP.md`: Look up the increment number (part of increment label) in that document and retrieve information about goal, context etc. 
3. Execute an explicit and thorough online research for each of the following topics to retrieve up-to-date information on relevant questions: a) concerning architectural design patterns b) concerning relevant technological advances and best practices c) concerning most up-to-date versions of dependencies / APIs etc.
4. Produce concise Clarification Summary and submit it for user review (save in directory ./issues under filename pattern inc-<n>-clarifications.md), see section 6 "Clarification Question Guidelines" for details about this document
5. Hard stop to wait for approval from this user review. Do not execute any further activities while waiting for review approval.
6. Proceed to authoring ONLY after the Hard Stop Gate is satisfied (explicit approval received + no unresolved blocking questions). If user answers partially and leaves ≥1 blocking ambiguity unresolved, re-ask ONLY those; avoid repeating cleared items.
7. After explicit clarification approval from the user, check the Clarification Summary document for clarification answers and write the finalized Increment Issue Description
8. Save the finalized Increment Issue Description in directory ./issues under filename pattern `inc-<n>-description.md`.

## 6. Clarification Summary Guidelines
The goal of the Clarification Summary document is to ensure a concise and very focused alignment with the responsible user on critical open questions and ambiguities. Its content shall be reduced to the max to contain the following:
   - a) Interpreted Goal (1 sentence)
   - b) Proposed Scope bullets (from roadmap + inferred details)
   - c) Ambiguities and Questions

Clarification questions are to be placed inline with markers = fixed square-brackets-marker + "CLARIFICATION NEEDED Cx:" (Cx = a numbered sequence label C1, C2, C3...), see this example:  [ CLARIFICATION NEEDED C1: **some ambiguity stated, then stating your default suggestion for easy confirmation, then placeholder for answer**: WRITE_YOUR_ANSWER_HERE ] 
Even if no open questions should exist, still create the Clarification Summary and state clearly that no open questions remain, still ask for greenlighting further processing.
Questions MUST be:
- Essential to prevent incorrect implementation (avoid trivia).
- Bounded: each asks about a single dimension (scope boundary, performance target, error handling nuance, dependency choice, test coverage edge, etc.).
- Actionable answer formats (e.g., "Choose one: A/B" or "Provide a value in ms").
- Limited: target 3–7 questions typical; >7 only if objectively necessary (justify briefly).
- Include pragmatic prompts for the user to make adding in-document answers as simple and efforless as possible. 
All answers and clarifying comments from the user shall be given inside the document as part of the user's review process.
Even if no open questions should arise, still produce the document and add statement like "no open questions, please add "ok" to greenlight".

## 7. Fully Authored Issue Structure
To produce the final Increment Description document, use the enriched, annotated template at `.github/ISSUE_TEMPLATE/increment-issue.md` as the single source of structural truth. 

Agent responsibilities when generating the final issue body:
1. Ensure every Scope bullet maps to a Definition of Done item (1:1 or clearly grouped) – reject extras silently introduced in clarification (move them to Follow-up).
2. Verify each DoD line is observable + deterministic; rewrite vague wording before posting.
3. Populate the Acceptance Checklist by mirroring each DoD line verbatim (or a concise paraphrase) for implementation tracking.
4. Separate genuine risks (with mitigations) from mere assumptions; never leave a Risk without a mitigation note.
5. Provide at least: one happy path test, one failure path test, and one boundary/edge test; add more only if materially different logic branches exist.
6. Confirm Links / References resolve (paths exist) before finalizing.
7. If any required section is intentionally empty (rare), insert a placeholder like “None (justification: <reason>)” to avoid ambiguity.
8. Reflect every user-provided hard constraint explicitly: translate prohibitions into Out of Scope items; translate enforceable rules into Definition of Done lines (and mirror in Acceptance Checklist); omit restating any constraint already covered verbatim by global guardrails (link instead).
9. Add the following mandatory DoD + Acceptance Checklist item which ensures keeping the reference docs up to date: 
   - before submitting the implementation for review, update the following reference documents in case relevant changes have been introduced by this increment and its implementation (only update where clearly necessary, avoid bloat and irrelevant udpates)
      - description of the respective "Increment <n>" inside the doc `docs/ROADMAP.md`:
         a) update the Increment status to Done  
         b) update and, if appropriate to remedy content divergence, overwrite the INC-<n> description to reflect high-level factual scoping of issue description and any deviations that resulted from the implementation process 
         c) if relevant, append any high-level follow-up bullets beneath the increment description
      - content of .github/copilot-instructions.md: current doc content has been checked for critical inconsistencies or critical gaps concerning design decisions introduced by this increment, and necessary updates have been saved accordingly
      - content of README.md: current doc content has been checked for critical inconsistencies or critical gaps concerning design decisions introduced by this increment, and necessary updates have been saved accordingly
   Explicitly instruct: These potential updates to docs/ROADMAP.md, .github/copilot-instructions.md and README.md MUST be part of the same PR and shall be written before FINAL Review (ie. before merge!), so they are reviewed and approved together with the implementation work. It regularly happens that the Coding Agent schedules this documentation task, then hits an implementation that affords an interim user review, and then in focusing on the requested changes from the interim user review, it either finally reports only those last changes in overall documentation update, or - standard case - omits completely the forgotten outstanding task of updating documentation. Thereby the instruction in the issue description shall ensure that the agent actively re-activates this specific todo on its to do list every time it enters into a new implementation sequence. And include this topic explicitly and always in DoD section.
10. Include explicit advice to check earlier increment outlines in ROADMAP.md when encountering conflicting behaviour or conflicting test expectations, to help determine current expected behaviour by distinguising behaviour from recent increments from inconcistent stale  behaviour from incomplete recent implementation coverage.

(Remark for future updates of this section: Do NOT replicate the template’s inline guidance here; changes belong solely in the template file to prevent divergence.)

## 8. Style & Precision Rules
- No speculative future features in Scope/DoD (place in Follow-up).
- Avoid soft language: replace “should” with “MUST” or rewrite objectively.
- Use present tense for behavior statements.
- Be concise, do not overstructure
- Prefer referencing shared rules (e.g., “Lint/Types pass per CONTRIBUTING.md”).

### 8b. Offline Implementation Guidance (Mandatory when agent has no internet)
When the Coding Agent cannot access the internet during implementation, every increment issue MUST include a compact “Implementation Guidance (offline)” subsection with sufficient technical detail to implement without ad‑hoc research. Keep it guidance‑only (no premature code), but specific enough to avoid ambiguity.

Required contents of the offline guidance subsection:
- Dependencies
   - Explicit list of new runtime and dev dependencies added/changed.
   - Exact, pinned versions that are known compatible with the repo’s existing frameworks and platform(s).
   - A one‑line rationale for each new dependency.
- In case of newly introduced APIs/entry points: 
   - The precise imports and class/function names to call, with signatures.
   - Minimal example snippets showing object creation and the key method(s) used (e.g., constructor + primary call).
   - Any required type expectations for inputs/outputs.

Constraints:
- Provide only what is necessary and stable; avoid restating global guardrails—link to `.github/copilot-instructions.md` when applicable.
- Do not include full implementations; keep to small illustrative snippets and contracts.
- Prefer referencing existing repo symbols to ensure consistency.

## 9. Validation Pass Before Finalizing
Agent MUST self-check:
- Every Scope bullet has a matching DoD verification point.
- No DoD item lacks either a test case or an asserted rationale for omission.
- No new dependency without rationale.
- All questions from clarification resolved or accepted via assumptions list.
- Links resolve (syntactic check: relative path exists).

## 10. Storing the issue description
Store the fully authored Increment Issue Description in markdown format (filename pattern `inc-<n>-description.md`) in directory `./issues/` on a doc-change branch (no code modifications). Inform the user about document readiness.

## 11. Common Pitfalls & How to Avoid
| Pitfall | Avoidance |
|---------|-----------|
| Re-describing global guardrails | Link to `.github/copilot-instructions.md` instead. |
| Scope creep during clarification | Tag extras as Follow-up. |
| Vague test descriptions | Include expected trigger + assertion summary. |
| Hidden refactors | Declare or defer. |
| Dependency surprise | List explicitly or state None. |

## 12. Decision Log Inline (Optional)
If >2 significant clarifications alter the initial interpretation, add a short “Decision Log” subsection (chronological bullet list with date + summary) above Follow-up.

## 13. Example (Abstracted Skeleton)
(Not tied to actual increment scope—illustrative)
```
Title: INC-99: Introduce conversation core module

Goal: Provide isolated conversation state manager for CLI reuse.

Scope:
- Create conversation.py with Conversation class (append & list messages)
- Integrate existing CLI to use Conversation
...
```
(Do not copy verbatim; adapt.)

## 14. Evolution of This Guide
Changes require updating version header + brief Changelog below.

### Changelog
- 2025-09-17: Added Section 8b for mandatory offline implementation guidance and bumped version.
- 2025-09-14: Added roadmap maintenance responsibility (#8) and relocated guide to docs.
- 2025-09-14: Initial version created post INC-1 implementation.

(End of file)
