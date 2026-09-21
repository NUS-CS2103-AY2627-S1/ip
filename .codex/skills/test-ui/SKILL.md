---
name: test-ui
description: Run exact console UI tests for the Twizzy Java application from command and expected-output lists stored in test/ui-test-plan.md. Use when adding or changing Twizzy commands, checking user-visible output, running the project UI test plan, or showing a reproducible console test transcript.
---

# Test UI

Run Twizzy against `test/ui-test-plan.md`, compare exact output, and show the complete input/output transcript.

## Workflow

1. Read `test/ui-test-plan.md` from the repository root.
2. When the user supplies commands and expected outputs, add or update a test case in that file before running it. Preserve its Aim, Inputs, and Expected output fields.
3. Ensure `javac -version` reports Java 25. On macOS, try `sdk use java 25.0.3.fx-zulu` if needed.
4. Run this command from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

5. Show the runner's console transcript to the user.
6. If a case fails, report its actual output, expected output, and diff immediately. Do not run later cases.

## Test-plan format

Use one `##` heading per case. Include an `**Aim:**` line and fenced `text` blocks under `**Inputs:**` and `**Expected output:**`.

Use `{{STARTUP}}` for Twizzy's complete startup output and `{{DIVIDER}}` for its divider. The runner expands these tokens before exact comparison. Keep commands out of expected output because redirected standard input is recorded separately and is not echoed by Java.

The runner starts a fresh Twizzy process for each case and stops at the first compilation, Java-version, parsing, timeout, stderr, exit-status, or output-comparison failure.
