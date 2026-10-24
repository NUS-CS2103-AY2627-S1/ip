---
name: test-ui
description: Test this CS2103 iP command-line chatbot from test/ui-test-plan.md, with optional pass-only commit/tag/push finishing for a level.
---

# Test UI

Use this project-specific skill when the user asks to test the iP chatbot UI, test a CS2103 iP level, or test and finish a level.

## Test Plan

All UI test cases live in `test/ui-test-plan.md`. Keep the file easy to edit as future iP levels add commands.

Each test case must use this format:

```text
### Test Case: <name>

Aim:
<what this test is checking>

Inputs:
<commands entered into the chatbot, in order>

Expected Output:
<expected console output>
```

To test startup loading from disk, a test case may include this optional block between Aim and Inputs:

```text
Initial Saved Data:
<contents to write to data/kiki.txt before this test starts>
```

The runner compares output strictly after normalizing line endings and ignoring only trailing whitespace/newline differences at the very end. Do not relax differences in wording, task numbering, `[T]`, `[D]`, `[E]`, `[X]`, `[ ]`, ordering, error messages, task descriptions, or dates/times.

## Running Tests

From the project root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The script inspects the repository to determine how to compile and run the chatbot. For this plain Java iP repository, it compiles `src/main/java/*.java`, locates the class containing `public static void main`, and runs that class.

If any test fails, the runner stops immediately and reports the failed test case, aim, inputs, expected output, actual output, and a unified diff. It must not commit, tag, or push after a failure.

If all tests pass, the runner prints every recorded console session first, then prints:

```text
ALL UI TESTS PASSED
```

The recorded console sessions include user inputs prefixed with `> ` and the program output. If `--finish` is used, the runner also prints `ALL UI TESTS PASSED` again after the finish flow completes successfully. Failed test runs must not print `ALL UI TESTS PASSED`.

## Optional Level Finishing

Only after all tests pass, the runner can finish a level when `--finish` is provided. It will ask for the level/tag and commit message if they are not already set:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py --finish
```

When prompted, enter either a plain number, such as `4`, or a normal string tag name, such as `A-MoreOOP`. A plain number is turned into the full `Level-X` tag (e.g. `4` becomes `Level-4`); any other string is used as the tag name as-is. Tag names cannot contain whitespace.

You can also provide them up front:

```bash
LEVEL=4 COMMIT_MESSAGE="Add task completion support" python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

`LEVEL` also accepts a plain string tag, e.g. `LEVEL=A-MoreOOP`.

Before committing, it runs `git status`, shows the files about to be committed, checks that the branch is `master`, and checks that the tag does not already exist. If anything is unexpected, it stops and reports the issue.

When finishing is safe, it runs this CS2103 iP sequence:

```bash
git add .
git commit -m "<COMMIT_MESSAGE>"
git tag <LEVEL>
git push origin master
git push origin <LEVEL>
```

Never amend, force-push, reset, delete tags, or overwrite an existing tag automatically.
