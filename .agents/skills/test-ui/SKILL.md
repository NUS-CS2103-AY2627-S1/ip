---
name: test-ui
description: Run the iP command-line application against the interaction cases recorded in test/ui-test-plan.md. Use after application code changes, when command behavior changes, or when asked to verify the CLI. Update the plan first when expected behavior changes; stop at the first failure and show the full input, actual output, and missing expectation.
---

# Test the Command-Line UI

Verify Green Chonk through repeatable user-facing interactions rather than isolated ad hoc commands.

## Workflow

1. Treat the current Git repository as the project root unless the user identifies another repository.
2. Read `test/ui-test-plan.md`. Confirm each affected behavior has a case containing an aim, input lines, and ordered expected output fragments.
3. Update the plan before testing when a code change intentionally changes commands or output.
4. Run the bundled harness from the project root:

   ```bash
   python3 .agents/skills/test-ui/scripts/run_ui_tests.py . test/ui-test-plan.md
   ```

5. Report the compilation result and case summary. The harness prints the console input and output for every executed case.
6. If a case fails, stop immediately. Report its aim, the expected fragment that was not found, and the actual output; do not hide the failure by weakening an expectation unless the product requirement changed.

## Test-Plan Contract

Keep one fenced `json` object in `test/ui-test-plan.md` with this shape:

```json
{
  "main_class": "greenchonk.GreenChonk",
  "cases": [
    {
      "name": "short-stable-name",
      "aim": "Behavior this case verifies",
      "inputs": ["command one", "bye"],
      "expected": ["first ordered output fragment", "later fragment"]
    }
  ]
}
```

Use complete meaningful lines for expected fragments. Keep them in output order. Input strings may be empty when testing a blank command, but expected fragments must not be empty. Include `bye` so each session terminates normally. Test valid behavior for the current increment; add malformed-input cases when the increment introduces error handling.

## Constraints

- Use the repository’s required Java version; the harness rejects a compiler whose major version is not 25.
- Compile all Java files under `src/main/java` into a temporary directory.
- Do not write class files into the repository.
- Do not modify application code merely to make a mistaken test pass.
- Do not commit, tag, push, or delete files as part of testing.
