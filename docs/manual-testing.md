# Carl manual test checklist

Use this checklist for behavior that is difficult or misleading to test in a headless JUnit run.

## Platforms

Run the packaged application on each supported platform using Java 25.

| Platform | Checks |
| --- | --- |
| macOS Apple Silicon | Window opens, text renders, commands work, app exits cleanly |
| Windows x64 | Window opens, text renders, commands work, app exits cleanly |
| Linux x64, if supported | Window opens, text renders, commands work, app exits cleanly |

Build the application separately on each operating system because JavaFX includes native platform files.

## Window and display

Repeat the following checks at 100%, 125%, and 150% display scaling where available:

1. Open Carl at its minimum window size.
2. Resize it wider, taller, narrower, and shorter.
3. Confirm message bubbles remain visible and text wraps without horizontal scrolling.
4. Enter a long task description and run `list` with at least 20 tasks.
5. Confirm the conversation scrolls to the newest response.
6. Confirm the input and Send button remain reachable.

Recommended screen sizes: 1366×768, 1920×1080, and a high-density laptop display.

## Visual states

1. Confirm ordinary user commands use green bubbles.
2. Enter an unknown command and confirm the error response is red.
3. Add a deadline and event, then confirm dates use amber accents.
4. Confirm the green status indicator appears beside “Carl · Night-Shift Dispatcher”.
5. Run `help` and confirm only command names use the monospace font.

## Locale and text

1. Run under an English OS locale and confirm dates display in English.
2. Run under a Chinese OS locale and confirm saved dates still use `yyyy-MM-dd HHmm` and display consistently.
3. Add tasks containing Chinese characters, accented Latin characters, and emoji.
4. Restart Carl and confirm those task descriptions load unchanged.

## Storage failures

Perform these checks using a disposable copy of the project:

1. Start with no `save.txt`; confirm Carl creates it and starts with an empty list.
2. Add malformed lines to `save.txt`; confirm valid lines load and a red warning identifies skipped lines.
3. Make `save.txt` read-only, try adding or marking a task, and confirm Carl shows an error and rolls back the change.
4. Restore write access and confirm subsequent changes save normally.
