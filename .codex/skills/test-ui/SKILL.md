---
name: test-ui
description: Run command-line UI test cases from test/ui-test-plan.md, compare actual output with expected output, and stop at the first failure while reporting the complete console session.
---

# Test UI

Run the project's interactive command-line tests exactly as recorded in [test/ui-test-plan.md](../../../test/ui-test-plan.md).

## Test-plan format

Each `## Test case:` section must contain:

- `### Aim` — what behavior the case verifies.
- `### Command` — the shell command that starts the program.
- `### Input` — a fenced text block containing the console input, one command per line.
- `### Expected output` — a fenced text block containing the exact expected stdout.

The plan may also include `Timeout seconds` near the top. Keep every test case's aim, input, and expected output in the plan; do not invent undocumented cases during a run.

## Run the tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The runner executes cases in file order, passes the Input block to each command, and compares stdout exactly after normalizing only platform line endings and the final newline. It stops immediately at the first non-zero exit, timeout, or output mismatch. It prints a console transcript for every completed case and, for a failure, prints both expected and actual output before exiting non-zero.

Use `--timeout` to override the plan's timeout or pass a different plan path. Do not open a browser or alter the test plan automatically. If the program needs compilation first, record the appropriate build step in the Command field or build it before running the plan.
