---
name: edge-case-probe
description: Hunt for edge cases in the chatbot by running it with crafted input and comparing what happens against what should happen. Use when the user asks to test the program, probe for bugs, check an increment before tagging, or find edge cases. This is exploratory testing by running the real program — not JUnit, and it writes no test files into the repository.
---

# Edge-case probe

Run the real program with deliberately awkward input and report what breaks. You are the tester, not the test suite. No files are added to the repo.

## Rules

* **Never write test input or output into the repository.** Everything goes in the scratchpad directory from the system prompt.
* **Report, do not fix.** List findings and stop. Fix only when the user asks.
* **Show real output.** Quote what the program actually printed. Never describe a run you did not perform.
* **A stack trace is always a finding.** The user should never see one.

## Running it

Compile every class together — the program is multi-class, so a single-file compile fails:

```bash
SP="<scratchpad path>"
javac -d "$SP/bin" src/main/java/*.java
printf 'line one\nline two\nbye\n' > "$SP/in.txt"
java -cp "$SP/bin" GudGoi < "$SP/in.txt"
```

**Feed input by file redirection, not by a PowerShell pipe.** Piping a string into a native process from PowerShell prepends a byte-order mark, which shows up as a stray character on the first line and looks like a program bug. Use the Bash tool with `<`, or `cmd /c "... < file"`.

End every input file with `bye` unless you are deliberately testing end-of-input.

## What to try

Work through these against whatever commands exist now. Not every row applies to every increment.

**Empty and blank**
* empty line, spaces only, tabs only
* a command with no argument: `todo`, `deadline`, `mark`, `list x`

**Arguments**
* missing delimiter: `deadline read book`
* delimiter with no value: `deadline read book /by`
* no space around the delimiter: `deadline read book/by Sunday`
* delimiter twice: `deadline a /by b /by c`
* delimiter inside the description: `todo email /by tomorrow`
* `/to` before `/from`
* very long description, 500+ characters

**Numbers**
* `0`, `-1`, `1.5`, `abc`, empty
* first and last valid position
* one past the end
* a number far larger than `int`, such as `99999999999`
* any number while the list is empty

**Commands**
* unknown word: `blah`
* different case: `LIST`, `Bye`, `ToDo`
* extra spaces between words
* a command that is a prefix of another

**State**
* `list` before anything is added
* mark a task that is already marked
* unmark a task that was never marked
* delete, then use the old numbers

**Input stream**
* end of input with no `bye` — this reaches `IO.readln()` returning null

## Reporting

One line per finding, worst first:

```
CRASH  | mark 99999999999 | NumberFormatException, stack trace shown to user
WRONG  | deadline a /by   | accepted an empty /by, task shows "(by: )"
ROUGH  | LIST             | rejected as unknown command; case sensitivity may surprise
OK     | list (empty)     | "Nothing on the agenda yet."
```

`CRASH` beats `WRONG` beats `ROUGH`. Say how many inputs you tried, so the user knows the coverage. Finish by naming the categories above you did **not** cover.
