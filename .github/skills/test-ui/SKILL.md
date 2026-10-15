---
name: test-ui
description: 'Run scripted console UI tests from test/ui-test-plan.md. Use when testing command-line interactions, comparing actual output with expected output, showing console transcripts, or stopping on the first failed test.'
argument-hint: 'Optional: path to a Markdown UI test plan'
---

# Console UI Testing

Run the project UI test plan as a sequence of isolated console sessions. Each
case must contain an aim, a command, input, and expected output.

## Procedure

1. Read `test/ui-test-plan.md`, or the path supplied by the user.
2. Confirm that each case has these Markdown sections:
   - `Aim`
   - `Command`
   - `Input`
   - `Expected output`
3. Build the Java application before running the cases:
   ```powershell
   New-Item -ItemType Directory -Force -Path build\classes | Out-Null
   javac -d build\classes (Get-ChildItem -Path src\main\java -Recurse -Filter *.java).FullName
   ```
4. Run the bundled runner from the repository root:
   ```powershell
   & .\.github\skills\test-ui\scripts\run-ui-tests.ps1 -PlanPath test\ui-test-plan.md
   ```
5. For every case, the runner sends the listed input to the listed command,
   compares the combined console output with the expected output, and prints
   the input and actual output as a session record.
6. If a case fails, stop immediately. Report the case name, actual output, and
   expected output from the runner. Do not run later cases.
7. Report the final pass count only when all cases pass.

## Test Plan Format

Use one `## Case:` heading per test. Put each value in a fenced code block;
the command block may be labelled `powershell`.

```markdown
## Case: short name

### Aim
What behavior this case verifies.

### Command
```powershell
java -cp build\classes Dingleberry
```

### Input
```
command
bye
```

### Expected output
```
complete console output
```
```

Keep expected output exact, including blank lines and separators. The runner
normalizes CRLF/LF differences, line-end spaces, and one final newline only;
leading spaces remain significant.