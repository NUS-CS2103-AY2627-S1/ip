---
name: test-ui
description: Run and maintain fail-fast console UI tests recorded in test/ui-test-plan.md. Use after code changes or when given commands and expected console output to verify the Java application's user-visible behavior.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI tests.

## Workflow

1. From the repository root, read the whole test plan and confirm each case has an aim, commands, and exact expected output.
2. When the user supplies new or changed commands and expected output, add or update the relevant cases before testing. Keep existing useful coverage. Update configuration if the source directory, main class, or build output changes.
3. After any code update, review the plan against the changed behavior and update it if needed. Documentation-only changes do not normally require new cases, but the tests must still run when the project instructions require them.
4. Run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

5. Show the runner's complete test-session record in the response, including console input and output. If it fails, do not run later cases or retry with altered expectations. Report the failed case and its actual and expected output, then diagnose or fix only when requested.

## Test-plan contract

- Keep project settings under `## Configuration` using the exact fields `Source directory`, `Classes directory`, and `Main class`.
- Give every case a unique `## Test case: <name>` heading.
- Under each case, provide `### Aim`, `### Commands`, and `### Expected output` in that order.
- Put commands and expected output in `text` fenced blocks. Commands are lines sent to one fresh application process; expected output is that process's complete stdout, compared exactly after normalizing line endings.
- Keep cases independent. Put a terminating command such as `bye` in each command list when the application expects one.

The bundled runner compiles every `.java` file below the configured source directory, requires Java 25, runs cases in document order, prints a transcript for every attempted case, and stops at the first compilation, launch, exit-status, or output failure.
