# Mouse User Guide

Mouse is a small JavaFX chatbot that hoards your to-dos, deadlines, and events like crumbs in a pantry.

Talk to it in the GUI window, or from the command line.
Tasks are saved to `data/mouse.txt` so they come back the next time you open Mouse.

![Mouse GUI](Ui.png)

## Quick start

1. Install **Java 25**.
2. Download `mouse.jar` from the [latest release](https://github.com/Sea1vester/ip/releases).
3. Copy the JAR into an empty folder.
4. In a terminal, run `java -jar mouse.jar`.
5. Type a command and press Enter, or click **Stash**.
6. Type `help` to see the command list.
7. Type `bye` to exit.

You can also run from source with `./gradlew run`.
The CLI entry point is `mouse.Mouse`.

Indexes in commands start at `1` (the first crumb in the current list).

## Add a to-do

`todo DESCRIPTION`

Example: `todo read book`

```
Stashed this crumb:
  [T][ ] read book
The stash now holds 1 crumb.
```

## Add a deadline

`deadline DESCRIPTION /by WHEN`

`WHEN` can be an ISO date (`2019-12-02`), a date and time (`2019-12-02 1800`), a slash date (`2/12/2019`), or free text such as `Friday`.

Example: `deadline return book /by 2019-12-02`

```
Stashed this crumb:
  [D][ ] return book (by: Dec 02 2019)
The stash now holds 2 crumbs.
```

Impossible dates such as `2019-02-30` are rejected.
Use `/by` only once.

## Add an event

`event DESCRIPTION /from START /to END`

Example: `event meeting /from Mon 2pm /to 4pm`

```
Stashed this crumb:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
The stash now holds 3 crumbs.
```

If both `START` and `END` are real dates, `/from` must be earlier than `/to`.
Use `/from` and `/to` only once each.

## List tasks

`list`

Shows every saved crumb, numbered from 1.

## Mark and unmark

`mark INDEX`

Marks that crumb as done.

`unmark INDEX`

Marks that crumb as not done.

Example: `mark 1`

```
Nibble done. Marked this crumb:
  [T][X] read book
```

## Delete a task

`delete INDEX`

Removes that crumb and renumbers the remaining ones.

## Find tasks

`find KEYWORD`

Shows crumbs whose description contains `KEYWORD` (case-insensitive).
Each match is numbered with its `list` index, so `delete`, `mark`, `unmark`, and `priority` can use that same number.

Example: `find book`

```
Crumbs matching that sniff:
1.[T][X] read book
2.[D][ ] return book (by: Dec 02 2019)
```

## Set priority

`priority INDEX high|low|none`

Sets urgency on an existing crumb.
High and low show up as `(priority: high)` or `(priority: low)` in the list.
`none` clears the priority.

Example: `priority 1 high`

```
OK, Mouse tagged this crumb:
  [T][X] read book (priority: high)
```

## Help and exit

`help` prints the command guide.

`bye` closes Mouse.

`list`, `help`, and `bye` do not take extra words.

## Saving

Mouse writes the full stash to `data/mouse.txt` after each add, mark, unmark, delete, or priority change.
Closing the app and opening it again loads that file.

If the save file is missing, Mouse starts with an empty stash.
If the file cannot be read, Mouse starts empty and says so.
Corrupt lines in the file are skipped, and Mouse tells you how many it ignored.

## Common mistakes

Mouse answers errors in a reddish bubble (GUI) or with a `SQUEAK!!!` prefix (CLI). Typical cases:

- Unknown command
- Missing description, `/by`, `/from`, or `/to`
- A flag used more than once
- A date that looks real but is not, such as 30 February
- An event whose start is not before its end
- The same crumb already in the stash
- A crumb number that is missing, zero, extra, or out of range

## Command summary

- `todo DESCRIPTION`
- `deadline DESCRIPTION /by WHEN`
- `event DESCRIPTION /from START /to END`
- `list`
- `mark INDEX`
- `unmark INDEX`
- `delete INDEX`
- `find KEYWORD`
- `priority INDEX high|low|none`
- `help`
- `bye`

## Credits

The GUI started from the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html) used in CS2103T
(Jeffry Lum and Damith C. Rajapakse).
Command, storage, and pantry-mouse behaviour were written for this iP.
