---
name: test-ui
description: Run fail-fast acceptance tests for this project's interactive console UI from commands and expected output. Use when asked to create, update, or execute UI test cases; verify command-by-command console behavior; maintain test/ui-test-plan.md; or provide a transcript of a manual CLI test session.
---

# Test UI

Test the console program one command at a time against the cases in `test/ui-test-plan.md`. Keep the plan current and always return the console transcript.

## Record the plan

1. Read `test/ui-test-plan.md`. Create it from its existing template structure if it is absent.
2. Translate the user's lists of commands and expected outputs into numbered test cases before running them.
3. Give every case an aim, its ordered console inputs, and the exact expected output after each input. Preserve spaces, punctuation, and line breaks in fenced text blocks.
4. Record relevant setup in the plan: launch command, working directory, prerequisites, comparison rules, and any required starting state.
5. Do not invent missing expected output. If it cannot be derived unambiguously from the user's request or existing plan, ask for it before testing.

Use one case for a sequence whose commands depend on shared program state. Use separate cases when isolation is required, and state how to reset that state.

## Prepare the program

1. Read `AGENTS.md`, `README.md`, and the relevant source or build files to determine how to launch the program.
2. Use Java 25 for Java build and run commands. On macOS, run `sdk use java 25.0.3.fx-zulu` in the same interactive shell before compiling or launching.
3. Compile before opening the test session when compilation is needed. Treat a build or launch error as a failed test session and report it with the transcript.
4. Start the application in a PTY so inputs can be sent incrementally and output can be observed after every command.

## Run tests fail-fast

For each test case, in plan order:

1. Put the program in the starting state documented by the case.
2. Start a fresh program process unless the plan explicitly requires state shared across cases.
3. Wait until startup output and the input prompt are complete.
4. Send exactly one listed input, then wait until the response and next prompt (or program exit) are complete.
5. Compare that response with the corresponding expected output before sending another input.
6. Normalize only terminal transport differences: CRLF versus LF and a single terminal-added echo of the submitted input. Do not ignore whitespace, blank lines, prompts, ordering, or punctuation unless the plan explicitly says to.
7. If the output matches, continue. If it differs, send no further input, terminate the running process, and stop the entire test session immediately.

Never update expected output merely to make a failure pass. Change it only when the user changes the requirement.

## Report the session

Show a chronological console transcript containing startup output, every input, and every program response. Use a fenced `text` block and make typed input visibly distinguishable, for example with an `[INPUT]` prefix added only in the report. Do not claim that this prefix was emitted by the program.

For a successful run, report how many cases passed and link to `test/ui-test-plan.md`.

For a failure, report:

- the first failed case and input;
- the exact expected output in a fenced block;
- the exact actual output in a fenced block;
- the transcript up to termination; and
- confirmation that later commands and cases were not run.

Keep raw output available until the report is complete. Clearly distinguish program output from shell/compiler diagnostics.
