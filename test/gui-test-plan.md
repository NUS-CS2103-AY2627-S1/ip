# Nori GUI Test Plan

This file covers the parts of Nori that `test/ui-test-plan.md` cannot reach.
That plan drives the console interface and compares whole sessions of text, so it says nothing about colour, type, layout or the keyboard.
Everything here is checked by hand, against a running window.

## How to run

From the repository root, with Java 25 active:

```powershell
.\gradlew.bat run
```

Work through each case below in a single session unless a case says otherwise.
A case fails if any of its expected observations does not hold; record what was seen instead.

## Test G1: The window names the product

**Aim:** Verify that the product is named as Nori everywhere the window appears, as required of a product that is not Duke.

**Steps:**

1. Start the application.
1. Read the title bar.
1. Read the header at the top of the window.
1. Look at the application's button on the task bar, and at its entry when switching windows.

**Expected:**

* The title bar reads `Nori — Your task penguin`.
* The header shows the wordmark `NORI` and the tagline `Tiny flippers. Big plans.`.
* The task bar shows Nori's penguin, cropped square, rather than a generic Java icon.
* No part of the window says `Duke`.

## Test G2: An error is drawn differently from an ordinary reply

**Aim:** Verify that a reply reporting something Nori could not do is told apart from one reporting success, by more than colour alone.

**Steps:**

1. Enter `todo read book`.
1. Enter `deadline submit report`.
1. Enter `bye` only when the rest of this case is done.

**Expected:**

* The reply to `todo read book` sits on a white card, its speaker label reads `NORI`, and it has no bar down its left edge.
* The reply to `deadline submit report` sits on a pale warm ground, carries a thick warm bar down its left edge, and its speaker label reads `NORI · PROBLEM`.
* The error text stays comfortably readable against its ground.
* The difference is still visible with the screen set to greyscale, because the left bar and the label carry it.

## Test G3: Task lists keep their columns

**Aim:** Verify that the conversation is set in a monospaced face, so the columns a task list and a schedule are built from line up.

**Steps:**

1. Enter `todo read book`.
1. Enter `deadline return book /by 2019-06-06`.
1. Enter `event exam /from 2019-06-06 0900 /to 2019-06-06 1200`.
1. Enter `event book fair /from 2019-06-06 1000 /to 1800`.
1. Enter `list`.
1. Enter `schedule 2019-06-06`.

**Expected:**

* In the `list` reply, every `[T]`, `[D]` and `[E]` type box starts at the same distance from the left edge of the bubble.
* In the `schedule` reply, `09:00` and `10:00` occupy the same width, and the rows under `Due:` and `Anytime:` are indented to sit directly beneath the timed rows above them.
* No row is ragged against its neighbours.

## Test G4: The window can be resized, and the conversation follows

**Aim:** Verify that the window works at the size a user gives it, down to its stated minimum.

**Steps:**

1. Drag the window's corner inwards as far as it will go.
1. Read the header, the conversation and the composer at that size.
1. Maximise the window.
1. Read a long reply, such as the one to `help`.

**Expected:**

* The window stops shrinking at roughly 480 by 420 pixels.
* At that size the header still shows the wordmark and the status pill, the command field and the **Send** button are both still usable, and nothing is cut off at the right edge.
* No horizontal scroll bar appears at any size.
* When maximised, the message bubbles grow with the window rather than staying a fixed width, but stop short of the far edge so that each side of the conversation stays obvious.

## Test G5: The two speakers do not look alike

**Aim:** Verify the asymmetry of the conversation, which is between a user and an application rather than between two people.

**Steps:**

1. Enter `list`.
1. Compare the bubble carrying the command with the bubble carrying the reply.

**Expected:**

* The user's command sits on the right, on a solid dark chip, with no avatar beside it.
* Nori's reply sits on the left, on a light card, with the penguin avatar beside it.
* The two bubbles have their corners rounded differently, so the sides differ in shape as well as in colour and position.

## Test G6: An earlier command can be recalled

**Aim:** Verify that the arrow keys walk through the commands entered this session.

**Steps:**

1. Enter `todo read book`.
1. Enter `list`.
1. Enter `list` a second time.
1. Press the up arrow three times, reading the command field after each press.
1. Press the down arrow three times, reading the command field after each press.
1. Press the up arrow once, edit the recalled text, and press Enter.

**Expected:**

* The first up arrow shows `list`; the second shows `todo read book`; the third leaves `todo read book` in place rather than wrapping round.
* `list` appears once in the history despite being entered twice in a row.
* The down arrows walk back towards the newest command and then leave the field empty.
* The edited command runs as edited.

## Test G7: The keyboard's position is visible

**Aim:** Verify that a user working without a mouse can see where their typing will go.

**Steps:**

1. Click in the command field.
1. Press Tab until focus leaves the field, watching the composer and the **Send** button.
1. Press Tab or Shift+Tab until focus returns to the command field.

**Expected:**

* While the command field holds the keyboard, the composer is ringed in the accent colour.
* The ring appears and disappears without anything on the row moving or changing size.
* The **Send** button shows a ring of its own when it holds the keyboard.

## Test G8: A damaged data file is reported as a problem

**Aim:** Verify that a startup failure is marked as an error rather than shown as an ordinary greeting.

**Steps:**

1. Close Nori.
1. Open `data/nori.txt` in an editor and replace its contents with `this is not a task`.
1. Delete `data/nori.txt.bak` if it exists.
1. Start the application.

**Expected:**

* The opening greeting appears as an ordinary reply.
* The message about the saved tasks appears in the error format, with the warm ground, the left bar and the `NORI · PROBLEM` label.
* Nori still accepts commands afterwards.

**Afterwards:** delete `data/nori.txt` so later sessions start clean.
