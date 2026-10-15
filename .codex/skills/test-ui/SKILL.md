---
name: test-ui
description: Run command-line UI test cases for Amy from a Markdown plan, compare actual and expected output, and stop at the first failure.
---

# Test UI

Use this skill when the user provides commands and expected console output to test Amy's interactive command-line behavior.

## Test-plan format

Record the cases in \`test/ui-test-plan.md\`. Each case must include an aim, an Inputs
fenced block, and an Expected output fenced block:

\`\`\`markdown
## Test case 1: Add and list a task

**Aim:** Confirm that a task is stored and shown by \`list\`.

**Inputs:**
\`\`\`text
read book
list
bye
\`\`\`

**Expected output:**
\`\`\`text
...expected complete console output...
\`\`\`
\`\`\`

Keep the expected output exact, including status markers and punctuation. Include
the initial greeting and farewell if they are part of the program output.

## Run tests

From the repository root, run:

\`\`\`bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py \
  test/ui-test-plan.md \
  --program java src/main/java/Amy.java \
  --session-log test/ui-test-session.log
\`\`\`

The runner executes each test case in its own process, sends the listed inputs,
and compares stdout with the expected output. It records each case's console
input and actual output in \`test/ui-test-session.log\`.

If a case fails, the runner stops immediately and reports the case, expected
output, and actual output. Do not continue to later cases until the failure is
resolved. A successful run reports every passed case and the session-log path.
