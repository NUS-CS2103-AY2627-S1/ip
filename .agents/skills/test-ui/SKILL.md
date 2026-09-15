---
name: test-ui
description: Use when checking LuigiBot console behavior against command-and-output UI test cases or maintaining the project's UI test plan.
---

# Test LuigiBot UI

Use `test/ui-test-plan.md` as the source of truth for console test cases. Each case represents one fresh LuigiBot session so related commands share task-list state.

## Workflow

1. If the user supplies commands and expected outputs, record or update the corresponding cases in `test/ui-test-plan.md` before running them. Every case must have a name, aim, input block, and expected-output block.
2. Preserve input and expected output exactly, including spaces, punctuation, blank lines, and separator lines. Include `bye` explicitly when the expected session includes the goodbye response. For persistence cases, use an optional `Initial saved data` text block to create `data/luigibot.txt` before startup and an optional `Expected saved data` text block to verify it after the session. To exercise filesystem errors, an `Initial save path` block containing `directory` creates the save-file path as a directory. Each case uses an isolated temporary working directory.
3. From the repository root, run:

   ```powershell
   python .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

4. Show the runner's console transcript to the user. It records the input commands and actual program output for every executed case.
5. On a failure, stop at that case. Report its actual and expected output; do not run later cases.

Pass a different Markdown plan path as the first argument only when the user explicitly requests another plan.

## Test-plan format

````markdown
## Test case: Descriptive name

**Aim:** One sentence describing the behavior under test.

### Input

```text
todo borrow book
bye
```

### Expected output

```text
exact console output
```
````

## Common mistakes

- Do not run every input in a separate process; commands within a case need shared state.
- Do not silently trim indentation or internal blank lines.
- Do not continue after a mismatch.
- Do not edit expected output merely to make a failing test pass; first determine whether the program or specification is wrong.
