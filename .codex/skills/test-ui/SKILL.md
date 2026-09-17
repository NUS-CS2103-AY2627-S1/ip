---
name: test-ui
description: Run this project's planned console UI tests with isolated data and exact output matching; diagnose failures or verify command changes.
---

# Test UI

Use this skill to run the console UI tests documented in
`test/ui-test-plan.md`. The plan is the source of truth: keep its test cases
up to date whenever the console interface changes.

## Test-plan format

Keep the following information in `test/ui-test-plan.md`:

- A test-environment section with an optional setup command and timeout.
- One `## Test case: <id>` section for every independent session.
- For each test case: an aim, the command that launches the program, the
  console inputs, and the exact expected console output.

Use fenced code blocks for inputs and expected output. Each case starts the
program in a fresh process, so do not rely on state from an earlier case.

When the application writes relative-path data, use these placeholders in the
run command to isolate that data for each case:

```markdown
**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/out/production bro.Bro`
```

The runner substitutes `{workspace}` with the repository's absolute path and
`{test_dir}` with a fresh temporary directory. This keeps the application code
using its normal relative path while preventing one test case's saved data from
affecting another.

To verify data loaded in a new application process, add an optional
`**Restart inputs:**` fenced block. The runner launches the same command a
second time in the same temporary directory, then compares the concatenated
output of both sessions with the expected-output block.

Use the setup and launch commands maintained in `test/ui-test-plan.md` and the
project build configuration. Do not copy a separate compile recipe into this
skill: package layout and JavaFX dependencies can change.

## Run the tests

1. Ensure Java 25 is active before running the setup or application command.
2. Update the plan before testing if commands or output changed.
3. Run the harness from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

   Pass `--plan path/to/plan.md` to use a different plan.

4. Summarize successful runs. For a failure, show the relevant transcript and
   mismatch; provide the full transcript when requested or needed to diagnose it.

The harness compares output exactly (apart from Windows versus Unix line
endings). It stops at the first failing case and prints the actual output,
expected output, exit status, and a unified diff. Each invocation stops there.
For a test-only audit, report the failure. During an authorized implementation
task, fix failures caused by the change and rerun the harness to complete the
remaining cases. Do not change expected output merely to hide a regression.

## Maintain the plan

Use a descriptive test-case id and an aim that states the behavior under test.
Keep expected output intentionally exact, including banners, prompts, blank
lines, and task-list formatting. If the user experience is intentionally
changed, update both the implementation and the expected output in the same
change.
