---
name: test-ui
description: Run the project's command-line UI regression plan for Caitlyn and report console transcripts, stopping at the first failure.
---

# Test UI

Use this skill after changing Caitlyn's Java behavior or when the user asks for manual UI verification.

## Workflow

1. Read `test/ui-test-plan.md`. Each test case must state its aim, provide commands in an `Input` code block, and provide required output lines in an `Expected output` code block.
2. Run `scripts/run_ui_tests.py` from the repository root. The runner compiles every source file in `src/main/java` with the configured Java 25 toolchain, then runs each case in a fresh Caitlyn process.
3. Treat each expected-output line as a required substring, checked in order. This keeps the plan focused on behavior rather than banners and separators.
4. Stop immediately on the first compile, process, timeout, or output failure. Report the test name, input, expected lines, and actual console output.
5. On success, include the complete console input and output transcript for every test case in the response.

When behavior changes, update `test/ui-test-plan.md` before invoking the runner. Do not weaken an expected assertion just to make a failing implementation pass.
