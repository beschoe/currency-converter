---
name: Increment Draft Request
about: Minimal prompt asking the Coding Agent to draft a full Increment Issue (INC-<n>) via clarification protocol
title: "INC-<n> Draft: <short goal>"
labels: [increment, draft]
assignees: []
---

## PURPOSE
The purpose of this issue type is to trigger creation of Increment Issue descriptions (just descriptions, no coding implementation!) from a very concise seed information that is summarized in this issue type here. That concise seed information shall serve to instruct the GitHub Coding Agent to then execute by producing the full Increment Issue Description (hint: do not forget to adapt title appropriately).

## Increment -> this is the core seed info, look up enriching content in reference docs as indicated below:
INC-<n>

## Goal (One Sentence) - delete if not needed
<concise outcome or remove>

## Hard Constraints / Must-Nots (Optional)  - delete if not needed
- <constraint or remove section if none>

## Rationale (Optional – 1 short line)  - delete if not needed
<why now / value> (optional)

---
### CODING AGENT TASK (AUTHOR AN INCREMENT ISSUE – DO NOT IMPLEMENT CODE)
Your assignment is LIMITED to producing a fully authored Increment Issue description for the indicated Increment label. You MUST NOT fire up a dev sandbox environment, install dependencies, run tests, implement any code or do any of such implementation-typical activities, but only focus on issue clarification + authoring.

Notes:
- Goal is optional in seed; if omitted you MUST derive an initial one-sentence goal directly from the `docs/ROADMAP.md` entry (no embellishment) before clarification.
- If present, any user-provided hard constraints MUST appear explicitly in the authored description (Out of Scope for prohibitions, Definition of Done for enforceable conditions, skipping only those already covered by global guardrails—link instead of duplicating).

Authoritative references: See hierarchy in `docs/INC_ISSUE_AUTHORING.md` Section 3 (do not restate or duplicate here).

#### Required Sequence (Pointer Only)
Follow Steps 1–7 in `docs/INC_ISSUE_AUTHORING.md` Section 5, then produce the authored description per Sections 7 & 10 (filename + storage, roadmap DoD item). No code changes.

Clarification Hard Stop: You MUST post the Clarification Comment and WAIT for explicit human approval (as defined in Section 5 Hard Stop Gate) before creating the markdown description file.

#### Forbidden Actions (Summary)
Refer to guardrails + Section 7; in this phase: no source edits, no dependency changes, no roadmap edits, no scope expansion (extras → Follow-up), no implementation hashtag.

#### Conflict Handling
If any contradiction among documents is detected, surface it explicitly in the Clarification Comment; do not guess.

#### Completion Signal
Follow Section 10 completion protocol (PR with markdown doc only; request review). Do not proceed to implementation phase.

<!-- End of agent instructions. Nothing below this marker should be altered by the agent outside the authorized replacement step. -->