---
name: test-ui
description: Run command-line UI test cases from a project test plan, compare actual output with expected output, and stop immediately on the first failure.
---

# Test UI

Use this project-specific skill when a user asks to exercise the program through its console interface using prescribed inputs and expected outputs.

## Test case format

Read `test/ui-test-plan.md` before running tests. Each test case must provide:

- an aim;
- the exact program-launch command, or a shared launch command defined in the plan;
- the console input lines, in order;
- the expected complete console output, in order.

The plan may contain a list of test cases. If the user supplies an additional list of commands and expected outputs, run those cases too, using the same format and rules.

## Execution

1. Work from the repository root and use Java 25. On macOS, switch with `sdk use java 25.0.3.fx-zulu` when that JDK is available.
2. Compile or otherwise prepare the program using the plan's setup command. Do not include setup output in a test case unless the plan explicitly expects it.
3. Run test cases in the order listed. Feed all input lines to one program process for that test case, preserving blank lines and line order.
4. Capture stdout and stderr separately when possible. Compare the captured output with the expected output exactly after normalizing only platform line endings (`CRLF` to `LF`) and one final trailing newline. Do not ignore prompts, whitespace, banners, or error messages.
5. Stop the session at the first mismatch or non-zero exit status. Do not run later test cases after a failure.

## Required report

After testing, show a console session record for every executed case, including:

```text
=== Test Case: <name> ===
INPUT:
<input lines>
OUTPUT:
<actual output>
RESULT: PASS
```

For a failure, show `RESULT: FAIL`, then show both `EXPECTED OUTPUT` and `ACTUAL OUTPUT`, identify the first difference when practical, and state that execution stopped immediately. Report any stderr and the process exit status as well. Never claim a test passed when the expected output was not checked.
