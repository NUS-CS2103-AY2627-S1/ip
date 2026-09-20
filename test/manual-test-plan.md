# Duchess Manual JavaFX Test Plan

Use Java 25 and build the application with `./gradlew check shadowJar` before
running these checks. Record the OS, display scale, screen resolution, system
language, and result for each environment tested. These cases are intentionally
manual because they depend on a real JavaFX window and platform rendering.

## Test case 1: Launch and basic conversation

Aim: Verify the packaged GUI opens and shares behavior with the tested command
processor.

Steps:

1. Run `java -jar build/libs/duchess.jar`.
2. Confirm the greeting, header, quick-command strip, conversation area, and
   composer are visible.
3. Enter `todo read book`, `list`, `mark 1`, and `stats`.
4. Compare every response with the equivalent console behavior.

Expected: The window opens without console errors, commands produce matching
responses, and the newest dialog remains visible.

## Test case 2: Keyboard focus and shortcuts

Aim: Verify interactive controls remain usable without a mouse-heavy workflow.

Steps:

1. Launch Duchess and type in the composer immediately.
2. Submit with Enter and confirm the input clears and regains focus.
3. Select New case, Deadline, Briefing, Find intel, Close case, Reopen, and
   Archive; verify each command starter and caret position. Archive must not
   delete anything until a task number is entered and the command submitted.
4. Select Roll call, Report, and Playbook; verify their responses and statuses.
5. Hover over shortcuts and check their command-format tooltips. Confirm all
   eleven shortcuts remain accessible at minimum window size, with no overlap.
6. Confirm the navy-and-gold header badge, full-size shield avatars, and
   Nine-Nine greeting remain visible.

Expected: Focus, caret placement, submission, and every shortcut work without
duplicate dialogs or disabled controls.

## Test case 3: Resize and display scaling

Aim: Verify the layout remains readable at supported sizes and display scales.

Steps:

1. Resize the window down to its 420 × 560 minimum.
2. Resize it to 1920 × 1080 or maximize it.
3. Repeat at 100% and a high-DPI scale such as 150% or 200% when available.
4. Add enough tasks and open Help to produce long, wrapped content.

Expected: Controls do not overlap or disappear, text remains readable, the
composer remains usable, and the conversation scrolls to the newest dialog.

## Test case 4: Exit behavior

Aim: Verify `bye` ends the GUI session cleanly.

Steps:

1. Launch Duchess and enter `bye`.
2. Confirm the goodbye dialog and muted header status appear.
3. Confirm the composer, Help button, and quick commands become disabled and
   the farewell remains visible for about two seconds before the app exits.

Expected: No additional command can be submitted after exit is requested.

## Test case 5: Startup recovery warning

Aim: Verify that the GUI clearly reports a damaged data file and protects it.

Steps:

1. Use an isolated working directory and create `data/duchess.txt` containing
   `not-a-valid-record`.
2. Launch the executable JAR from that directory.
3. Confirm an error dialog explains that saving is disabled and gives recovery steps.
4. Enter `todo temporary task`, then `list`.
5. Exit and verify the original file still contains `not-a-valid-record`.

Expected: The warning appears after the greeting. The task remains usable in
memory, the save failure is shown, and the damaged file remains unchanged.

## Test case 6: Platform and language smoke test

Aim: Detect platform-specific launch, font, locale, or path problems.

Steps:

1. Run cases 1–4 on each available target: macOS, Windows, and Linux.
2. Repeat once with an English system language and once with a non-English
   language such as Chinese, when those environments are available.
3. Add deadlines and events and restart Duchess to verify persisted Unicode
   descriptions and English month abbreviations remain readable.

Expected: The application launches through the platform script, Unicode text
round-trips through storage, icons have readable fallbacks, and dates retain
the documented Duchess format on every locale.

## Test case 7: Blank submission and long input regression

Aim: Prevent empty Enter repeats from flooding the conversation (forum #286).

Steps:

1. Hold Enter for five seconds with an empty composer, then repeat with spaces.
2. Verify no messages appear and Send is disabled.
3. Paste a long todo command and confirm it wraps in the composer at minimum width.
4. Submit with Enter and confirm exactly one task is added and input clears.
5. Repeat with the Send button; try pasting text containing line breaks.
6. Run `find nonexistent-keyword` and confirm the explicit no-matches response.
7. Maximize the window and confirm text grows and the conversation stays centered,
   with a maximum width of 880 pixels. Return to minimum size and open Help.

Expected: Input remains responsive, long text wraps without horizontal clipping,
line breaks in pasted commands become spaces, and blank commands produce no dialogs.
