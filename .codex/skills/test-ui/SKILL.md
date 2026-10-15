---
name: test-ui
description: Execute console UI test cases defined in test/ui-test-plan.md, compare each program response with its expected output, and produce a transcript. Use when testing an interactive command-line interface in this project.
---

# Console UI testing

Use the test plan at `test/ui-test-plan.md` as the source of truth. If it is missing or does not contain the needed cases, create or update it before running tests.

## Test-plan format

The plan must include a **Launch command** (the exact command used to start the application) and one section per test case. Each test case must state:

- **Aim:** the behavior being checked.
- **Inputs:** the ordered console inputs. Represent each line exactly, including a blank line when one is entered.
- **Expected output:** the complete output expected after those inputs, with whitespace and line breaks preserved in a fenced `text` block.

Use this template:

```markdown
# UI test plan

## Launch command

```text
<command that launches the program>
```

## Test case: <short name>

- **Aim:** <behavior being checked>
- **Inputs:**

```text
<one console input per line>
```

- **Expected output:**

```text
<complete expected console output>
```
```

## Running tests

1. Read the whole plan and preserve its declared case order.
2. Run the launch command once per test case, feed the listed inputs in order, and capture all console output. Do not echo the supplied inputs as application output unless the program itself echoes them.
3. Compare actual output against expected output exactly, except for explicitly documented normalization rules in the plan. Do not silently trim output, ignore prompts, or accept partial matches.
4. Append a **Test session record** to `test/ui-test-plan.md`. For each attempted test, include the aim, the console input sent, the expected output, the actual output, and a pass/fail result. Preserve all content in fenced `text` blocks so the session can be reviewed and rerun.
5. On the first failure, immediately stop: do not run any remaining cases. Record the failed case and report both the actual and expected outputs clearly in the response.
6. If all cases pass, report the number of passed cases and link to the recorded session.

When the application cannot start, treat that as a failed test case. Record the command, its error output, and the expected output, then stop.

Use Java 25 for compilation and execution. Keep test-session records chronological; do not overwrite earlier sessions.
