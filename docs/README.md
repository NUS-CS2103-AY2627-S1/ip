# Gepit User Guide

Gepit is a friendly desktop task manager for keeping track of todos, deadlines, and multi-day events. It saves your tasks automatically, so they are available the next time you start the app.

![Gepit user interface](Ui.png)

## Quick start

Type a command in the box at the bottom of the window, then press **Enter** or click **Send**. Dates use the `yyyy-MM-dd` format. Type `help` at any time to see a compact command summary.

## Viewing tasks

Use `list` to show all tasks and their numbers. `[X]` means a task is complete, while `[ ]` means it is incomplete.

## Adding tasks

- Todo: `todo read chapter 3`
- Deadline: `deadline submit report /by 2026-09-18`
- Event: `event orientation camp /from 2026-09-20 /to 2026-09-22`

An event's end date must be later than its start date.

## Managing tasks

Use the number shown by `list`:

- `mark 2` marks task 2 as complete.
- `unmark 2` marks task 2 as incomplete.
- `update 1 read chapters 3 and 4` replaces task 1's description.
- `delete 3` removes task 3.

Task numbers can change after deletion, so run `list` again before another numbered command.

## Finding tasks

Use `find report` to find descriptions containing `report`. Search is case-insensitive.

## Exiting and storage

Type `bye` to receive Gepit's farewell, then close the window. Successful changes are saved automatically in `data/gepit.txt`, relative to the folder from which the JAR is run.

## Troubleshooting

- For an invalid date, use a real calendar date in `yyyy-MM-dd` format, such as `2026-09-18`.
- If a numbered command fails, use `list` and choose an existing positive task number.
- If the JAR does not start, confirm `java -version` reports Java 25, then run `java -jar Gepit.jar` from a terminal to see the error.

## Acknowledgements

The following images are used as profile pictures in Gepit's graphical interface:

- Gepit profile picture: [The World of Herobrine on CurseForge](https://www.curseforge.com/minecraft/mc-mods/the-world-of-herobrine)
- User profile picture: [Clown Emoji PNG on Pinterest](https://in.pinterest.com/pin/clown-emoji-png--12033123998564035/)
