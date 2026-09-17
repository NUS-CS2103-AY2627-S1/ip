---
name: test-ui
description: Record and run fail-fast tests for interactive command-line programs. Use after every code update in this project; when the user supplies, requests, updates, or runs lists of console commands and expected outputs; when creating or maintaining test/ui-test-plan.md; or when verifying a text-based UI and showing its complete console session.
---

# Test UI

Test an interactive console program one command at a time against the expected output recorded in `test/ui-test-plan.md`. Treat that file as the source of truth.

## Workflow

1. After every code update, invoke this skill before declaring the work complete. Inspect the project instructions and determine the command that starts the program. Build it first when necessary. For this project, use Java 25 as required by `AGENTS.md`.
2. Review `test/ui-test-plan.md`. Update it when the code change affects console commands, output, state transitions, error handling, or needs additional regression coverage; otherwise leave it unchanged. When the user supplies cases, create or update the plan from them and preserve useful existing cases unless the user asks to replace them.
3. Give every test case a unique ID, a concise aim, one or more input commands, and the expected output caused by each command. Record expected startup output separately.
4. Keep the explanatory Markdown and the machine-readable JSON block consistent. The runner reads the first `json` fenced block after the `## Machine-readable plan` heading.
5. Run `python3 .agents/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md`.
6. Show the runner's console transcript to the user. Do not summarize away the input/output record.
7. If a comparison fails, stop immediately. Report the test case and command, followed by the complete actual and expected output. Do not run later commands or cases.

## Plan format

Use this JSON shape inside `test/ui-test-plan.md`:

```json
{
  "version": 1,
  "working_directory": ".",
  "run_command": "java -cp out/production/ip Meow",
  "timeout_seconds": 10,
  "idle_milliseconds": 250,
  "comparison": "exact",
  "test_cases": [
    {
      "id": "TC-001",
      "aim": "Exit cleanly",
      "expected_startup_output": "Welcome!\n",
      "steps": [
        {"input": "bye", "expected_output": "Goodbye!\n"}
      ]
    }
  ]
}
```

Use JSON escapes such as `\n` for line breaks. `comparison` may be `exact` or `trim-final-newline`; prefer `exact`. Each test case starts a fresh program process, while its steps share that process so state can carry across commands.

Document the same cases above the JSON in readable Markdown. Each case must explicitly show its aim, inputs in order, expected startup output when applicable, and expected output after every input.

Do not invent expected output. If the user has not supplied enough information and it cannot be derived safely from an existing specification, ask for the missing expectation before running the test.

## Runner behavior

The bundled `scripts/run_ui_tests.py` runner captures standard output and standard error together, prints a transcript using `> command` for entered input, compares startup output and every command response, and terminates the child process and entire test run on the first failure. On failure, it prints the complete actual and expected output and returns a nonzero status.

The runner uses a short idle period to delimit output from an interactive process. Increase `idle_milliseconds` in the plan for programs that emit delayed output. Never weaken expectations merely to make a failing test pass.
