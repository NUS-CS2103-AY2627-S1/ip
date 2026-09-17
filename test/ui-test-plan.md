# UI Test Plan

Prerequisites: Java 25 and the Gradle wrapper must be available. Run from the project root.

## GUI launch banner

Aim: Verify that launching the JavaFX GUI displays Della's welcome banner in a dialog box.

Expected output: The first Della dialog contains `Hi! I'm Della :))` and `How can I help you?`.

## Case 1: Exit command

Aim: Verify that the chatbot returns the farewell message for `bye`.

Inputs: `bye`

Expected output: The output contains `Byee! Rest well!` and the process exits successfully.

## Test Session: 2026-09-07

Prerequisites: Java 25.0.4, Gradle wrapper, project root as working directory.

=== Test Case: Exit command ===
Input:
bye
Output:
> Task :compileJava UP-TO-DATE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE
Error: Could not find or load main class Launcher
Caused by: java.lang.ClassNotFoundException: Launcher
Exit status: 1
Result: FAIL (test harness configuration: `build.gradle` specifies `Launcher` instead of `della.Launcher`)

The UI test session stopped at the first failure as required.

=== Test Case: Exit command (Storage refactoring verification) ===
Input:
bye
Output:
The GUI launched successfully and emitted JavaFX compatibility warnings,
but the command-line input was not consumed because the application is GUI-based.
The process was terminated after the GUI remained open.
Exit status: terminated after UI launch
Result: NOT COMPARABLE (the plan's expected command-line interaction does not
match the configured GUI launcher)
