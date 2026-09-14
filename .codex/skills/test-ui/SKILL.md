---
name: test-ui
description: Record and run exact-output tests for this project's command-line UI. Use after every code update in this repository, when given lists of console commands and expected outputs, when asked to create or update test/ui-test-plan.md, or when asked to run, repeat, or report manual-style UI tests with a fail-fast transcript.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI tests. Launch a fresh program process for each test case, feed its input commands in order, and compare the complete console output exactly.

## After code updates

1. Review the code change against every case in `test/ui-test-plan.md`.
2. Update the plan when the change intentionally adds or changes console UI behavior, commands, test aims, inputs, or expected outputs. Never rewrite an expectation solely to make the implementation pass.
3. If the plan remains applicable, leave it unchanged and report that it was reviewed.
4. Run the complete plan before treating the code update as complete.

## Record test cases

1. Read `AGENTS.md`, the application entry point, and existing run instructions before choosing the program command.
2. Create or update `test/ui-test-plan.md` with the format below. Preserve unrelated existing cases unless the user asks to replace them.
3. Record each user-supplied command and expected output verbatim. Do not change an expectation merely to make the current program pass.
4. Give every case a unique heading and a concise `**Aim:**` statement.
5. Put one console command per line in `#### Inputs`, in the exact order entered. Put the complete output for that fresh process in `#### Expected output`, including startup and exit text.

Use this required shape:

````markdown
# UI Test Plan

## Test configuration

### Program command

```shell
command that starts the program
```

- Timeout (seconds): 10
- Comparison: Exact console output after normalizing CRLF line endings to LF.

## Test cases

### TC-01: Short name

**Aim:** Explain the behavior under test.

#### Inputs

```text
first command
second command
```

#### Expected output

```text
complete expected output
```
````

Treat all content inside the input and output fences as significant, including blank lines and the final newline. Do not put Markdown code fences inside those blocks.

## Prepare the runtime

Use Java 25 for this project. On macOS, run `sdk use java 25.0.3.fx-zulu` in the same shell that launches the tests, then verify `java -version` reports Java 25. If `sdk` is unavailable, locate an installed Java 25 runtime rather than silently testing with a different version.

## Run the tests

From the project root, run:

```shell
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The runner must remain fail-fast. Do not add flags or wrappers that continue after failure. A test fails when its combined stdout/stderr differs from the expected output, it exits with a nonzero status, or it times out.

## Report the session

Show the runner's complete console transcript in the response so the user can see every input and output observed. Summarize which cases passed.

On failure, stop immediately and identify the first failed case. Show its actual output and expected output, plus the runner's diff. Do not run or report later cases as tested.

## Bundled script

Use `scripts/run_ui_tests.py` to parse the plan, execute cases, compare exact output, and print the fail-fast transcript. Update the script only when the plan format or execution semantics genuinely need to change.
