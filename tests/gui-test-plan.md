# GUI test plan: A-BetterGui and A-MoreErrorHandling

## Scope

Verify responsive, asymmetric chat styling, compact rounded avatars, readable multi-line replies, and keyboard focus. A-MoreErrorHandling adds explicit error cards, visible file warnings, validation, and failed-save recovery while preserving valid command behavior and Epi's cat-themed wording.

Run the usual JUnit, Checkstyle, and all 21 console regression cases in [test-plan.md](test-plan.md) first. The eight `DialogBoxTest` cases run without a graphical desktop.

## Automated JavaFX scene checks

With Java 25 configured, run `./gradlew guiSmokeTest` in Git Bash or `.\gradlew.bat guiSmokeTest` in PowerShell. A graphical desktop is required; this task is deliberately not a dependency of ordinary `test` or `check`.

The runner uses the actual FXML/controller/resources in a new working directory under `build/tmp/guiSmokeTest` for every run. No personal data is loaded. It exits nonzero at the first failed check and retains generated data and scene previews for diagnosis. Generated files remain under ignored `build/` and must not be committed.

| Aim | Inputs/setup | Expected result |
| --- | --- | --- |
| Preserve submission behavior | Send `todo read book`, a dated event, a deadline, `mark 1`, and `list`, alternating the TextField action and Send button. | Each command adds exactly one user row and one Epi row; input clears. The complete list stays in a single card with task 1 marked. |
| Preserve blank/error handling | Submit spaces, then `unknown-command`. | Spaces add no rows; the error remains exactly `I do not understand what that means, Human.` |
| Check supported sizes | Load saved test tasks at scene sizes 420x480, 600x700, and 1000x700; submit `sort date`. | No horizontal overflow or vertically clipped message text; original task numbers/status remain; avatars load as rounded square crops no larger than 36 pixels. The wide reply exceeds the former 430-pixel cap. New overflowing replies scroll to the bottom. |
| Relayout existing messages | Resize the same conversation's root from 600 pixels wide to 400, 1000, and 400, at 440 pixels high. | The same message nodes and text survive each resize. Width and height bounds still hold, with no overlapping rows; the wide reply expands. This checks scene relayout, not operating-system window dragging. |
| Stress wrapping | At 420x480, add a description containing 200 consecutive `a` characters, then `list`. | User and Epi text wrap inside their rows, with no clipped message height or horizontal overflow. |
| Distinguish errors semantically | At 420x480, submit `unknown-command`, `deadline book /by 2020-02-30 1200`, and `event meeting /from 2020-01-01 1200 /to 2020-01-01 1200`. Then add `todo Invalid date format! Meow!`. | Each actual error has an `error-message` card labelled `Epi - needs attention`, with usable wrapping. The successful todo has an ordinary card even though its description looks like an error. |
| Show damaged-file warnings | In the isolated fixture only, write `T | 1 | valid task`, `broken record`, and `T | 0 | last task` as three lines, then load a new scene. Run `list` and `delete 1`. | One warning card follows the greeting, identifies line 2, and asks for repair/restart. Valid tasks retain order/status and receive numbers 1 and 2. Delete is rejected; every byte of the damaged file remains unchanged. |
| Roll back failed saves | Repair that isolated fixture to `T | 0 | saved task`, load a new scene, then replace the fixture file with an empty directory. Submit `mark 1` and `list`. | No startup warning for the repaired file. Mark returns one explicit error containing `No task changes were kept`; no success reply. List still shows the incomplete task. The directory is not replaced. The runner restores only its own fixture afterwards. |

Reports: `build/reports/gui-smoke/compact.png`, `default.png`, `wide.png`, `long-text.png`, `errors.png`, and `storage-warning.png`. These are rendered scene previews, not screenshots of a full window or proof of physical keyboard/mouse behavior. The task prints one PASS line per check group and `All GUI smoke checks passed.` on success.

## Manual desktop checks

