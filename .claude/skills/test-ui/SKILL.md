---
name: test-ui
description: Run Tally's console UI tests from test/ui-test-plan.md, checking the program's actual output against the expected output for each case. Use after any change to code under src/main/java/, before committing, or whenever asked to test, verify, or check the chatbot's behaviour.
---

# Testing Tally's console UI

> **Attribution:** this skill was written by Claude (AI), to requirements taken
> from the sample prompt in the CS2103T Week 2 project instructions.

Each case in `test/ui-test-plan.md` lists commands to type and the console output
they should produce. The runner replays each case through the program and
compares what was printed against what was expected.

## Running the tests

```bash
python3 test/run-ui-tests.py
```

Add case IDs to run a subset, e.g. `python3 test/run-ui-tests.py TC-03 TC-05`.

The runner builds the project through Gradle first, so there is no need to
compile separately, and the cases run against the same classes and dependencies
the real build produces. It exits non-zero if anything fails.

## Reporting the result

Show the user the session transcript the runner prints — the commands typed and
the output returned for each case. That record is the point of the exercise: it
is what a person doing this by hand would have seen.

On failure the runner stops at the first bad case and prints the aim, the input,
the expected output, the actual output, and a diff. Relay all of it. Do not run
the remaining cases, and do not describe a failing run as passing.

## Keeping the plan current

Before running, check whether the change just made alters console output. If it
does, update `test/ui-test-plan.md` in the same change:

- **New command or behaviour** — add a case. Give it the next free `TC-nn`, and
  write an `**Aim:**` that says what the case proves, not what it types.
- **Changed wording or format** — update the expected output of every case that
  shows it. More than one case usually needs touching, since each expected block
  contains the full session including the greeting.
- **Removed behaviour** — delete the case rather than leaving it commented out.

Write the expected output from what the increment's specification requires, not
by pasting what the program currently prints. Pasting actual output turns the
test into a claim that today's behaviour equals today's behaviour, which cannot
fail and so proves nothing.

If a case fails, work out which side is wrong before touching anything. A
failure means the code and the plan disagree; changing the plan to match broken
code hides the bug rather than fixing it.

## Case format

````markdown
## TC-09 - Short title

**Aim:** What this case establishes, and which requirement it comes from.

**Input**
```text
todo read book
bye
```

**Expected output**
```text
(everything the program prints, including the greeting banner)
```
````

Trailing spaces and blank lines at the very end of the output are ignored when
comparing, so neither can cause a false failure.
