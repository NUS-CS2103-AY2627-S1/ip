---
name: test-ui
description: Run the Kenbot text-UI (black box) test cases recorded in test/ui-test-plan.md. Each test case starts a fresh run of the program, types its input lines into the console, and compares the whole console output against the expected output. Use when asked to test the UI, run the text-UI tests, check the chatbot's console behaviour, add or update a UI test case, or verify that a command still prints what it should.
---

# Text-UI testing

Kenbot is a console program, so the cheapest useful test is to type commands
into it and check what it prints. This skill runs those tests from a single
plain-Markdown plan, so the test cases stay readable and reviewable rather than
being buried in a script.

## The test plan

All test cases live in `test/ui-test-plan.md`. That file is the source of
truth; the runner has no test data of its own. Each test case records:

* **Aim** - what the test case is checking, and why it exists.
* **Input** - the lines typed into the console, one per line.
* **Expected output** - everything the program should print after the greeting.

The shared greeting banner is recorded once in the plan's `Greeting` section and
is prepended to every case's expected output automatically, so it never has to
be repeated.

Read the plan's own "How this file is read" section before editing it; it
describes the exact format the runner parses.

## Running the tests

Use Java 25. On macOS, switch with `sdk use java 25.0.3.fx-zulu` if `java
-version` reports anything older.

From the repository root:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

This skill lives in `.codex/skills/test-ui/` and is symlinked into
`.claude/skills/test-ui/`, so Claude Code and Codex run the same files.
`.claude/skills/test-ui/scripts/run_ui_tests.py` resolves to the same script
and works identically. Edit the files under `.codex/` only.

Useful options:

* `--case TC3` runs a single test case (repeat the flag for several). Use this
  while fixing one failure, then run the whole plan again.
* `--plan PATH` uses a different plan file.
* `--src PATH` and `--main-class NAME` point at different sources or a
  different entry point, if the project is reorganised.
* `--timeout SECONDS` changes how long a run may take before it is treated as
  hung. The default is 10 seconds.

The runner compiles the sources into a temporary folder first, so it never
leaves `.class` files in the repository. If compilation fails, it reports the
compiler errors and runs no test case.

Each test case is then run from its own empty throwaway folder. Kenbot saves its
tasks to `data/Kenbot.txt` relative to the folder it is started from, so this
stops one case from reading the file another case saved, and stops the runner
from overwriting the real `data/Kenbot.txt` in the repository.

## What the runner reports

For every test case it prints the console session: the lines typed in (each
prefixed with `> `), then everything the program printed. Anything the program
wrote to standard error is shown separately, so a stack trace is visible even
when the expected output still matched.

Comparison ignores only what a reader could not see: trailing spaces at the end
of a line, and blank lines at the very end of the output. Everything else must
match exactly.

**The session stops at the first failure.** The runner prints the expected
output, the actual output, and a line-by-line difference, states that the
remaining test cases were not run, and exits with status 1. This is deliberate:
once the program is misbehaving, later results cannot be trusted, and one clear
failure is easier to act on than a wall of them.

## Adding a test case

1. Decide the aim first, and write it down. A test case whose aim cannot be
   stated in one sentence is usually testing too much.
2. Add a `## TCn - short title` section to `test/ui-test-plan.md` with the
   `**Aim:**`, `**Input:**`, and `**Expected output:**` parts.
3. Write the expected output from what the program *should* print, not from
   what it currently prints. Copying current output turns a bug into a rule.
4. End the input with `bye`. It is the only command that ends the program
   cleanly; running out of input crashes it.
5. Run `--case TCn` to check the new case, then run the full plan.

## Notes for this project

* These are black box tests of the console UI. They complement, and do not
  replace, the JUnit tests the iP rubric asks for on individual methods.
* A failure caused by a genuine bug should be fixed in `src/main/java`, not by
  editing the expected output to match. Only update expected output when the
  intended behaviour itself has changed.
* Do not commit or push as part of running these tests unless separately asked.
