---
name: test-ui
description: Run UI-style acceptance tests for the chatbot CLI by feeding command lists to the program, matching stdout against expected output, and recording the exact console transcript.
---

# Test UI

Use this skill when the current code change affects the chatbot's interactive CLI behavior and you need to validate output against a written test plan.

## Required workflow

1. Update `test/ui-test-plan.md` when the behavior or expected output changed.
2. Run the UI test script from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py --plan test/ui-test-plan.md
   ```

3. The script must:
   - run the app once per test case
   - feed the case input as stdin
   - compare the actual stdout to the expected output exactly
   - print the console input and output for the test session
   - stop immediately on the first failing case and print both actual and expected output

4. If any case fails, report the failing case ID, the actual stdout, the expected stdout, and the command transcript.

## Test plan format

The file `test/ui-test-plan.md` contains a JSON block defining the app command and the list of cases. Keep it in this structure:

```json
{
  "app_command": ["java", "-ea", "-Dfile.encoding=UTF-8", "-cp", "{classes}", "sage.Sage"],
  "cases": [
    {
      "id": "todo-list",
      "aim": "Verify todo creation and listing.",
      "input": "todo borrow book\nlist\nbye\n",
      "expected": "... exact stdout ..."
    }
  ]
}
```

The JSON is the source of truth for test execution; the surrounding markdown is for human-readable documentation.

The runner requires Java 25, compiles the non-GUI application once, and starts each case in a separate temporary
directory. `{classes}` is replaced with the compiled class directory. Commands are argument arrays, never shell
scripts. A nonzero exit code or timeout also fails the case even when stdout matches. Line endings are normalized
to LF by text-mode capture so the same plan runs on Windows, macOS, and Linux.

Use `--jar build/libs/sage.jar` to run the same cases against a packaged JAR without compiling sources. This runs
the CLI entry point inside the JAR; it does not replace manual GUI testing.
