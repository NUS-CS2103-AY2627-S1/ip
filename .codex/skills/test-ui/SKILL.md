---
name: test-ui
description: Run documented, fail-fast console UI test sessions from test/ui-test-plan.md. Use when Codex needs to create, update, or execute text-based UI acceptance tests; feed command lists to an interactive program; compare complete console output with exact expectations; preserve an input/output transcript; or diagnose a console UI test failure.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI tests. Record the test cases before running them, then execute them in file order with the bundled runner.

## Record the plan

Create or update `test/ui-test-plan.md` from the repository root. Include relevant setup information above the cases, such as build instructions, required runtime, assumptions, and state-isolation requirements. Follow repository instructions such as `AGENTS.md`; for this project, use Java 25 for Java build and run commands.

Give every case this exact structure:

````markdown
## Test Case: <unique ID and title>

### Aim

<behavior or requirement being verified>

### Command

```json
["program", "argument-1", "argument-2"]
```

### Inputs

```text
first console command
second console command
```

### Expected output

```text
complete expected console output, including prompts and whitespace
```
````

Use one launch command per case, expressed as a JSON array without shell operators. Put the ordered console commands sent to the program in `Inputs`. Record the program's complete expected console output, not only the lines that differ between cases. An empty input or output is represented by an empty fenced block.

Do not derive or silently rewrite expected output from a failing run. Change expectations only when the requirements themselves changed, and explain that change to the user.

## Prepare the program

Complete required setup or compilation before starting the test session. Keep test cases independent: reset files, storage, or other persistent state between cases when the application does not do so itself. Do not run destructive cleanup unless it is clearly authorized.

## Run the session

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py \
  --plan test/ui-test-plan.md \
  --transcript _temp/ui-test-session.txt
```

The runner executes each case in a fresh process, sends its input exactly as recorded, combines standard output and standard error in console order, normalizes CRLF line endings to LF, and otherwise compares output exactly. It stops immediately on a timeout, nonzero exit code, or output mismatch.

## Report results

Show the resulting console input/output record to the user and link `_temp/ui-test-session.txt`. Summarize which cases passed. On failure, prominently report the failed case, actual output, expected output, and that later cases were not run. Preserve the failed transcript; do not continue the session after a failure.
