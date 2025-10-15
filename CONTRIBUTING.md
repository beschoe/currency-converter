# Contributing Guide

This guide focuses on contribution mechanics and local build steps. It intentionally avoids duplicating guardrails and architecture details that live in the authoritative sources below.

Authoritative sources (read first, do not duplicate here):
- Guardrails, architecture, standards, and testing expectations: `.github/copilot-instructions.md`
- Increment scopes and statuses: `docs/ROADMAP.md`
- Increment issue authoring & clarification workflow: `docs/INC_ISSUE_AUTHORING.md`

---

## 1) Local environment

- Java: The project compiles with `--release 17`. Any JDK ≥ 17 works (17, 21, 25, …) because the compiler emits Java 17 bytecode.
- Maven: Use the Maven Wrapper provided in this repo for reproducible builds.

Check tool versions (optional):
```bash
./mvnw -v
java -version
```

If `./mvnw` is not executable, run `chmod +x mvnw`.


## 2) How to build locally

Run these from the repo root:
```bash
# Clean and run tests (preferred during development)
./mvnw -B -ntp clean test

# Package the library (runs tests)
./mvnw -B -ntp package

# Install to local Maven repository (for local consumers)
./mvnw -B -ntp install
```

Notes:
- Keep builds non‑interactive (`-B -ntp`) to be CI/agent friendly.
- Before opening a PR, ensure tests pass locally.


## 3) Submitting changes (humans & agents)

1. Align scope with `docs/ROADMAP.md`. When scope is new or unclear, create an increment issue per `docs/INC_ISSUE_AUTHORING.md`.
2. Keep PRs small and focused.
3. Before pushing:
   - `./mvnw -B -ntp clean test` passes locally
   - Update docs only where behavior changed materially (see the process docs)
4. In the PR description, reference the corresponding increment (e.g., `INC-3`).


## 4) Guidance for GitHub Coding Agents

- Treat `.github/copilot-instructions.md` as the single source for guardrails, standards, and testing expectations.
- Use the wrapper and non‑interactive flags:
  ```bash
  ./mvnw -B -ntp clean test
  ```
- If scope is ambiguous, produce a clarification doc per `docs/INC_ISSUE_AUTHORING.md` and wait for approval before implementation.


## 5) Troubleshooting

- “mvn: command not found”
  - Use the wrapper: `./mvnw ...`
- Java version errors
  - We compile with `--release 17`. Ensure your JDK is ≥ 17. If multiple JDKs are installed, set `JAVA_HOME` to a compatible JDK.
- Cannot resolve internal artifacts
  - This repo is self‑contained and vendors minimal replacements for internal annotations and currency enum. Do not add back private dependencies.
- Jackson JSON failures
  - Ensure `MoneyJacksonModule` is registered in your `ObjectMapper` for serialization tests.


## 6) Useful references

- Guardrails & standards: `.github/copilot-instructions.md`
- Increment authoring & clarification: `docs/INC_ISSUE_AUTHORING.md`
- Roadmap & increment scopes: `docs/ROADMAP.md`
- Issue template (if present): `.github/ISSUE_TEMPLATE/increment-issue.md`
