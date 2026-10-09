---
name: test-ui
description: Run the project’s documented console UI test cases, comparing every command’s actual output with its expected output and stopping at the first failure.
---

# Test UI

Use this project-specific skill when the user asks to test the interactive console behavior of the Java program.

## Source of truth

Read `test/ui-test-plan.md` before running anything. The plan is the source of truth for:

- the project working directory and setup command;
- the command used to launch the program;
- the ordered test cases;
- each test case’s aim and ordered console command inputs; and
- the expected output for every command in each case.

Do not invent test cases or silently change expected output. If the plan is incomplete or ambiguous, report the missing detail before testing.

Read all current definitions in `test/ui-test-plan.md` completely. Historical results are stored separately in `test/ui-test-history.md`; they are evidence from earlier runs, not test definitions or expected output, and do not need to be read to execute the current plan.

## Execution

1. Use Java 25 for compilation and execution. Confirm the selected Java version before running the plan; stop and report the problem if Java 25 is unavailable.
2. Run `java test/UiTestRunner.java` from the repository root. The runner uses Java source-file mode so it works on Windows, macOS, and Linux without a separate runner compilation step or a PowerShell dependency. This reusable runner reads the setup command, launch command, test cases, expected output, session boundaries, and file assertions from the Markdown plan. Do not recreate an ad hoc runner for each test session.
3. Execute the test cases in the order listed. The runner launches the program using the documented launch command and provides the documented inputs in order.
4. Treat each command/input and expected-output pair as one assertion. Capture the program’s response to that input, excluding output already recorded for an earlier input in the same session. Normalize only `CRLF` to `LF`, then compare the result exactly. Preserve spaces, blank lines, punctuation, and Unicode characters.
5. As soon as an assertion fails, terminate the running program, stop the entire test session, and do not run later test cases. Report the test case, command/input, actual output, and expected output.
6. On success, report that all listed test cases passed.

If a test case needs state from earlier inputs, keep those inputs in the same case and preserve their order. Do not combine separate test cases into one process unless the plan explicitly says to do so.

## Runner requirements and known pitfalls

Account for these requirements before starting the test run. A runner error is not an application failure; correct the runner before reporting a failed assertion.

The checked-in runner implements the requirements below. Update the runner only when the plan needs a new kind of setup, interaction, or assertion that the generic runner cannot express. Adding ordinary commands, expected responses, persistence checks, or another restart session should require changing only the Markdown plan.

- Compile once using the plan's setup command, then reuse the compiled output for every case. Do not recompile before individual cases.
- A fresh Java process is intentionally required for each isolated test case. A restart case may require more than one process. Java startup can take considerably longer than compilation, so allow enough time for every documented launch instead of treating a slow launch as a hang.
- The application prints a startup banner enclosed by the same underscore separator lines used for command responses. For an interactive runner, consume and record the complete startup block before sending or interpreting the first test input. Otherwise, the banner can be mistaken for the first command's response.
- Keep each command's response separate. After sending a command, read through the closing separator before sending the next command or performing its side-effect assertion.
- UI-005 requires one long-lived interactive process. For each of its first six commands, use this exact sequence: send one command, capture and compare its response, read and compare `data/sumo.txt`, then send the next command. Do not pipe all UI-005 inputs at once because that cannot prove that each change was saved immediately.
- Normalize only `CRLF` to `LF`. Do not trim leading spaces from response bodies. If separators are removed for comparison, remove only the documented separator lines and preserve all text between them exactly.

## Test-session record

Always include a concise transcript in the response after testing. Show each command or console input with a `$ ` or `> ` marker and its resulting output in a fenced `text` block. Include setup/compilation output when it is relevant. On failure, end the transcript at the first failing assertion and clearly label both `Actual output` and `Expected output`.

When the task includes recording the run in the repository, append a concise entry to `test/ui-test-history.md`; never append session records to the test plan. For a successful run, record only the date, Java version, cases executed, and overall result. For a failed run, additionally record only the first failing case, command/input, actual output, and expected output. Do not store full successful transcripts because Git history already preserves earlier records.

Do not edit the implementation to make a test pass. If the plan itself needs correction, explain the issue separately and wait for the user’s direction.
