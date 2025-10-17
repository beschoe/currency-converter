```markdown
# currency-converter Roadmap & Increment Tracker (v1.0)

Saved to `docs/` for canonical documentation layout.

Authoritative list of implementation increments. This file is the source for overview on planned and implemented increment scopes, status, and Definition of Done (DoD). The `.github/copilot-instructions.md` file references this document instead of duplicating content.

Status legend:
- Planned: Defined but not under active implementation.
- In Progress: Active coding / PR open.
- Done: Merged & validated; scope frozen (no in-place edits—append notes if follow-ups needed).

Change policy:
1. Never rewrite a Done increment in place — append a "Follow-up" note beneath it if required.
2. Forward-looking (Planned / In Progress) increments may be refined; keep edits concise and dated.
3. Each implementation issue / PR title: `INC-<n>: <short goal>` and must link back to the increment section anchor here.

---
## INC-0: Make code compile and tests green in isolated local repo (Status: Planned)
Goal: Make this repository compile and run tests locally with no access to Mercateo’s internal artifact ecosystem, so future changes by humans and AI agents build reliably on any clean machine.

## Scope
- In scope (Milestone INC-0): Local build and test green (no network access to private repos required).
- Out of scope (later milestones): GitHub Actions / Coding Agent CI workflows, additional Copilot prompts, publishing artifacts.

See issues/INC-0-Setup.md for details.

---
# INC-1: GitHub Coding Agent Readiness

Goal: Provide an optimally instrumented sandbox and repository context so the GitHub Coding Agent can work quickly and reliably (demo-friendly, zero trial-and-error on setup).

See issues/INC-1-GithubAgentReadiness.md for details.

---
## INC-2: No-op CI trigger via README whitespace (Status: In Progress)
Goal: Create a minimal, no-functional-change modification (whitespace adjustment in README.md) to exercise the GitHub Coding Agent workflow and CI pipeline end-to-end without impacting library behavior.

Short scope note:
- Add a single harmless whitespace-only change to `README.md` to trigger CI and validate the reusable setup workflow integration.
- No code or behavior changes in the Java library.

Link: issues/inc-2-description.md (to be created after clarifications)

---
## INC-3: Lift canonical base currency constraint (Status: Done)
Goal: Allow ingestion of unrestricted currency pairs and enable on-the-fly synthetic cross-rate calculation in `getExchangeRate(from, to)` without persistent precomputation/caching.

Implementation summary:
- Removed requirement for single canonical base currency
- Implemented graph-based exchange rate storage using EnumMap
- Added BFS path-finding algorithm for shortest-hop synthetic rate calculation
- Maximum 4 hops enforced with deterministic enum-ordinal-based neighbor traversal
- Direct rate precedence over synthetic rates maintained
- Precision-preserving rate composition using scaled base/quote values
- All existing tests pass; 11 new tests added for synthetic rate scenarios

Delivered features:
- Unrestricted pair ingestion (any base/quote currency combinations)
- On-the-fly synthetic cross-rate calculation with no persistent caching
- Deterministic shortest-hop path selection (BFS with enum ordering)
- Direct rate takes precedence when both direct and synthetic paths exist
- IllegalArgumentException thrown when no path exists between currencies
- 4-hop maximum path length enforced
- Precision and rounding consistent with existing DecimalPlacesStrategy
- JSON behavior unchanged (no new fields, rateValue not exposed)
- Backward compatibility: all 35 existing tests pass unchanged

See: PR #[to be filled] and issues/inc-3-description.md

---
## Future (Unscheduled Fragments)
Keep unscheduled ideas *out* of main increment list; add here only if they become likely within next 2–3 cycles.
*(Currently empty)*

---
## Cross References
- Core instructions: `.github/copilot-instructions.md` (current version v1.0) – consult for global guardrails & tooling.

---
## Changelog
- v1.0: Initial roadmap with increments INC-0 for initial setup readiness for development


---
(End of file)

```
