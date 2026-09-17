---
name: test-ui
description: Run console UI test cases from a project test plan, compare actual output with expected output, stop at the first failure, and report the session transcript.
---

# Test UI

Use this skill for interactive console programs when the user provides, or asks to maintain, a list of commands and expected outputs.

## Test plan

Use `test/ui-test-plan.md` as the source of truth. Before running tests:

1. Read the plan and preserve its existing cases.
2. If the user supplied additional commands or expected outputs, record them in the plan before executing them.
3. Ensure every case has an aim, an input block, an expected output block, and a comparison mode.

Use this structure for each case:

````markdown
## Test case N: short name

### Aim
What behavior this case verifies.

### Comparison
`exact` or `contains`

### Input
```text
command 1
command 2
```

### Expected output
```text
expected output
```
````

`exact` compares the complete stdout after normalizing line endings. Preserve all meaningful whitespace. `contains` checks that each nonblank expected line appears in the actual output in the same order, after trimming only leading and trailing whitespace from both lines; internal spacing remains significant. Use it when the case is intentionally independent of banners or separators. Include `bye` only when the test is specifically checking the exit command; otherwise end the input at the last feature command and use end-of-input to terminate the process.

## Running the tests

1. Inspect the repository instructions and use the project-required runtime. For this project, compile and run with Java 25.
2. Build the program once before the cases. Use a temporary output directory when the project has no build tool.
3. Run every test case in a fresh process, feeding the Input block through standard input. Do not combine test cases into one process because task state must not leak between cases.
4. Capture both the exact console input and the program's stdout/stderr for each completed case.
5. Compare the captured output with the Expected output using the case's comparison mode.
6. Stop immediately when a case fails. Do not run later cases after a failure.

Do not edit application source code to make a UI test pass. If the project cannot compile or launch, treat that as a failed test session and report the command, error output, and the relevant test case.

## Reporting

After testing, report:

- the test cases completed and whether each passed;
- for every completed case, a console transcript containing the exact input and output;
- on failure, the first failing case, the expected output, the actual output, and a focused difference when possible.

When a test fails, make the fail-fast behavior explicit and omit any claim that later cases passed. A successful run must show all case transcripts.
