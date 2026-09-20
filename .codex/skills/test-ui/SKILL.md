---
name: test-ui
description: Run console UI tests for this Java chatbot project from test/ui-test-plan.md. Use when the user asks to test, verify, or review interactive command-line behavior against listed commands and expected output, including checking transcripts and stopping at the first failed UI test case.
---

# Test UI

Use this skill to verify the Java chatbot's console behavior against the project-specific UI test plan in `test/ui-test-plan.md`.

## Test Plan Format

Record all UI test cases in `test/ui-test-plan.md`. Each test case must include:

- a Markdown heading for the test case name
- `Aim:` explaining what the test checks
- `Inputs:` followed by a fenced `text` block containing the exact commands to type
- `Expected output:` followed by a fenced `text` block containing the exact expected console output
- optional `Initial data file:` followed by a fenced `text` block containing the starting `data/fifi.txt` contents
- optional `Expected data file:` followed by a fenced `text` block containing the expected `data/fifi.txt` contents after the run

Use this shape:

````markdown
## Test Case Name

Aim: Check the behavior being tested.

Inputs:
```text
command one
command two
bye
```

Expected output:
```text
full expected console output
```

Expected data file:
```text
full expected data/fifi.txt contents
```
````

Keep expected output exact, including the chatbot banner, separators, prompts, response text, and blank lines that should appear.

## Workflow

1. Read `test/ui-test-plan.md` and check that each test case has an aim, inputs, and expected output.
2. Run `scripts/run_ui_tests.py` from this skill folder against the repository root.
3. Let the script compile all Java files under `src/main/java`, run the program separately for each test case, and feed the listed inputs through standard input.
4. If all tests pass, report the passing cases and include the console transcript for each case.
5. If a test fails, stop immediately and report:
   - the failed test case name
   - the aim
   - the commands entered
   - the expected output
   - the actual output
   - any compile/runtime error output

Do not continue after the first failure.

## Running The Script

From the repository root:

```bash
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

Useful options:

```bash
python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md --main-class Fifi
python .codex/skills/test-ui/scripts/run_ui_tests.py --keep-build
```

If `python` is unavailable, use the bundled Codex Python executable when available.
