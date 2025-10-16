# INC-3 Clarification Summary

a) Interpreted Goal (1 sentence)
- Lift the single canonical base currency constraint so unrestricted currency pairs can be ingested, and enable on‑the‑fly construction of synthetic cross-rates for any `from -> to` pair derivable from the provided pairs, without persistent precomputation/caching.

b) Proposed Scope (concise bullets)
- Accept ingestion of directed currency pair rates with arbitrary bases (not limited to a canonical base).
- `getExchangeRate(from, to)` returns either a direct rate (if ingested) or a synthetic rate computed by chaining/inverting available pairs.
- Synthetic rate computation is done on demand per request; no persistent caching/precomputation of composite paths.
- Preserve existing public API contracts and JSON behavior; extend only where necessary per `.github/copilot-instructions.md`.
- Maintain precision rules and rounding consistency via existing `DecimalPlacesStrategy`.
- Unit/integration tests cover direct, inverted, and multi‑hop synthetic scenarios; include edge cases (no path, same currency, conflicting data).
- Performance expectations: acceptable for typical library use without precomputation; no new dependencies unless justified in the final issue.

c) Ambiguities and Questions (please answer inline)

[ CLARIFICATION NEEDED C1: Increment number/title confirmation — proceed with label/title "INC-3: Lift canonical base currency constraint"? If different, provide the desired label/title. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C2: Input format for ingestion — should ingestion continue to use existing constructors/factories (e.g., `FrozenCurrencyConverter` with provided rates) without changing signatures, or is it acceptable to add a new ingest method? Choose one: (A) keep current signatures only; (B) allow adding a new ingest method; (C) allow both and prefer backward compatibility. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C3: Path selection policy — when multiple synthetic paths exist between currencies, should the implementation: (A) pick the shortest-hop path deterministically; (B) pick the numerically most stable/accurate path; (C) any valid path is acceptable as long as deterministic; (D) any valid path (no guarantee on determinism). Choose one. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C4: Handling conflicting direct vs. synthetic rates — if a direct ingested rate conflicts with a computed synthetic rate (due to data inconsistencies), should direct rates take precedence? Choose one: (A) direct always wins; (B) prefer synthetic if within tolerance; (C) error on significant conflict; specify tolerance if selecting (B) or (C). Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C5: Error behavior when no path exists — should `getExchangeRate(from, to)` throw `IllegalArgumentException` (current pattern for invalid pairs) or return an optional/null? Choose one: (A) throw `IllegalArgumentException`; (B) return empty/Optional; (C) other: specify. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C6: Upper bound for path length — do we need a maximum composition length to prevent excessive chaining (performance/precision)? If yes, provide a hop limit (e.g., 4 or 6); otherwise state "no limit" and accept potential performance trade‑offs. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C7: Determinism across runs — do you require stable results across runs given the same input set (i.e., deterministic path selection)? Choose one: (A) Yes, deterministic; (B) No, any valid path acceptable. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C8: JSON exposure — confirm that JSON serialization rules remain unchanged (no new fields) and synthetic vs direct rates are not distinguished in JSON. Choose one: (A) unchanged JSON; (B) add metadata; (C) other. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C9: Performance baseline — do you want a micro-benchmark to demonstrate reasonable overhead of on-the-fly path construction compared to direct rates? Choose one: (A) Yes, include JMH micro-benchmark; (B) No, skip; (C) Only if tests reveal regressions. Answer: WRITE_HERE ]

[ CLARIFICATION NEEDED C10: Documentation updates — per guide, confirm the implementation must update `docs/ROADMAP.md`, `.github/copilot-instructions.md`, and `README.md` where relevant in the same PR before final review. Choose one: (A) Confirm; (B) Adjust: specify. Answer: WRITE_HERE ]

---
Please answer inline in this document. After your confirmation and answers, I will proceed to draft `issues/inc-3-description.md` per the authoring guide.
