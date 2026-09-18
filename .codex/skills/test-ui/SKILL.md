---
name: test-ui
description: Run Epi's isolated console regression tests from tests/test-plan.md, record the session, and stop at the first failure.
---

# Test UI

Use this skill after Java code changes that affect Epi's console behavior.

1. Update `tests/test-plan.md` when console behavior changes. Each case needs an aim, input, and complete expected response. The shared startup output is specified once at the top of the plan.
2. Use Java 25. From the project root, run `powershell -NoProfile -File .codex/skills/test-ui/scripts/run_ui_tests.ps1` (PowerShell 7 also works). The runner uses Gradle's `prepareUiTests` task to compile the app and obtain its full runtime classpath and Java executable. A failed build stops testing.
3. Each case runs `epi.Epi` in a fresh Java process and a unique temporary working directory. Never seed a case from the user's `data/` directory. JUnit storage tests also use temporary files.
4. Compare the entire output, including the shared startup, case-sensitively. Only trailing whitespace and platform line-ending differences are ignored. Unexpected or missing response lines fail the test. English locale settings apply only to test processes.
5. Print each test's console input and actual output. The latest transcript is saved to `build/reports/ui-tests/transcript.txt`.
6. Stop immediately on a mismatch, nonzero exit, stderr, or timeout; show expected and actual output. A case times out after 15 seconds by default (`-TimeoutSeconds` can change this). Failed-session files are retained for inspection; successful temporary sessions are removed.
7. Do not claim success unless every case passes. After a failure, fix the cause and start a new complete test run. `-PlanPath <path>` can select a separate plan when validating the runner itself.
