---
name: test-ui
description: Test Lumi's console user interface from command-and-output cases in test/ui-test-plan.md. Use after Java code updates that can affect behavior or output, or when asked to run, add, or revise console UI regression tests.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI cases.

## Workflow

1. Read the plan before testing. If the user supplies commands and expected outputs, add or update cases in the plan first. Every case must include its aim, its ordered input commands, and one expected response per command.
2. Update the plan when a code change intentionally changes observable console behavior or introduces behavior that is not covered. Do not change expectations merely to make an unintended regression pass.
3. From the repository root, run:

   ```text
   python .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

   The runner verifies Java 25, compiles the application in a temporary directory, starts a fresh application process for each case, compares responses in order, and prints the console input/output record.
4. If a case fails, stop at that first failure. Report the failing case and command together with the actual and expected output printed by the runner. Do not change production code unless the user also asked for a fix.
5. If every case passes, report the pass count and include the console session record produced by the runner when handing off test results.

## Test plan format

Follow the format already used in `test/ui-test-plan.md`. Put commands in the `Inputs` text fence. Put the corresponding response blocks in the `Expected outputs` text fence, in the same order, separated by a line containing only `---`.

Use `<EMPTY>` on an input line to test an empty command. Interleave invalid commands with valid commands and later `list` checks when state preservation matters; this detects error paths that accidentally add or modify tasks.

Expected blocks contain the text inside Lumi's divider lines. The runner checks divider placement separately, ignores platform line-ending differences and trailing whitespace, and preserves leading whitespace. Startup output is shown in the transcript but is not repeated in every expected block.
