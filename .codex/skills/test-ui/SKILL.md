---
name: test-ui
description: Use when testing Tasque console behavior after Java changes or checking CS2103T command and output regressions.
---

# Test Tasque UI

Run reproducible console tests against the current Tasque sources. Use
`test/ui-test-plan.md` by default.

## Test workflow

1. Read the selected test plan. Every case must contain an ID, aim, complete
   input sequence, and complete expected stdout.
2. If the user supplies ad-hoc cases, represent them with the same schema in a
   temporary plan under `_temp/test-ui/`. Add them to the persistent plan only
   when they are intended as durable regression coverage.
3. From the repository root, run:

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1
   ```

   Pass `-PlanPath <path>` to run another plan.
4. On success, report the result and reproduce the runner's separate INPUT and
   OUTPUT transcripts for manual inspection.
5. On failure, stop. Report the failing case, expected stdout, actual stdout,
   standard error or exit details when present, and confirm that later cases
   were not run.

## Test-plan schema

Use this exact structure so the runner can parse it:

````markdown
## TC-ID: Short title

**Aim:** Behavior this case protects.

### Inputs

```console-input
command
bye
```

### Expected output

```console-output
complete Tasque stdout only
```
````

Expected-output blocks contain process stdout only; never include terminal
input echo. Preserve intentional spaces and blank lines. Do not update an
expectation merely to hide a regression: change it only when an approved
requirement intentionally changes observable behavior.

Generated classes, captures, and transcripts belong under `_temp/test-ui/`.
Do not modify production code as part of running this skill.
