---
name: test-ui
description: Run console UI tests from test/ui-test-plan.md for this Java chatbot project. Use when asked to test interactive command-line behavior, verify commands against expected output, record test cases, run scripted input sessions, or report console transcripts and first-failure details.
---

# Test UI

Run scripted console UI tests against the current project. Test cases live in `test/ui-test-plan.md`; create or update that file before running tests when the user supplies new command and expected-output lists.

## Test Plan Format

Keep the plan in Markdown. Each test case must include:

- `## Test Case: <name>`
- `Aim: <what the test verifies>`
- one fenced `input` block containing commands, one command per line
- one fenced `expected` block containing one expected output chunk per command, separated by a line containing only `---`

Example:

````markdown
## Test Case: Mark a task as done

Aim: Verify that `mark 1` marks the first task as done.

```input
return book
mark 1
bye
```

```expected
added: return book
---
Nice! I've marked this task as done:
  [X] return book
---
Nice seeing you. Until next time!
```
````

The runner checks each expected chunk as text that must appear in the output produced after the matching command. This keeps tests focused on meaningful chatbot responses instead of banners, separator lines, or indentation noise.

## Run Tests

1. Confirm `test/ui-test-plan.md` contains the relevant test cases. If the user provides test cases inline, record them there first.
2. Run from the repository root:

   ```bash
   python .codex/skills/test-ui/scripts/run-ui-tests.py
   ```

3. If the app needs a different command, pass it explicitly:

   ```bash
   python .codex/skills/test-ui/scripts/run-ui-tests.py --program "java -cp src/main/java nico.Nico"
   ```

4. Use Java 25 for project runs. If compilation is needed, use the build command recorded in the test plan or pass `--build "<command>"`.

## Reporting

After testing, report the console transcript printed by the runner, including each user input and the output observed after it.

If a test fails, stop there and report:

- test case name
- command that failed
- expected output
- actual output
- transcript so far

Do not continue to later test cases after a failure.

## Resource

`scripts/run-ui-tests.py` parses `test/ui-test-plan.md`, runs the interactive program, and stops on the first mismatch. It uses only Python's standard library.
