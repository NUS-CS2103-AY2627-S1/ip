---
name: test-ui
description: Run the project's scripted text UI test plan by feeding each test case's input commands to the program and comparing its console output with the recorded expected output. Use when asked to execute or update UI test cases.
---

# Test UI

Run the canonical text UI test cases recorded in [test/ui-test-plan.md](../../../test/ui-test-plan.md). The plan is the source of truth for the program setup command, program command, test aims, input commands, and expected output.

## Run the test plan

1. Treat the current project root as the working directory unless the user identifies another repository.
2. Read `test/ui-test-plan.md` before testing. Do not invent expected output during a test run; update the plan only when the user asks to change or add test cases.
3. Run the setup command recorded in the plan, if one is present. The current project uses Java 25, so use Java 25 for compilation and execution.
4. Run the bundled standard-library-only runner from the project root:

   ```text
   python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
   ```

   Use `python3` if `python` is unavailable. Pass `--repo <path>` only when testing a different repository.
5. The runner starts a fresh program process for each test case, sends all input commands in order, and compares the complete combined console output (stdout and stderr) with the expected output. It normalizes line endings and ignores only the final line ending.
6. Show the runner's console transcript in the response. It includes the input and output for every completed test case.
7. If a test case fails, stop immediately. Report the failing test case's aim, actual console output, and expected console output. Do not run or report later test cases as passed.

## Test-plan format

Keep every test case in `test/ui-test-plan.md` with an aim, a fenced input-command list, and a fenced expected-output block:

```markdown
## Test case 1: Short name
- Aim: What behavior this verifies.

### Inputs
```text
first command
second command
```

### Expected output
```text
complete console output for the commands above
```
```

Each non-empty line in `Inputs` is sent to the program as one command. The expected block must contain the complete output for that test case, including the startup and exit messages when the program prints them.

The plan's top-level metadata must include:

```markdown
- Setup command: `command used to compile the program`
- Program command: `command used to launch the UI`
- Timeout seconds: `30`
```

`Setup command` and `Timeout seconds` are optional; `Program command` is required. The runner uses a fresh process for each test case, so each case must be self-contained.

## Resource

`scripts/run_ui_tests.py` parses the plan, executes the commands, prints the console transcript, and exits at the first failed test case. It uses only Python's standard library; no third-party packages are required.
