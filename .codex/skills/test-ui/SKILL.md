---
name: test-ui
description: Update and run JUnit tests, then run fail-fast command-by-command regression tests for this project's console UI from test/ui-test-plan.md, compare exact output, and record the session. Use after every application code update, when console commands or messages change, or when asked to test the chatbot UI.
---

# Test UI

Run the console test plan as an exact, interactive regression suite. Keep one application process alive within each test case so later commands can inspect state created by earlier commands.

## Workflow

1. Read `test/ui-test-plan.md` completely.
2. After every application code change, review the corresponding JUnit tests under `src/test/java`. Add or update conventionally named `*Test` classes to cover changed nontrivial public behavior and reasonable normal, boundary, and error cases.
3. From the repository root, select Java 25 and run the complete JUnit suite:

   ```bash
   source "$HOME/.sdkman/bin/sdkman-init.sh" && \
     sdk use java 25.0.3.fx-zulu >/dev/null && \
     ./gradlew test
   ```

4. Stop at the first failing JUnit test. Report the failing test and output, and do not continue to UI testing until the failure is resolved or the user directs otherwise.
5. If the code update changes commands, formatting, or observable console behavior, update the relevant UI cases and expected output before testing. Add a case when existing coverage does not exercise the change.
6. Ensure every UI case has an `id`, `aim`, and one or more exchanges containing `input` and `expected_output`. Mark the command that should terminate the application with `expect_exit: true`.
7. Run the UI suite with Java 25:

   ```bash
   source "$HOME/.sdkman/bin/sdkman-init.sh" && \
     sdk use java 25.0.3.fx-zulu >/dev/null && \
     python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

8. Preserve fail-fast behavior. On a mismatch, do not run another command or test case. Report the failed case, input, expected output, actual output, and the console record produced so far.
9. After a successful run, show the console input/output record and report the path `test/ui-test-session.md`.

## Test plan format

Store configuration and test cases in the first `json` code fence in `test/ui-test-plan.md`. Express each expected response as a list of exact console lines. Use variables such as `{{LINE}}` only when they are declared in the plan's top-level `variables` object.

The runner compiles all Java sources beneath the configured source directory, verifies that Java 25 is active, interacts with the program one command at a time, and writes a Markdown transcript. It requires only the Python standard library.

## Resource

Use `scripts/run_ui_tests.py` as the deterministic test runner. Do not replace its exact comparisons with substring checks.
