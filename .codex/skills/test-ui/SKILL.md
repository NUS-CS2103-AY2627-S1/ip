---
name: test-ui
description: Run the project's command-line UI test cases from test/ui-test-plan.md, compare each session with its expected output, and report the console transcript. Use when asked to test the Rei UI or its command/output behaviour.
---

# Test UI

Run the command-line UI test cases defined in `test/ui-test-plan.md`. Each case states its aim, stdin input, and expected stdout. The runner starts a fresh program process for every case, reports the input/output transcript, and stops as soon as one case fails.

## Run tests

1. Keep the program command and every test case in `test/ui-test-plan.md` using the documented format.
2. Compile the project with Java 25 before testing when its compiled classes are not current. Do not change the program merely to make a test pass.
3. Run the standard-library-only runner from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py test/ui-test-plan.md
   ```

   Use `--command "..."` only when the program command in the plan needs a temporary override.
4. Present the runner's console transcript. On a failure, report the failing case together with its actual and expected output; do not run later cases.

## Test plan format

The plan must begin with a `## Program command` heading followed by one `text` code block. Add each test as `## Test case: <name>` with an aim, input, and expected-output code block. The runner compares output exactly after normalising Windows and Unix line endings.

For a dynamic banner or timestamp that is not relevant to a test, place `{{ANY_PREFIX}}` as the first line of the expected-output block. It matches any preceding output; the remaining lines must still match exactly.

See [test/ui-test-plan.md](../../../test/ui-test-plan.md) for the project’s cases.
