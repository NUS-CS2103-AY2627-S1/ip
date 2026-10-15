# Testing Thomas

## Automated tests

Run the full suite with Java 25:

```shell
./gradlew test checkstyleMain checkstyleTest
```

The automated tests cover command parsing and execution, task and place validation, date/time handling,
file persistence and corrupt-record recovery, console output, and end-to-end non-GUI workflows.

## Manual GUI compatibility tests

Perform these checks using the packaged application on Windows, macOS, and Linux where available:

1. Launch the application with no existing `data` directory and confirm it creates its data files.
2. Verify the window and every dialog at 100%, 125%, 150%, and 200% display scaling.
3. Resize the window to its minimum size and to full screen; confirm text remains readable and scrolling works.
4. Enter every command shown by `/help`, including invalid input, and compare the result with the CLI behavior.
5. Restart after adding, editing, marking, and deleting records; confirm the remaining data is restored.
6. Test an empty input, long descriptions, Unicode, Chinese text, emoji, and pasted multi-space text.
7. Run with English and Chinese OS display languages and verify dates, files, and commands behave consistently.
8. Make the data directory read-only and verify failures are reported without crashing or losing existing data.
9. Temporarily corrupt individual task and place records; verify valid records still load and the app remains usable.
10. Confirm `bye` closes the GUI cleanly and keyboard submission behaves like clicking the send button.
