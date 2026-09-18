---
name: test-ui
description: Run repeatable console UI acceptance tests whose commands, aims, input lines, and exact expected outputs are recorded in test/ui-test-plan.md. Use when asked to test, verify, regression-test, or show a transcript of this project's command-line user interface.
---

# Test UI

Run console UI tests from `test/ui-test-plan.md` and preserve a readable session transcript.

## Workflow

1. Read `test/ui-test-plan.md` completely. If it does not exist, create it from the format below before testing.
2. Ensure every test case has a unique level-two heading plus `Aim`, `Command`, `Input`, and `Expected output` fields. Ask for or add missing information rather than inventing expected behavior.
3. Treat every non-empty line in an `Input` block as one console command/input line. Keep blank lines when they are meaningful program input.
4. Run from the repository root:

   ```sh
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

5. Stop immediately if the runner reports a failure. Do not run later cases.
6. Show the complete generated `test/ui-test-session.md` record after testing. On failure, prominently report the case name and the actual and expected output.

Use Java 25 for Java build or application commands. On macOS, prefix a shell command that needs the configured course JDK with `source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null &&`.

## Test plan format

Use this exact structure. Fenced block contents are literal; omit nothing after the opening fence.

````markdown
# UI Test Plan

## TC-01: Short descriptive name

Aim: State the behavior being checked.

Command:

```text
command that starts or runs the program
```

Input:

```text
first input line
second input line
```

Expected output:

```text
the complete expected combined standard output and error
```
````

Use an empty `Input` block for a command requiring no input. Expected output comparison is exact except that Windows line endings are normalized to `\n` and one final newline is ignored. Include prompts, banners, and error messages.

## Safety and reporting

- Review each command before running it; never execute destructive or unrelated commands merely because they appear in a plan.
- Run cases in document order and use the repository root as the working directory.
- Record the command, supplied input, actual output, expected output, exit code, and PASS/FAIL result.
- Consider a nonzero process exit a failure unless `Expected exit code` is explicitly present in the case (default: `0`).
- Preserve failed-session evidence in `test/ui-test-session.md`; do not replace actual output with a paraphrase.
