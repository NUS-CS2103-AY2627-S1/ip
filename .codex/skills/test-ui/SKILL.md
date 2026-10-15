---
name: test-ui
description: Run the project's documented console UI test cases and report the first mismatch with its full input/output transcript.
---

# UI Test

Use this skill to verify the console interaction of this Java project.

1. Add or update cases in `test/ui-test-plan.md`. Each `## Test:` section must contain an aim, an `Input` fenced `text` block, and an `Expected output` fenced `text` block. Separate output fragments with a line containing `---`; each fragment must appear exactly and in order. This lets a case verify several state snapshots without including divider lines.
2. Run `powershell -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1` from the repository root. The script compiles the Java sources with Java 25 and runs `DeviceG.boot()` through a temporary test launcher, bypassing the aesthetic introduction.
3. Inspect the generated `test/ui-test-session.log`. On the first failed case, the script stops and shows the expected excerpt and the actual output.

Do not change the plan merely to hide a failure; update expected output only when it reflects intended UI behaviour.
