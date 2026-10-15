test-ui skill

Usage:
1. Edit `test/ui-test-plan.md` and add test cases in the JSON block.
2. Run the skill runner manually:
   powershell -NoProfile -ExecutionPolicy Bypass -File .\\.copilot\\skills\\test-ui\\run.ps1 --PlanPath test\\ui-test-plan.md

Behavior:
- Runs each test's command, sends optional stdin, captures stdout/stderr.
- Writes a session record to `test/ui-test-session.log` with command, input, expected and actual outputs.
- If any test fails, the runner terminates immediately and reports expected vs actual.
