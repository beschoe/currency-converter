# INC-3: Lift canonical base currency constraint

## Goal
Lift the single canonical base currency constraint so unrestricted currency pairs can be ingested, and enable on‑the‑fly construction of synthetic cross‑rates for any `from -> to` pair derivable from the provided pairs, without persistent precomputation/caching.

All algorithmic and technology-related design choices (data structures, search strategy, precision handling beyond existing strategies, etc.) are fully delegated to the GitHub Coding Agent. This issue intentionally avoids additional technical detail.

## Scope
In scope:
- Ingest directed currency pair rates with arbitrary bases (no single canonical base required).
- `getExchangeRate(from, to)` must return either:
  - the direct ingested rate when present, or
  - a synthetic rate computed by composing available pairs on demand (no persistent precomputation/caching).
- Path selection and behavior (per clarifications):
  - Deterministic selection of the shortest‑hop path when multiple synthetic paths exist.
  - Enforce a maximum composition length of 4 hops.
  - If both direct and synthetic rates are available and conflict, the direct rate takes precedence.
  - If no path exists, throw `IllegalArgumentException`.
  - Results must be deterministic across runs given the same inputs.
- JSON behavior remains unchanged; do not expose synthetic vs direct distinction in JSON.
- Maintain precision and rounding via existing strategies (e.g., `DecimalPlacesStrategy`).
- API expectations:
  - Preserve backward compatibility of existing constructors/factories; a new ingest method is allowed only if structurally unavoidable.

Out of scope:
- Any persistent caching or precomputation of synthetic paths.
- JSON metadata additions or new JSON fields.
- Non‑deterministic path selection policies.
- Unlimited path lengths or hop limits greater than 4 (unless explicitly approved in a follow‑up).
- Introducing new runtime dependencies without explicit justification and approval.

## Definition of Done (DoD)
- Unrestricted pair ingestion implemented; existing APIs preserved, with any additions only if structurally unavoidable.
- `getExchangeRate(from, to)` computes synthetic cross‑rates on demand when direct rates are not available.
- Deterministic, shortest‑hop path selection implemented with a hard hop limit of 4.
- Direct ingested rates take precedence over conflicting synthetic rates.
- `IllegalArgumentException` is thrown when no path exists between currencies.
- Precision and rounding are aligned with existing `DecimalPlacesStrategy` usage and current library behavior.
- JSON serialization/deserialization remains unchanged; no new fields or flags.
- No new runtime dependencies added; if any are proposed, they must be justified and pinned, or else removed.
- Tests:
  - Happy path: direct conversion (ingested pair) returns expected result.
  - Synthetic conversion: multi‑hop within the 4‑hop limit returns expected result deterministically.
  - Failure path: no available path raises `IllegalArgumentException`.
  - Edge: same‑currency conversion behavior validated (existing scaling/precision preserved).
  - Edge: presence of both direct and synthetic path — verify direct rate precedence.
  - Edge: multiple candidate synthetic paths — verify shortest‑hop determinism.
  - Edge: hop‑limit boundary — no composition beyond 4 hops; verify behavior.
- Documentation updates (mandatory, same PR before final review):
  - Update `docs/ROADMAP.md` to reflect INC‑3 status and any scope refinements; mark Done at submission time.
  - Check `.github/copilot-instructions.md` for inconsistencies or gaps introduced by this increment; update only where necessary.
  - Check `README.md` for material inconsistencies; update only where necessary.
  - Include an explicit reminder in the PR description that these documentation updates are part of the DoD.
- Conflict handling guidance: if encountering behavior or test expectation conflicts with earlier increments, check earlier increment outlines in `docs/ROADMAP.md` and align with the most recent expected behavior; document any deviations in the PR.

## Acceptance Checklist
- [ ] Unrestricted pair ingestion implemented; existing APIs preserved (additions only if structurally unavoidable).
- [ ] `getExchangeRate(from, to)` computes synthetic cross‑rates on demand with no persistent caching.
- [ ] Deterministic shortest‑hop selection with hop limit = 4 implemented.
- [ ] Direct rate precedence over synthetic conflicts enforced.
- [ ] `IllegalArgumentException` on no‑path condition implemented.
- [ ] Precision/rounding consistent with `DecimalPlacesStrategy` and existing behavior.
- [ ] JSON behavior unchanged; no new fields.
- [ ] No new runtime dependencies (or fully justified/pinned if introduced).
- [ ] Tests added/updated for happy path, synthetic multi‑hop, no‑path failure, same‑currency, direct‑vs‑synthetic precedence, multi‑path determinism, hop‑limit boundary.
- [ ] `docs/ROADMAP.md`, `.github/copilot-instructions.md`, and `README.md` checked and updated where relevant in the same PR before final review.
- [ ] PR notes include reminder to verify earlier increments for conflicting expectations and to document any deviations.

## Risks & Mitigations
- Inconsistent data leading to conflicts between direct and synthetic rates — mitigated by explicit direct‑rate precedence.
- Potential path explosion — mitigated by hard hop limit (4) and shortest‑hop selection.
- Precision drift across chains — mitigated by adherence to existing precision strategies; no additional technical detail specified here.

## Notes on Design Ownership
All algorithmic and technology‑related design choices for enabling on‑the‑fly synthetic rates are explicitly delegated to the GitHub Coding Agent, within the guardrails of `.github/copilot-instructions.md` and the scope constraints above.
