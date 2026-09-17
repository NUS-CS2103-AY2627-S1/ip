---
name: test-ui
description: Run project command-line UI test cases defined in test/ui-test-plan.md, compare each actual console transcript with its expected output, stop immediately on the first failure, and report the session transcript.
---

# Test UI

Use this skill when asked to execute the project's scripted console/UI tests.

## Procedure

1. Read `test/ui-test-plan.md`. Each test case must include an aim, a command, inputs, and expected output. The command and expected output are fenced code blocks; inputs are optional fenced code blocks.
2. Run test cases in document order from the repository root. Use Java 25 for Java commands (`sdk use java 25.0.3.fx-zulu` when needed).
3. For each case, send the listed input exactly, capture stdout and stderr as one console transcript, and compare it with the expected output exactly after normalizing only platform line endings (`CRLF` to `LF`). Do not ignore prompts, whitespace, or extra lines.
4. Print a clear record containing the test case name, command, console input, actual console output, and PASS/FAIL result.
5. If a case fails, stop immediately. Report both the actual and expected output and do not run later cases.
6. If all cases pass, report the complete session transcript and the number of passed cases.

Prefer the bundled `scripts/run_ui_tests.py` runner for consistent parsing, fail-fast behavior, and transcript formatting:

```sh
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

Do not edit the test plan to make a failure pass. If the plan is malformed, stop and report the missing field or malformed code block.
