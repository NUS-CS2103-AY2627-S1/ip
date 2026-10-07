---
name: test-ui
description: Run scripted console UI tests for this Java project from test/ui-test-plan.md. Use when given lists of commands, inputs, and expected outputs; execute cases in order, compare output exactly, stop at the first failure, and show the console session.
---

# Test UI

Use this skill to test drax.Drax's console interface from a reproducible test plan.

## Workflow

1. Read `test/ui-test-plan.md`. Each test case must contain `Aim`, `Command`, `Input`, and `Expected output` fenced code blocks.
2. Treat each `Command` block as a shell command and each `Input` block as stdin. A blank `Input` block means no stdin.
3. Run the cases in document order with Java 25. On macOS, initialize SDKMAN and select `java 25.0.3.fx-zulu` before running Java commands when needed.
4. Compare the combined stdout and stderr with the expected output exactly, after normalizing only CRLF to LF. Do not ignore prompts, whitespace, ordering, or exit status. A non-zero exit status is a failure even if text matches.
5. Stop immediately at the first failed case. Report its aim, command, input, expected output, actual output, and exit status; do not run later cases.
6. After a successful run, show a numbered console session containing every command, stdin, and output. Also write the same session to `test/ui-test-session.md` so it can be reviewed.

Prefer the bundled runner for consistent behavior:

```bash
python3 skills/test-ui/scripts/run_ui_tests.py
```

The runner reads the plan, invokes one subprocess per case, compares output, stops on failure, and records the session. If the plan uses a command that needs shell features, keep those features inside the command block; the runner executes it with Bash.

Do not edit expected output to make a failing test pass. If output is nondeterministic, make the program or test setup deterministic first, or explain why exact comparison cannot be used.
