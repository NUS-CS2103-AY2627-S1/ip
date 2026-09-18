---
name: test-ui
description: Run console UI test cases recorded in a repository's test/ui-test-plan.md, comparing each session's output exactly and stopping at the first failure.
---

# Test UI

Use this skill when the user asks to run or validate a Java command-line user interface with a markdown test plan.

## Test source of truth

Read `test/ui-test-plan.md` before running tests. It contains one or more cases with this shape:

~~~markdown
## Test Case: short name
- Aim: what the case verifies

### Inputs
```text
command one
command two
```

### Expected Output
```text
the complete stdout transcript, including the startup banner
```
~~~

The commands in one inputs block are sent to a single program session in order, so state is preserved within that case. The expected-output block must match stdout exactly, apart from platform line-ending differences. Include the final newline in the block by keeping the closing fence on the line after the last output line.

## Running the tests

Run the bundled helper from the target repository root. When the skill is installed globally, use the helper from the directory containing this `SKILL.md`:

```powershell
pwsh -NoProfile -File <path-to-this-skill>/scripts/run_ui_tests.ps1
```

The helper enforces Java 25, compiles all Java files under `src/main/java` into a temporary directory, and runs the test cases in plan order. It prints each test case's console input and actual console output as the test-session record. Do not continue to later cases after a failure. A failing case prints both the expected and actual output and exits nonzero.

If the user supplies a different plan, main class, source directory, or timeout, pass the corresponding script parameters rather than changing the project files. When reporting results, preserve the helper's session record and identify the first failed case, if any.
