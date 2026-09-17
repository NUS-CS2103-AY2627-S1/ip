---
name: test-ui
description: Run the Quu chatbot's text-Ui test cases from test/ui-test-plan.md in this IntelliJ project, comparing each command's actual console output against the expected output and stopping at the first failure. Use when the user asks to test the Ui, run the Ui tests, check the chatbot output, verify the CLI behaviour, or after changing command handling, output wording, or task rendering.
---

# test-ui

Runs the text-Ui test cases recorded in `test/ui-test-plan.md` against the
chatbot, and reports the first mismatch.

## What it does

1. Compiles every `.java` file under `src/main/java` into a temporary directory.
2. For each test case, starts the program fresh and types the commands in order.
3. After each command, compares the output that followed against the expected
   output from the plan.
4. Echoes the whole console session — inputs prefixed `>`, program output
   indented — so the run can be read like a terminal transcript.
5. Stops at the **first** failing test case and reports the expected output,
   the actual output, and the first line where they differ.

## Running it

The runner is a single Java file, `test/TestRunner.java`. Java 11+ can execute a
source file directly, so there is no build step and nothing to install — this
project's JDK 25 runs it as-is.

Open the **Terminal** tool window (`Alt+F12`), which starts at the project root:

```bash
java test/TestRunner.java
```

Options:

| Flag | Default | Purpose |
|---|---|---|
| `--plan PATH` | `test/ui-test-plan.md` | Test plan to read |
| `--src PATH` | `src/main/java` | Sources to compile |
| `--main CLASS` | `quu.Quu` | Main class to run (fully qualified) |
| `--only TC-ID` | — | Run one test case, e.g. `--only TC-05` |
| `--save PATH` | `data/Quu.txt` | Save file cleared before each case; `none` to leave it |
| `--no-compile` | off | Use the classes IntelliJ last built |

Exit status: `0` all passed, `1` a test failed, `2` setup problem (no plan,
compile error).

## IntelliJ specifics

**No JDK configuration needed.** The runner compiles through
`javax.tools.ToolProvider` and launches the program with the JDK running the
runner itself (`java.home`). Whatever JDK you use to start it is the one used
throughout, so there is nothing to point at a compiler and no dependence on
what happens to be on the shell PATH.

**Where TestRunner.java lives.** It sits in `test/`, which is *not* a source
root, so IntelliJ will not compile it into the module and it stays out of
`out/production/ip`. Leave `test/` unmarked — if IntelliJ offers to make it a
test source root, decline; these are not JUnit tests, and marking it would put
`TestRunner` on the module's classpath.

**Compiling.** By default the sources are compiled fresh into a temporary
directory, so the run always tests what is saved on disk and never disturbs
`out/`. Use `--no-compile` to run what IntelliJ last built — faster, but only
correct if you have built since your last edit. IntelliJ rebuilds on Build
(`Cmd+F9`) or Run, *not* on save, so this is easy to get wrong; the runner
compares timestamps and file counts and warns when the output looks stale.

**Run configuration.** To run the suite from the toolbar, add
**Run > Edit Configurations > + > Application**:

- Name: `test-ui`
- Main class: `TestRunner`
- Working directory: `$ProjectFileDir$`

Or use a **Shell Script** configuration with script text
`java test/TestRunner.java` and the same working directory, which avoids adding
`TestRunner` to the module. ANSI colour is suppressed when output is not a
terminal, so it stays readable in the Run window.

**Do not run the chatbot through IntelliJ's Run window while testing.** The
runner drives its own `java` process and reads its output; a second instance
competing for the console will produce confusing results.

## Adding a test case

Append a section to `test/ui-test-plan.md`. The runner finds test cases by
scanning for `## TC-<id> — <title>` headings followed by a ` ```session ` block:

````markdown
## TC-15 — Delete a task

**Aim:** `delete N` removes the Nth task and reports the new count.

**Source:** iP spec, Level 6.

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> delete 1
Noted. I've removed this task:
[T][ ] read book
Now you have 0 tasks in the list.
```
````

Every test case must state its **aim**, its **inputs** (the `>` lines), and its
**expected output** (everything else). Record where the expected output came
from in **Source** — the iP specification, or `project convention` when the
spec is silent, so it is clear which expectations are negotiable.

## Session block syntax

- `> command` — a line typed at the prompt.
- Any other line — expected output for the command above it.
- Lines before the first `>` — expected startup output. Omit them entirely to
  skip checking the banner and greeting.
- `...` on its own line — matches zero or more output lines. Use it to skip
  over the ASCII-art banner or any other output that is not under test.

## Comparison

Before comparing, both sides are normalised: each line is stripped of leading
and trailing whitespace, blank lines are dropped, and divider rules (10 or more
`_` or `-`) are dropped. Level 0 makes the horizontal lines and banner
optional, so they are treated as presentation rather than behaviour.

Whitespace *inside* a line is significant. `[T][ ] read book` and
`[T] [ ] read book` are different, and the test will fail — this is deliberate,
because the iP sample output has no space between the type and status icons.

## Interpreting a failure

The run stops immediately, so the reported failure is the earliest one. Fix it
and run again rather than reading ahead — later cases may depend on the same
behaviour and would only repeat the same finding.

A failure means one of two things, and it is worth deciding which before
changing code:

- The program's behaviour is wrong. Fix the program.
- The expectation is wrong or out of date. Fix the plan — but only where
  **Source** says `project convention`. Cases sourced from the iP spec should
  not be edited to match the code; that would defeat the purpose.

## When output must vary

Prefer a `...` wildcard over deleting an assertion. If a test case only cares
that a task was added and not about the exact count, keep the wording lines and
wildcard the rest, rather than dropping the whole expected block.
