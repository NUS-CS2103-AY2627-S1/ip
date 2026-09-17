# Murphy UI Test Plan

This plan covers the current command-line entry point and the JavaFX GUI. Run the command-line cases with a fresh `data/murphy.txt` before each case.

## Test environment

- **Program command:** `./gradlew run`
- **Working directory:** repository root
- **Java version:** Java 25 (`sdk use java 25.0.3.fx-zulu` on macOS, if needed)
- **Input:** one command per line through standard input

## Test Case: Friendly startup and exit

**Aim:** Verify that Murphy starts with the correct product name and exits with the current personality response.

**Inputs:**

```text
bye
```

**Expected output:** The welcome banner identifies Murphy, no legacy product name appears, and the final response is:

```text
Bye! I'll be here guarding your task list. Tiny paws, big responsibility.
```

## Test Case: Recoverable command errors

**Aim:** Verify that malformed commands are reported without terminating the program.

**Inputs:**

```text
todo
deadline report /by not-a-date
event meeting /from /to 4pm
mark nope
bye
```

**Expected output:** Each invalid command produces one `OOPS!` response explaining the correction, and Murphy still processes the later commands and exits normally.

## Test Case: Save-file recovery

**Aim:** Verify that a missing or malformed `data/murphy.txt` file does not crash Murphy.

**Data setup:** Remove `data/murphy.txt`, run `todo test recovery`, then replace the file with one valid task line and one malformed line before a second run.

**Inputs:**

```text
list
bye
```

**Expected output:** Murphy starts with an empty list when the file is missing, and reports a recoverable corrupted-line warning while retaining valid records on the second run.

## GUI manual checks

1. Launch `murphy.Launcher` and confirm the window title and header say Murphy.
2. Resize the window from the minimum size to a wide window; confirm message bubbles and the input field resize without overlap.
3. Enter an invalid command; confirm the error appears in the red error style and the application remains usable.
4. Use a deliberately unreadable/corrupted save file; confirm the storage warning appears in the conversation.
