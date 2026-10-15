---
name: test-ui
description: Run repeatable end-to-end tests of this project's console UI from command and expected-output lists. Use when Codex is asked to create, update, or execute UI test cases; verify chatbot responses; perform text UI regression testing; or record an interactive console test session in test/ui-test-plan.md.
---

# Test UI

Test the console program against the cases in `test/ui-test-plan.md`, treating that file as the source of truth.

## Prepare the plan

1. Read `test/ui-test-plan.md` completely.
2. Add or update cases requested by the user before running them.
3. Give every case a unique ID and specify its aim, ordered inputs, and exact expected output after each input.
4. Preserve spaces, punctuation, capitalization, and line breaks in fenced `text` blocks. Use `<no output>` when an input should produce nothing.
5. Ensure each case ends with `bye`, unless its aim explicitly tests end-of-input behavior.

Follow the existing test-case template in the plan.

## Run the tests

1. Work from the repository root.
2. Select Java 25 before compiling or running. On macOS, run `sdk use java 25.0.3.fx-zulu` in the same shell when SDKMAN is available; otherwise verify that `java -version` and `javac -version` both report Java 25.
3. Determine the compile and launch commands from the repository. For the current layout, compile into a temporary directory with `javac -d <build-dir> src/main/java/*.java` and launch with `java -cp <build-dir> Henry`.
4. Start a fresh program process for each test case so cases cannot share state.
5. Capture stdout and stderr separately. Treat unexpected stderr or a nonzero exit code as a failure unless the case explicitly expects it.
6. Compare startup output before sending the first input. Then send inputs in order and compare the complete output attributable to each input with its expected block. Normalize only platform line endings (`CRLF` to `LF`); do not trim whitespace or ignore extra lines.
7. Stop immediately at the first mismatch, unexpected stderr, premature exit, timeout, or nonzero exit. Do not run later steps or cases.
8. Apply a short timeout to startup and every step. Do not send another command until the current output has been checked.

If buffering prevents reliable step-by-step capture, use a pseudo-terminal. Do not weaken the comparison or send all inputs before checking intermediate output.

## Report the session

Always show a chronological console transcript for everything that ran, including startup output. Prefix typed input with `> ` and reproduce program output exactly beneath it.

For a successful run, report the number of passed cases and include each case's transcript.

For a failure, identify the case and step, state that testing stopped immediately, and show:

- the transcript up to the failure;
- the exact expected output in a fenced `text` block;
- the exact actual output in a fenced `text` block; and
- whether the difference involved stdout, stderr, exit status, or timeout.

Do not update expected output merely to make a failing implementation pass. Change it only when the user confirms the intended behavior or project requirements establish a different correct result.
