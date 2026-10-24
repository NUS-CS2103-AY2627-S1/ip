---
name: test-ui
description: Compile and test Orbit's command-line interface from the repository's Markdown UI test plan. Use after Java behavior changes or when asked to verify iP commands and console output; do not use for GUI or unit-test tasks.
---

# Test UI

Run repeatable, fail-fast console tests while keeping `test/ui-test-plan.md` as the readable source of truth.

## Workflow

1. Work from the repository root.
2. Review `test/ui-test-plan.md`. When behavior changes, add or update cases before testing. Each case must state its aim, complete input session, and expected output fragments in order.
3. Ensure Java 25 is active. The runner accepts `--java-home <JDK_DIRECTORY>` when `JAVA_HOME` or the system Java is not Java 25.
4. Run:

   ```text
   python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
   ```

5. Read the console transcript printed for every case. Stop at the first failure and report its actual output plus the missing expected fragment. Do not commit code until all cases pass.

The runner compiles the console entry point and its transitive dependencies from `src/main/java` into the ignored `_temp/ui-test-classes` directory. JavaFX classes are compiled and tested separately by Gradle. It does not write inside the source tree or preserve application data between test cases.
