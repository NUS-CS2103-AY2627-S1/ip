---
name: test-ui
description: Run the command-line UI test cases defined in test/ui-test-plan.md after application changes.
---

# Test UI

Use this skill after updating the project's command-line application. The test plan at
`test/ui-test-plan.md` is the source of truth for the test cases and how to run the
program.

1. Update the plan first if the change adds or alters observable command behavior.
   Each test case must state its aim and provide one or more command/expected-output
   pairs.
   When adding or revising command cases, keep the plan proportionate to the
   application's supported behavior and consider these coverage areas:

   - empty-state behavior, including commands that display or modify an empty list;
   - missing, non-numeric, and out-of-range task numbers for both `mark` and
     `unmark` when those commands are available;
   - malformed command names and malformed required markers, especially where the
     parser requires whitespace around a command or marker;
   - leading, trailing, or repeated whitespace when input normalization is
     supported; and
   - documented boundary behavior, such as a maximum task capacity.

   Test duplicate markers or similarly ambiguous input when the application defines
   a clear intended result. If behavior is not specified, report it as a product
   decision instead of inventing an expected output. Keep startup output and fixed
   divider lines out of per-command expectations unless that presentation is itself
   being changed.
2. Compile the application with Java 25, following the project instructions. If Java
   25 is unavailable locally but JDK 26 is installed, compile Java 25-compatible
   bytecode with JDK 26 instead:

   ```powershell
   javac --release 25 -d out (Get-ChildItem -Path src/main/java -Filter *.java -Recurse | ForEach-Object FullName)
   ```

   Confirm the resulting classes use Java 25's class-file version (69), for example:

   ```powershell
   javap -verbose out/keef/Keef.class | Select-String 'major version'
   ```
3. Run the plan from the repository root:

   ```powershell
   .\.codex\skills\test-ui\scripts\run-ui-tests.ps1
   ```

   Supply `-RunCommand` only when the plan's default command needs to be overridden.
   In the JDK 26 fallback, provide its `java.exe` explicitly: `PATH` may resolve to
   an older Java runtime that cannot run Java 25 bytecode. For example, derive the
   runtime beside the selected `javac` and run:

   ```powershell
   $java26 = Join-Path (Split-Path (Get-Command javac).Source) 'java.exe'
   .\.codex\skills\test-ui\scripts\run-ui-tests.ps1 -RunCommand ('"{0}" -cp out keef.Keef' -f $java26)
   ```
4. Preserve the runner's console-session record in the response. On the first failed
   command it stops the process and prints both the expected and actual output; do not
   continue with later test cases.

The runner compares the response body for each command. The transcript it prints
includes the application's divider lines, so the complete console exchange remains
visible without repeating those fixed lines in every expected-output block.
