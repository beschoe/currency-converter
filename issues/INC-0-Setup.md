# INC-0 — Local Build and Test Enablement (Self-contained Repo)

Goal: Make this repository compile and run tests locally with no access to Mercateo’s internal artifact ecosystem, so future changes by humans and AI agents build reliably on any clean machine.

This issue describes an explicit, minimal, and reversible sequence of tasks. We will execute them step-by-step, verifying at each checkpoint. Do not remove or weaken tests; always fix underlying behavior.

## Scope
- In scope (Milestone INC-0): Local build and test green (no network access to private repos required).
- Out of scope (later milestones): GitHub Actions / Coding Agent CI workflows, additional Copilot prompts, publishing artifacts.

## Assumptions
- You already copied the two internal dependency sources into `./extern/`.
- We will vendor minimal, API-compatible stubs into `src/main/java` so Maven can build offline.
- We’ll make `pom.xml` standalone (no private parent POM), and pin required plugin/dependency versions.

## Preconditions (one-time checks)
- Ensure Java 17 and Maven 3.6+ are available.

```bash
java -version
mvn -v
```

If Java < 17, install/activate JDK 17.

## Task A — Make the project self-contained (no private parents/deps)

- [x] A1) Detach from the private parent POM
- Edit `pom.xml` to remove the `<parent>` block.
- Add a `<groupId>` of your choosing (e.g., `com.example`) at the top level.
- Keep `artifactId` and `version` as-is unless you have a reason to change.

Acceptance criteria:
- The POM no longer references `com.mercateo.common:library-parent`.

- [x] A2) Vendor internal annotations (util-annotations)
- From `./extern/`, copy the minimal annotations into the following paths:
  - `src/main/java/com/mercateo/common/util/annotations/NonNullByDefault.java`
  - `src/main/java/com/mercateo/common/util/annotations/NonNull.java`
  - `src/main/java/com/mercateo/common/util/annotations/Nullable.java`

Content guidance (minimal):
- Package must be `com.mercateo.common.util.annotations`.
- The annotations can be no-op declarations, e.g., `@interface NonNull {}` etc.
- For `NonNullByDefault`, a SOURCE-retained type-level annotation is sufficient.

Acceptance criteria:
- Project compiles past references to these annotations.

- [x] A3) Vendor i18n KnownCurrencies enum
- From `./extern/`, copy the enum to:
  - `src/main/java/com/mercateo/common/i18n/KnownCurrencies.java`

Critical: Names AND ORDER must match `ConvertableCurrency` exactly (the code maps by `ordinal()` and tests compare sequences).

Add a static method:
```java
public static KnownCurrencies parseFromCurrency(java.util.Currency currency) {
    return valueOf(currency.getCurrencyCode());
}
```

Acceptance criteria:
- `ConvertableCurrencyTest` passes the i18n alignment assertions.

## Task B — Stabilize Maven build configuration (standalone POM)

- [x] B1) Configure Java 17 compilation
- In `pom.xml`, add `maven-compiler-plugin` (e.g., 3.11.0) with:
  - `source` = `17`
  - `target` = `17`
  - Optionally use `<release>17</release>`.

Example snippet (for later copy/paste when implementing):
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.11.0</version>
      <configuration>
        <release>17</release>
      </configuration>
    </plugin>
  </plugins>
</build>
```

- [x] B2) Ensure JUnit 4 test execution
- Add/pin `maven-surefire-plugin` (e.g., 3.2.5) so JUnit 4 tests are discovered consistently.

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.2.5</version>
</plugin>
```

- [x] B3) Pin test dependency versions (remove reliance on parent BOM)
- Add explicit versions:
  - `junit:junit:4.13.2`
  - `org.assertj:assertj-core:3.24.2` (or a stable newer version)
  - `org.mockito:mockito-core:4.11.0` (matches current usage) or 5.x if preferred
  - Keep `org.openjdk.jmh:jmh-generator-annprocess:1.37` (present already)
  - Add `org.openjdk.jmh:jmh-core:1.37` (scope `test`) to guarantee availability of annotations under all toolchains

Note: Lombok is declared but unused in code; you may keep or remove. SLF4J API is declared but unused; keep or remove — both are harmless.

Acceptance criteria:
- `pom.xml` no longer depends on parent-managed versions.
- `mvn -B -ntp -q dependency:tree` shows resolvable artifacts only from public repos.

## Task C — Verify build & tests locally

 - [x] C1) Clean build
```bash
mvn -B -ntp clean test
```

Expected outcome:
- Sources compile
- All tests pass

If failures occur, triage quickly:
- Missing classes: Verify copies from `./extern` landed in the exact target package paths and names.
- Enum mismatch: Ensure `KnownCurrencies` names and order mirror `ConvertableCurrency`.
- JMH annotation errors: Add `jmh-core` test dependency and/or ensure `maven-compiler-plugin` is present.

- [ ] C2) Package (optional, once tests are green)
```bash
mvn -B -ntp package
```

## Task D — Housekeeping (optional, still within INC-0)

 - [x] D1) Maven Wrapper
- Add Maven Wrapper to make the project reproducible across machines.

```bash
mvn -N -q -DoutputDirectory=.mvn/wrapper org.apache.maven.plugins:maven-wrapper-plugin:3.3.2:wrapper
```
- Commit `mvnw`, `mvnw.cmd`, and `.mvn/wrapper/*`.

- [ ] D2) Document local run in README (already mostly present)
- Add a minimal “How to build locally” section if desired:
```bash
mvn -B -ntp clean test
```

## Acceptance Criteria for Milestone INC-0
- `mvn clean test` succeeds locally on a clean checkout with Java 17, without access to any private artifacts.
- No tests disabled or removed.
- POM is standalone; all dependencies resolve from public repositories.

## Execution Notes
- We’ll implement tasks in order: A → B → C (D optional).
- At each subtask completion, we’ll run `mvn clean test` to catch regressions early.
- If you want me to proceed with the edits, I’ll apply them in small patches and validate the build after each step.
