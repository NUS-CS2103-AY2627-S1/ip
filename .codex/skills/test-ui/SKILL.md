---
name: test-ui
description: Run and verify scripted console UI tests for this Java project. Use when testing command-line interactions, comparing command responses with expected outputs, recording UI test cases in test/ui-test-plan.md, or reporting a console-session transcript.
---

# Test UI

## Run tests

Run `scripts/run-ui-tests.ps1` with matching command and expected-output-pattern lists.
Include `bye` as the last command so the application exits.

```powershell
& .codex/skills/test-ui/scripts/run-ui-tests.ps1 `
  -Commands @('todo read book', 'list', 'bye') `
  -ExpectedOutputs @('(?s)^.*\r?\n\r?\nadded:\r?\n\[T\]\[ \] read book$', '(?s)^.*\r?\n\r?\n1\. \[T\]\[ \] read book$', '^So long, and thanks for all the fish\.$')
```

Record every test case in `test/ui-test-plan.md`, including its aim, inputs, and
expected output pattern. Patterns are regular expressions because non-final
messages include a random quote. The runner compiles with Java 25, prints the
complete console transcript, and stops at the first output mismatch, reporting
its command, expected pattern, and actual output.

Run the relevant tests after every code update. First update
`test/ui-test-plan.md` when the changed code affects UI behaviour or expected
output.

