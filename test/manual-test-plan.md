# Manual test plan

These checks cover JavaFX behavior and operating-system integration that is
not reliable to assert in headless JUnit tests. Run the applicable checks
before a release and record the tester, date, platform, and result.

## Execution record

| Date | Environment | Check | Result |
| --- | --- | --- | --- |
| 18 Sep 2026 | macOS 26.6.2, Java 25, `C.UTF-8` | JavaFX startup through `./gradlew run` | Pass: application started without an exception |

The cross-platform, alternative-locale, scaling, and visual checks below need
their corresponding environments and remain release-time manual checks.

## GUI startup and basic conversation

Aim: Confirm the packaged graphical application starts and remains connected
to the same command behavior covered by the automated tests.

1. Run `./gradlew run` using Java 25.
2. Confirm the welcome message, Daddy avatar, user avatar, and input controls
   appear without clipping.
3. Enter `todo read book`, `mark 1`, `list`, and an invalid command.
4. Confirm user messages and Daddy replies are visually distinct.
5. Confirm the invalid command uses the error styling.
6. Enter `bye` and confirm the farewell appears.

Expected: The window starts without console errors, commands behave as in the
CLI, errors are visually prominent, and the conversation remains readable.

## Responsive window sizes

Aim: Confirm the conversation remains usable at common small and large window
sizes.

Test at approximately these content sizes:

- 800 x 600
- 1280 x 720
- 1920 x 1080
- A narrow window near the application's minimum width

For each size, add enough tasks to require scrolling and run `list`.

Expected: Text wraps without being cut off, the input and send button remain
reachable, avatars stay cropped correctly, and scrolling reaches all replies.

## Operating-system matrix

Aim: Detect platform-specific JavaFX, file-path, font, and packaging problems.

Repeat the GUI startup test using the generated `Daddy.jar` on:

- macOS with Java 25
- Windows with Java 25
- Linux with Java 25

Expected: `java -jar Daddy.jar` opens the GUI, bundled JavaFX libraries load,
avatars and fonts render acceptably, and tasks persist under `data/daddy.txt`
beside the JAR's working directory.

## Display scaling

Aim: Confirm controls and text remain usable with non-default scaling.

Repeat the responsive-window test at 100%, 125%, 150%, and 200% display
scaling where the operating system supports those settings.

Expected: No controls overlap, text remains legible, and dialog content is not
truncated.

## Language and locale settings

Aim: Confirm command parsing and stored dates remain stable when the operating
system language changes.

1. Run once with an English locale and once with a Chinese locale.
2. Add `deadline submit report /by 02-12-2026 1800`.
3. Restart the app and run `list`.

Expected: The command is accepted in both locales, the saved task reloads, and
English month/day display remains consistent with the documented interface.

## File-system failures

Aim: Confirm environmental errors are understandable and do not crash the GUI.

1. Start with no `data` directory.
2. Start with a malformed line in `data/daddy.txt`.
3. Where safe, make the data directory read-only and try to add a task.

Expected: First-run storage is created automatically, malformed records are
quarantined with recovery files, and permission failures produce a specific
error while leaving the visible task list unchanged.
