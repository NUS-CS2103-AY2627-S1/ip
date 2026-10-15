---
name: test-ui
description: Record and run exact console input/output tests for the peanutbuttercat Java app. Use when the user supplies commands and expected console output or asks to execute, repeat, or review manual UI test cases; do not use for JUnit or other unit tests.
---

# Test the console UI

Turn the user's command/output lists into reproducible test cases, run each case in a fresh `PeanutButterCat` process, and show the complete test-session transcript.

## Record the plan

Before running tests, create or update `test/ui-test-plan.md` from the repository root. Preserve existing test cases unless the user asks to replace them.

Use the exact schema already present in that file:

- Give every case a unique `## Test case:` heading.
- Record a short, observable aim. If the user omitted one, infer it from the command and expected behavior.
- Put commands, one per line and in entry order, in the `Inputs` `text` block. Commands within one case share application state.
- Put only program output in the `Expected output` `text` block. Do not include shell prompts or echoed input.
- Keep the project configuration at the top current. Do not weaken the Java 25 requirement.

If a command or expected output is genuinely ambiguous, ask for the missing information before running that case. Do not invent a materially different expectation.

## Run the plan

From the repository root, run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1
```

The runner compiles all Java sources into a temporary directory, requires `javac` and `java` version 25, normalizes CRLF/LF differences, and ignores one final line terminator. All other characters, whitespace, and line ordering are compared exactly.

Run test cases in file order. Each case starts a fresh application process; never carry state between cases. The runner stops at the first failure. Do not continue, retry, modify the expectation to match the implementation, or run later cases after a failure unless the user explicitly asks for a new session.

## Report the session

Show the runner's complete transcript in the response, including each executed case's aim, every console input line, and actual console output. Summarize which cases passed.

On failure, clearly identify the failed case and reproduce both the `Actual output` and `Expected output` sections from the runner. State that later cases were not run. A missing or wrong Java version is a setup error before the test session, not a failed test case.
