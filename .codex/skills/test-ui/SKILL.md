---
name: test-ui
description: Run scripted end-to-end tests of this project's command-line UI, checking each command's output against expected output recorded in test/ui-test-plan.md. Use when asked to test the CLI, run UI test cases, or verify command/output behavior.
---

# Test UI

Exercise the program through standard input and standard output, using
`test/ui-test-plan.md` as the canonical test plan and producing a visible test
session transcript.

## Prepare the plan

1. Read `AGENTS.md` and inspect the repository to determine how to compile and
   launch the program. Use Java 25 as required by this project. Do not assume
   that Gradle is available when no Gradle build files are present.
2. Read `test/ui-test-plan.md`. If the user supplies new or revised test cases,
   record them there before running them. Preserve unrelated existing cases.
3. Each test case must have:
   - a unique ID or descriptive name;
   - an **Aim** explaining the behavior being checked;
   - **Inputs**, as an ordered list of commands exactly as they will be entered;
   - **Expected output**, divided by input command so every command has one
     explicit expected response.
4. Resolve material ambiguities in expected output before testing. Never derive
   the expected output from the current program output after the test starts.

## Run the tests

1. Compile the current source before starting. If compilation fails, stop and
   report the compiler output; do not describe any test case as having run.
2. Protect persistent application data from test side effects. Back up existing
   data and restore it after the session, including after a failure. Run from an
   isolated temporary working directory when practical. Do not delete or
   overwrite the user's saved data.
3. Start a fresh program process for each test case unless the plan explicitly
   says that a case continues state from another case. Capture standard output
   and standard error. Treat unexpected standard error as a failure.
4. Send the listed input commands in order. After each command, collect the
   complete response attributable to that command and compare it with that
   command's expected output. Compare exact text, line order, punctuation, and
   blank lines. Ignore only terminal line-ending differences (`LF` versus
   `CRLF`) and one final newline at end of output.
5. Include startup or shutdown text in the expected output for the command that
   triggers it, as specified by the plan. When output boundaries cannot be
   observed reliably while the process is interactive, run the entire case as
   one piped session, capture the full output, and compare command response
   segments in order.
6. On the first mismatch, unexpected error, timeout, premature exit, or missing
   output, terminate the running program immediately. Do not execute later
   commands or test cases.

## Report the session

Always show a chronological console transcript after testing, including every
input actually sent and all output received. Distinguish input from output with
clear labels or prompts without altering the captured text.

If all cases pass, identify every case that passed. If a case fails, report its
ID/name and the command that failed, then show the expected output and actual
output in separate fenced blocks. State explicitly that the remaining session
was terminated and which commands or cases were not run. Also report whether
persistent application data was restored.
