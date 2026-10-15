# Thomas manual test plan

Checks that need a person at a real machine. Everything the chatbot *says* is
already pinned by the two automated suites: JUnit for each class, and
`ui-test-plan.md` for the console end to end. What is left is what a program
cannot see about itself -- how the window looks on a given screen, whether it
survives a different operating system or language setting, and what happens at
the moments JavaFX takes over, such as closing after `bye`.

Run each check on every row of the environment table that applies, and record
the result in the table at the bottom. A check that cannot be run on a given
row (no such machine to hand) is recorded as *not run*, not as passed.

## Environments

| Id | OS | Screen | OS language / locale | Why it is on the list |
|----|----|--------|----------------------|-----------------------|
| E1 | macOS | Built-in Retina (2x scaling) | English | Development machine; the baseline. |
| E2 | macOS | External 1080p (1x scaling) | English | Font and avatar sizes at 1x; text that looks fine at 2x can look coarse. |
| E3 | Windows 10 or 11 | 1080p, 100% scaling | English | The JAR bundles Windows JavaFX natives; the window has never been drawn there. |
| E4 | Windows 10 or 11 | 125% or 150% display scaling | English | Non-integer scaling is where JavaFX layouts most often clip or blur. |
| E5 | Linux (Ubuntu or similar) | Any | English | The JAR bundles Linux natives too; fonts differ, so line heights differ. |
| E6 | Any of the above | Any | **Chinese (Simplified)**, system-wide | A locale with its own month names, am/pm markers and digit shapes. |
| E7 | Any of the above | Any | **German** | A locale with its own month abbreviations and a 24-hour clock convention. |
| E8 | Any of the above | Smallest window the app allows (360 x 300) | English | The minimum size set in `Main.start`; layout must still work there. |
| E9 | Any of the above | Full-screen or maximized on a wide monitor | English | Bubbles must not stretch edge to edge; a user line must stay a column on the right. |

How to run the app for these checks, from the project root:

```bash
./gradlew run
```

Or from the shaded JAR, which is what the Windows and Linux rows are really
about, since it is the JAR that carries the platform natives:

```bash
./gradlew shadowJar
```

```bash
java -jar build/libs/thomas.jar
```

Each run works on `./data/tasklist.txt` under the directory the JAR was started
from. Start every row from an empty `./data`, so that the checks do not depend
on what an earlier run left behind.

## Checks

### M1: The window opens and greets

1. Start the app.
2. **Expect:** a window titled "Thomas the Tank Engine", the avatar on the left
   of a first bubble reading the two greeting lines, the input field already
   focused (typing goes straight into it without a click), and the button
   labelled "Peep!".
3. **Expect:** no console output other than JavaFX's own start-up messages, and
   no stack trace.

### M2: Every bubble color is reachable

Type each line and look at the reply bubble's tint. The colors are defined in
`src/main/resources/css/dialog-box.css`; the point of the check is that the
right one is applied, not the exact shade.

| Type | Expected reply tint |
|------|---------------------|
| `todo read book` | green border (`add-label`) |
| `mark 1` | blue border (`marked-label`) |
| `unmark 1` | blue border (`marked-label`) |
| `list` | plain cream reply bubble, no colored border |
| `find book` | plain cream reply bubble |
| `on 2019-12-02` | plain cream reply bubble |
| `delete 1` | amber border (`delete-label`) |
| `undo` | amber border (`delete-label`) |
| `blah` | red border and pink fill (`error-label`) |
| `delete 99` | red border and pink fill (`error-label`) -- a command that parsed but failed gets the error bubble too |

Your own typed lines must each appear as a dark blue bubble on the right, with
no avatar.

### M3: `bye` closes the window, but not before the farewell is seen

1. Type `bye`.
2. **Expect:** the farewell bubble appears, stays readable for about a second
   and a half, and then the window closes on its own. It must not close so fast
   that the farewell is never painted, and it must not stay open.
3. **Expect:** the process has exited (no lingering Java process).

### M4: Long content wraps rather than widening the window

1. Add a todo with a description of around 200 characters, then `list`.
2. **Expect:** both the echo of your line and the reply wrap inside their
   bubbles. The window does not grow wider, and no horizontal scroll bar
   appears.
3. Add fifteen or more tasks and `list`.
4. **Expect:** the conversation scrolls, and the newest bubble is scrolled into
   view on its own after every command without touching the scroll bar.

### M5: The smallest window still works (E8)

1. Drag the window down to its minimum size.
2. **Expect:** it refuses to go smaller than roughly 360 x 300; the input bar
   and button stay fully visible; the conversation area keeps at least a bubble
   or two in view; nothing is clipped off the right edge.

