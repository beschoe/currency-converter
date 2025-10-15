# INC-1: GitHub Coding Agent Readiness

Goal: Provide an optimally instrumented sandbox and repository context so the GitHub Coding Agent can work quickly and reliably (demo-friendly, zero trial-and-error on setup).

Authoritative companions: `.github/copilot-instructions.md` (guardrails), `CONTRIBUTING.md` (local build workflow), `docs/ROADMAP.md` (scope), `docs/INC_ISSUE_AUTHORING.md` (agent interaction rules).

---

## Research Highlights (condensed)
- Reusable workflows: Use `on: workflow_call` to offer a shared setup workflow for CI/agents; callers can pass inputs/secrets; matrix support is possible in callers. Ref: GitHub Actions “Reuse workflows”.
- Java setup: Prefer `actions/setup-java@v5` with `distribution: temurin`, matrix for Java 17 & 21 (our code targets release 17), enable built-in Maven cache; use wrapper with non-interactive flags.
- Agent context: Repository custom instructions live in `.github/copilot-instructions.md` (kept concise, authoritative, linked from CONTRIBUTING/README). Avoid duplicating details elsewhere.
- Repo settings (human step): Ensure Actions are enabled, default workflow permissions are adequate, and Coding Agent is enabled for the repo (account already agent-enabled). Keep required checks pragmatic (demo-friendly).

Assumptions:
- Private repo with Actions enabled; Coding Agent can run on this repository once toggled in settings.
- No external secrets required (project is self-contained).

---

## Task List (checkboxed)

### A) Sandbox workflow for Agent-ready environment
- [ ] A1. Add reusable setup workflow `.github/workflows/copilot-setup-steps.yml`:
  - `on: workflow_call`
  - `inputs`:
    - `java-version` (string, default: '21')
  - Steps:
    - Checkout (actions/checkout@v4 or @v5)
    - Setup Java (actions/setup-java@v5) with `distribution: temurin`, `java-version: ${{ inputs.java-version }}`, `cache: maven`
    - Ensure wrapper is executable (chmod +x mvnw) and print versions (`./mvnw -v` / `java -version`)
    - Optional: echo helpful diagnostics (workspace, runner OS) for triage
- [ ] A2. Update existing CI (`.github/workflows/ci.yml`) to reuse A1:
  - Replace inline setup with a call to `./.github/workflows/copilot-setup-steps.yml` and keep the matrix at the caller level (17, 21)
  - Follow with a minimal job step: `./mvnw -B -ntp clean test`
- [ ] A3. Add a lightweight “caller example” section comment in the reusable file (YAML comments) to guide future reuse by other workflows (no extra workflow needed)
- [ ] A4. Validate CI run time and cache efficacy (expect cache hits on subsequent runs)

### B) GitHub-side configuration (human, no code)
- [ ] B1. Verify “GitHub Coding Agent” is enabled for this repository (Settings → Code & automation → Copilot → Coding Agent) and allowed to run on private repos per org policy
- [ ] B2. Actions settings: Confirm Actions are enabled; set default workflow permissions to “Read repository contents” (typical) and allow GitHub-authenticated actions
- [ ] B3. Branch protection (demo-friendly):
  - Optional: Require “CI” workflow to pass on `main` (keep minimal checks to avoid demo friction)
- [ ] B4. Restrict required approvals to avoid blocking agent demo PRs (optional; adapt to demo target)

### C) Essential context files (keep focused; avoid overengineering)
- [ ] C1. Review `.github/copilot-instructions.md` for any drift; reference the new reusable workflow by name and purpose (no duplication)
- [ ] C2. Ensure `CONTRIBUTING.md` points to guardrails and shows `./mvnw -B -ntp` usage (already in place); keep scope lean
- [ ] C3. Add `.github/ISSUE_TEMPLATE/config.yml` to default to the Increment template; keep blank issues ENABLED (per demo flexibility)
- [ ] C4. Optional: Add `CODEOWNERS` to auto-assign review for agent PRs (keeps human loop fast); keep simple (e.g., `* @repo-owner`). If undesired, skip and use manual assignment (current preference).
- [ ] C5. Optional: Add a CI badge to `README.md` for quick status visibility during demo

### D) Validation & Demo Readiness
- [ ] D1. Open a trivial doc-only PR to trigger CI with matrix (17, 21) and confirm green
- [ ] D2. Run the reusable setup workflow indirectly via CI and verify the wrapper/non-interactive flags are honored in logs
- [ ] D3. Confirm the Agent can reference the repository instructions (spot-check: the guardrails file is concise, pointers resolve)

---

## Deliverables
- Reusable workflow: `.github/workflows/copilot-setup-steps.yml`
- Updated CI that reuses the setup workflow
- (Optional) `.github/ISSUE_TEMPLATE/config.yml`, `CODEOWNERS`, README badge

## Acceptance Criteria
- CI green on both JDK 17 and 21 using the Maven Wrapper and non-interactive flags
- No private dependencies or secrets required
- Guardrails and CONTRIBUTING remain the authoritative sources without duplicated detail
- Reusable setup workflow is documented inline and callable from current/ future workflows

## Notes / Risks
- GitHub-side toggles for Coding Agent must be set by a maintainer with repo admin rights
- Keep the workflow minimal to preserve demo time; avoid publishing/signing steps and long caches

