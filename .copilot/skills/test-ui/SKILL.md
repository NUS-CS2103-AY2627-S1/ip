---
name: test-ui
description: Run UI-style console tests by executing commands and comparing their console output against expected results. Abort immediately on first failure and report the mismatch.
---

# test-ui skill

This skill runs a sequence of console commands described in test/ui-test-plan.md and compares each command's captured console output against the expected output.

Usage:

  powershell -ExecutionPolicy Bypass -File .copilot\skills\test-ui\scripts\test-ui.ps1 test\ui-test-plan.md

Behavior:

- The test plan is a Markdown file (test/ui-test-plan.md) containing one or more test cases.
- Each test case must include: an ID, Aim, Command, and an Expected block (fenced with ```).
- The script runs each Command, captures stdout+stderr, normalizes newlines, and compares exactly to Expected.
- On mismatch, the script prints expected and actual outputs and exits with an error code (terminating the session).
- On success, the script prints a session log showing each command and its captured output.

Files created by this skill:
- .copilot/skills/test-ui/scripts/test-ui.ps1  -- the runner script
- test/ui-test-plan.md                         -- sample test plan (editable)

Examples of commands that can be tested: java -jar build\\app.jar, mvn -q -DskipTests package, or any console program available in PATH.

CI Integration:
- A GitHub Actions workflow (.github/workflows/test-ui.yml) is provided to run on push and pull_request. It runs a pre-check script that verifies whether the test plan needs updating (simple heuristics for built jars) and then invokes the test-ui runner. If the plan appears out of date the job exits non-zero so the developer can update and commit the plan locally.
- The CI job runs on windows-latest and invokes: .copilot\skills\test-ui\scripts\check-plan-and-run.ps1 test\ui-test-plan.md
