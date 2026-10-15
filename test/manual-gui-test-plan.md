# Manual test plan for the window

The JUnit tests cover almost every line Billy has, but four classes are left to
this document instead: `Main`, `MainWindow`, `DialogBox` and `Launcher`. They do
nothing except build JavaFX controls and hand them to the toolkit, so a test of
them would be a test of JavaFX. What is worth checking about them is how they
look and feel, which a person has to look at.

Everything below is checked by hand before an increment is called done.
`./gradlew run` starts the window.

## MT-01 The window opens and greets the user

1. Delete `data/billy.txt` if it exists, so this is a first run.
2. Run `./gradlew run`.

Expect: the window opens at roughly 420x620. Billy's greeting is the first
message, in a white card on the left with a small round avatar beside it. No
"welcome back" note, since there is no saved list.

## MT-02 The two speakers look different

1. Type `list` and press Enter.

Expect: `list` appears on the **right**, in a caramel bubble, no wider than about
three quarters of the window. Billy's reply appears on the **left**, in a white
card that uses the full width. The small corner of each bubble points toward its
own avatar.

## MT-03 An error is unmistakable

1. Type `blah`.

Expect: Billy's reply is a pale red card with a red bar down its left edge and a
warning sign before the text, clearly different from the white cards above it.
2. Type `list` again. Expect: the reply is a normal white card, i.e. the error
styling does not stick to later replies.

## MT-04 The window resizes

1. Drag the window's right edge in as far as it will go.

Expect: it stops at about 360px wide. Text rewraps rather than being cut off, no
horizontal scrollbar appears, and the send button stays visible.

2. Drag it out to roughly twice the width.

Expect: Billy's cards grow with the window, the user's bubbles stay capped at
about three quarters of the width, and the input box stretches.

## MT-05 The conversation scrolls to the newest message

1. Add enough tasks to fill the window, then add one more.

Expect: the view follows the newest message automatically.
2. Scroll up with the mouse wheel, then send another command. Expect: scrolling
by hand works, and the view jumps back to the bottom on the new message.

## MT-06 The window closes on `bye`

1. Type `bye`.

Expect: the farewell appears, the input box and send button go grey, and the
window closes about a second and a half later, leaving time to read the
farewell.

## MT-07 The task list survives a restart

1. Add a task, then close the window with `bye`.
2. Run `./gradlew run` again.

Expect: the greeting is followed by a note saying how many tasks were picked up,
and `list` shows the task added before.

## MT-08 The icon

Expect: the window's title bar and the dock or task bar show Billy's avatar
rather than a generic Java icon.

## Worth checking on another machine

These cannot be checked from one computer, and are the reason the fonts and
colors are written the way they are:

* **Windows and Linux.** The stylesheet asks for Trebuchet MS first, which
  ships on both macOS and Windows; a Linux machine without it falls back through
  the list. Check the text is still legible and nothing overlaps.
* **A high-resolution screen.** Check the avatars are not blurry. They are
  256px images drawn at 28px, so they should have room to spare.
* **A different system language.** Dates are formatted with `Locale.ENGLISH` on
  purpose, so `Dec 2 2019` should read the same on a machine set to another
  language rather than following the system locale.

## Code deliberately left untested

Some lines are not covered by JUnit and are not meant to be:

* `Billy()` (the no-argument constructor) and `Billy.main` both work against the
  real `data/billy.txt`. Every other test uses a temporary folder precisely so
  that running the tests never touches a real task list.
* `assert` statements in `Storage.splitFields`, `TaskList.remove` and the
  `TaskDate` constructor state invariants. Covering them would mean arranging for
  an invariant to be false, which is the thing they exist to rule out.
* The empty-group path in `CommandWord.describeCommands` cannot run, because
  every group has at least one command in it. It is kept so that adding a group
  later cannot print a heading with nothing under it.
* `Storage.save` skips creating a folder when the save path has no parent, which
  only happens if Billy is pointed at a bare file name. The application always
  uses `data/billy.txt`.