Use a new empty folder with a freshly built JAR (`./gradlew clean shadowJar`, then copy `build/libs/epi.jar` there and run `java -jar "epi.jar"` with Java 25). A clean build should produce only `epi.jar` in `build/libs`, with JavaFX and the GUI resources bundled. This keeps synthetic tasks out of your personal data file. Start with no tasks and use the inputs below.

### 1. Initial window and visual hierarchy

- Aim: Confirm that the cat theme remains readable without wasting space.
- Input: Launch the app.
- Expected: Existing Epi title and greeting; header fills the width; a small rounded cat photo and a left-aligned white Epi card. Text contrasts clearly with the background; no image distortion. No assets or background graphics obstruct the conversation.

### 2. Multi-line responses and asymmetric layout

- Aim: Keep whole responses together and clearly distinguish commands from replies.
- Inputs:

```text
todo read book
event discuss the project plan and prepare the final demonstration with the team /from 2019-12-02 1400 /to 2019-12-02 1600
deadline return library books /by 2019-12-01 1800
mark 1
list
sort date
```

- Expected: Right-aligned compact user bubbles labelled `You`; wider left-aligned Epi cards labelled `Epi`. Each `list`/`sort date` response has one avatar and one card containing its heading and all three tasks. `sort date` shows original numbers 3, 2, 1. Task 1 remains marked. Long text wraps naturally.

### 3. Resize the same window

- Aim: Confirm live relayout rather than only initial rendering at fixed sizes.
- Input: Drag the window to its minimum size (420x480 outer window), enlarge it to roughly 1000x700, then shrink it again.
- Expected: Header, input, and Send remain usable. Cards grow/shrink and reflow without horizontal scrolling or cut-off lines. Long conversation history is available through vertical scrolling. The app enforces its minimum window size.

### 4. Keyboard and mouse

- Aim: Preserve both input methods and make focus visible.
- Input: Submit `list` using Enter; submit `sort date` by clicking Send; type another command immediately. Use Tab/Shift+Tab to move between input and Send; activate the focused Send button with Space.
- Expected: Each submission occurs exactly once, clears the input, and returns focus to it. The input/button outline indicates keyboard focus. The latest reply scrolls into view; older replies remain available by scrolling up. Hover and pressed button states are visible.

### 5. Long content and existing errors

- Aim: Ensure unusual content does not break presentation or change backend behavior.
- Input: Add a very long description including an unbroken word; run `list`, `find book`, `mark 999`, and `unknown-command`.
- Expected: Long commands and replies wrap within the window. Existing cat-themed error text remains readable and unchanged. Actual errors have a pale red background, a red accent, and the label `Epi - needs attention`, without relying on colour alone. No crash or overlapping input bar.

### 6. Damaged-file protection and repair

- Aim: Check the visible recovery workflow without risking real tasks.
- Setup: Only in the disposable JAR test folder, close Epi, back up its synthetic `data/epi.txt`, and insert a line containing `broken record` between valid task records. Restart Epi.
- Inputs: `list`, `todo must not be saved`, `mark 1`, and `delete 1`.
- Expected: Startup shows one warning card naming the bad line and repair/restart instructions. List contains the valid tasks only, in file order with consecutive task numbers. Every edit is rejected, and the whole file (including the bad line) remains unchanged. Read-only commands continue working.
- Recovery: Close Epi, restore the synthetic backup, and restart. The warning disappears; valid edits work again. Never run this fixture procedure on personal task data.

### 7. External file changes

- Aim: Refuse a stale overwrite if another process changes the task file.
- Setup: Use only the disposable JAR folder. With Epi open and a saved task present, append a valid `T | 0 | external task` record to its synthetic file in a text editor.
- Inputs: `mark 1`, then `list`.
- Expected: Mark shows the external-change error asking for restart, not a success reply. List still shows the original in-memory state, without the attempted mark. The external file contents survive. Close and restart Epi to load the new record before making further edits.

Record OS/display scaling, pass/fail, and any screenshots when running these manual checks. Automated scene snapshots do not replace checks at other operating-system scaling settings.
