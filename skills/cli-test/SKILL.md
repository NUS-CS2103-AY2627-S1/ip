---
name: cli-test
description: Test command-line applications adversarially through scripted stdin and captured output. Use when a CLI workflow, command parser, or terminal interaction needs functional, edge-case, or regression testing.
metadata:
  short-description: Adversarial CLI testing
---

# CLI Test

Test the command-line application as a user would, trying to expose incorrect output, crashes, state corruption, and input-handling bugs.

## Workflow

1. Inspect project instructions and identify the supported build/run command.
2. Use the required runtime version from those instructions.
3. Build before testing. If the required runtime is unavailable, report that limitation clearly.
4. Run focused scenarios through scripted stdin. Include normal flows and malformed, empty, repeated, boundary, and special-character inputs relevant to the change.
5. Check exit status, stderr, and stdout against observable expectations. Do not call a test passed without concrete evidence.
6. Report each assertion as exactly one marker:

   `STEP_PASS|<step-id>|<evidence>`

   or

   `STEP_FAIL|<step-id>|<expected> → <actual>`

7. For failures, include the command sequence, expected behavior, actual behavior, and a concise fix suggestion.

## Java CLI guidance

- Prefer compiling into `/tmp` so generated `.class` files do not enter the repository.
- Compile all Java source files recursively when the project uses source subfolders.
- Use Java 25 when available, as required by this project.
- Test EOF as well as the normal exit command.
- Verify state transitions independently from display formatting.
- Do not commit, tag, or push unless explicitly requested.

## Duke Level 4 checks

When testing the Duke Level 4 increment, verify that only `todo`, `deadline`,
and `event` create tasks. Confirm that an unrecognised command does not change
the task count, and that list output identifies each task as `[T]`, `[D]`, or
`[E]` before its completion status.

When a command is added, test its successful result, malformed input, and its
effect on the task list. For `delete`, confirm later tasks shift forward and an
invalid number leaves the list unchanged. For `help`, confirm every supported
command and its required syntax are listed.

## Scope boundary

This skill does not use browser automation and does not assess DOM accessibility, responsive layout, or web visual design. For browser-rendered applications, use a browser UI-testing skill instead.