### M6: A wide window keeps the two sides apart (E9)

1. Maximize the window.
2. Type a short line and a long line.
3. **Expect:** your bubbles stay on the right and are capped at about
   three-quarters of the width; Thomas's bubbles stay on the left. Neither kind
   stretches edge to edge.

### M7: Display scaling does not blur or clip (E2, E4)

1. On each scaling setting, open the app and run M2.
2. **Expect:** text is crisp, the avatar is a clean circle (not an ellipse or a
   square with corners showing), and bubble borders are not cut off at the
   bottom of the conversation.

### M8: Dates read in English whatever the OS language (E6, E7)

This is the manual counterpart to the locale cases in `TaskTest`, which switch
the JVM's default locale under JUnit. Running the real app under a real OS
language setting checks the same thing the way a user would meet it.

1. With the OS set to Chinese (E6) or German (E7), start the app.
2. Type `deadline return book /by 2019-12-02 1800` and then `list`.
3. **Expect:** the reply reads `(by: Dec 02 2019, 6:00 PM)` -- English short
   month, 12-hour clock, `PM` in capitals. Not `12月`, not `Dez`, not `18:00`.
4. Type `on 2019-12-02`.
5. **Expect:** the header reads `Here is my timetable for Dec 02 2019:`.
6. Open `./data/tasklist.txt` in a text editor.
7. **Expect:** the line reads `D | 0 | return book | 2019-12-02 1800`, in
   ASCII digits. Copy the file to an English-locale machine and start the app
   there: the task must load, with no "couldn't read it" bubble.

### M9: Non-ASCII text survives the round trip

Descriptions are not limited to English, and the save file must bring them back
whole. `StorageTest` already round-trips such a description under JUnit; this
check repeats it through the real app on each OS row, since the file encoding is
the one place where the OS itself can still make a difference.

1. Type `todo 读书` and `todo Bücher lesen`, then `bye`.
2. Start the app again and type `list`.
3. **Expect:** both descriptions come back exactly as typed, not as `??` or
   mojibake. `Storage` reads and writes through `FileWriter` and
   `Scanner(File)`, which use the JVM's default charset; since Java 18 that is
   UTF-8 on every platform, so this should pass everywhere. If it does not, the
   first thing to check is whether the JVM was started with a
   `-Dfile.encoding` override, which is the one way the default still differs.

### M10: Loading warnings reach the window

The console prints these as separate blocks; the window must show them too, or
a user whose tasks did not load will never learn it.

1. With the app closed, edit `./data/tasklist.txt` so one line reads
   `X | 0 | mystery` and another is a valid task.
2. Start the app.
3. **Expect:** the first bubble carries the greeting followed by one
   "left a saved line in the yard" line for the damaged one, and `list` shows
   the valid task.
4. Close the app, delete `./data/tasklist.txt` and create a *folder* with that
   name in its place. Start the app.
5. **Expect:** the first bubble carries the greeting followed by the "couldn't
   read your saved tasks" warning and "Setting off with an empty train." Typing
   `todo x` then replies with a "couldn't save your tasks" warning above the
   confirmation, in the green add bubble rather than the red error one, since
   the command itself succeeded.

### M11: The empty line is ignored in the window

1. With the input field empty, press Enter, and click "Peep!".
2. **Expect:** nothing is added to the conversation. (The console answers a
   blank line with a message; the window deliberately does not, since a slip
   of the Enter key is not worth a pair of bubbles.)

### M12: Keyboard-only use

1. Without touching the mouse: type a command, press Enter; type another,
   press Enter.
2. **Expect:** each is sent; focus stays in the input field after each reply;
   the field is cleared after each send.

## Results

Fill in one row per environment run. Use *pass*, *fail* (with a note), or *not
run*.

| Env | M1 | M2 | M3 | M4 | M5 | M6 | M7 | M8 | M9 | M10 | M11 | M12 | Date | Notes |
|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|------|-------|
| E1 | | | | | | | | n/a | | | | | | |
| E2 | | | | | | | | n/a | | | | | | |
| E3 | | | | | | | | n/a | | | | | | |
| E4 | | | | | | | | n/a | | | | | | |
| E5 | | | | | | | | n/a | | | | | | |
| E6 | | | | | | | n/a | | | | | | | |
| E7 | | | | | | | n/a | | | | | | | |
| E8 | | | | | | n/a | | n/a | | | | | | |
| E9 | | | | | | | | n/a | | | | | | |
