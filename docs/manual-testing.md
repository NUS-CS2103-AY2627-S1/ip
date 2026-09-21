# Manual testing

Everything that can be tested automatically is. `./gradlew check` runs 265
JUnit tests and Checkstyle, `text-ui-test/runtest.sh` drives the terminal app
end to end, and GitHub Actions runs both on Ubuntu, macOS and Windows for every
push.

What follows is what is left: the GUI, which cannot be driven without a JavaFX
toolkit, and the environment differences that only show up on a real machine.

## Why the GUI is not covered by JUnit

A JavaFX control cannot be created without a running toolkit, and a running
toolkit needs a display. The parts of the GUI worth testing — does the window
resize sensibly, is an error obvious enough to notice — are judgements about
appearance rather than assertions about values.

The logic behind the GUI *is* covered: `Goat.getResponse`, `getWelcomeMessage`,
`isExitRequested` and `isLastResponseAnError` are the whole of the interface
between the window and the rest of the program, and `GoatTest` exercises all
four. What is untested is the drawing.

## Starting up

1. **First run.** Copy `goat.jar` into an empty folder, `java -jar "goat.jar"`.
   Expect the greeting as the first bubble, an empty conversation below it, and
   the cursor already in the text field so a command can be typed immediately.
2. **Second run.** Add a task, close the window, start it again.
   Expect the task list to survive, and a `data/goat.txt` beside the jar.
3. **Damaged save file.** Edit `data/goat.txt` to read
   `D | 0 | x | 2 Dec 2019` and start the app.
   Expect the opening bubble to be an **error** bubble — amber, ruled — naming
   line 1, and the app still usable with an empty list.

## The three kinds of turn

4. Type `todo read book`.
   Expect a green bubble on the **right** holding what you typed, then Goat's
   reply on the **left**: no bubble, monospaced, behind a thin grey rule.
5. Type `blah`.
   Expect the reply to be amber-tinted with an amber rule. It should be
   obvious which of the last few replies was the complaint without reading
   any of them.
6. Add several tasks and type `list`.
   Expect the numbers and the `[T][ ]` markers to line up in a column. This is
   what the monospaced face is for; if they do not line up, the font fell back.

## Resizing

7. Drag the window narrow, down to its minimum.
   Expect a user bubble to stay at roughly three-quarters of the width, replies
   to wrap rather than being cut off, and no horizontal scrollbar.
8. Drag it wide.
   Expect the conversation to fill the width with no band of background down
   either side, and long task lines to stop wrapping.
9. Make it very short.
   Expect the input bar to stay put at the bottom and the conversation to
   scroll.

## Keyboard and controls

10. With the field empty, expect **Send** to be greyed out and do nothing.
11. Type one character; expect Send to become active.
12. Press <kbd>Enter</kbd> rather than clicking Send. Expect the same result,
    and the field to stay focused so the next command needs no click.
13. Type `bye`. Expect the farewell to appear, the field to be disabled, the
    status to read `closing...`, and the window to close about a second and a
    half later — long enough to read the farewell.

## Other operating systems

The three CI platforms cover compilation and the whole automated suite. What
they cannot check is appearance.

14. **Windows.** Expect Segoe UI in the interface and Consolas in the replies.
    Check the task-list columns still line up.
15. **Linux.** Neither Avenir Next nor Menlo is likely to be present, so both
    fall back. Check the replies are still monospaced — if they are not, the
    columns will be visibly ragged.
16. **A HiDPI display.** Check the window icon is not blurry and the rules
    beside replies are still visible at 3 pixels.

## OS language settings

Goat is written in English and stays in English; what must not change is what
it *finds*.

17. Start with `java -Duser.language=tr -Duser.country=TR -jar "goat.jar"`.
    Add `todo TITLE deed`, then `find title`.
    Expect it to be found. Turkish lowercases `I` to a dotless `ı`, so folding
    case with the machine's locale would silently match nothing here. This was
    a real bug, fixed by folding under `Locale.ROOT`; `LocaleTest` now pins it,
    but it is worth seeing once on a real machine.
18. In the same run, add a deadline and check the date still reads
    `Dec 02 2019` rather than a Turkish month name, and that `data/goat.txt`
    still holds `2019-12-02 1800`. A save file has to move between machines.
19. Repeat with `-Duser.language=zh -Duser.country=CN`. Expect the same.

## Known limits

- **Apple Silicon.** The JavaFX bundled in the jar is built for Intel, per the
  version the tutorial specifies. It works on a JDK that ships JavaFX itself
  (Azul Zulu FX, Liberica Full); on a plain JDK expect an `UnsatisfiedLinkError`
  about architecture, and use `./gradlew run` from a clone instead.
- A description may not contain `|`. The save file separates fields with it, so
  such a description could not be read back. The app refuses it and says why.
