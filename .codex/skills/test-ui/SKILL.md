---
name: test-ui
description: Run command-line UI test cases from test/ui-test-plan.md, compare each actual output with its expected output, and stop immediately on the first failure.
---

# Test the command-line UI

Use this skill for scripted black-box tests of the project's interactive command-line program.

## Test plan

Keep the test cases in `test/ui-test-plan.md`. Each test case must contain:

- an `Aim:` line;
- one or more `### Command` sections containing the exact input command in a fenced `text` block; and
- a matching `### Expected output` section containing the exact expected stdout in a fenced `text` block.

The runner executes every command in a fresh process, so each case is isolated. The expected output must include all output written to stdout for that command, including prompts, separators, and newlines. If stderr matters, configure the command in the runner invocation or document it separately; normal UI assertions compare stdout.

## Run the tests

Run from the repository root:

```bash
python3 .codex/skills/test-ui/scripts/run-ui-tests.py \
  --command 'java -cp out/production/CS2103T Monkey' \
  --plan test/ui-test-plan.md
```

Use the project's actual build/run command when it differs. The runner accepts `--command` as a shell-like command string and `--plan` as the test-plan path.

## Required behavior

For each test case and command:

1. Start the program and send the command followed by a newline.
2. Capture stdout and stderr separately; compare stdout exactly with the expected block.
3. Print a console-session record showing the input, actual output, and pass/fail result.
4. If the test fails, stop immediately and report both the expected and actual outputs. Do not run later cases.

Exit with status 0 only when all cases pass; exit nonzero on a failed test, malformed plan, timeout, or program error.

The implementation is in `scripts/run-ui-tests.py` and uses only Python's standard library.
