# Ekud User Guide

**Ekud** is a desktop chatbot for keeping track of todos, deadlines, and events.
You type commands; Ekud replies in a chat window. Tasks are saved automatically
so they are still there the next time you open the app.

![Screenshot of the Ekud window](Ui.png)

## Quick start

1. Install **Java 25** (or later) on your computer.
2. Download the latest `ekud.jar` from the
   [GitHub Releases](https://github.com/Cha0yue/ip/releases) page.
3. Copy `ekud.jar` into an empty folder. That folder becomes Ekud's home folder
   (the save file is created next to the JAR, not next to the project source).
4. Open a terminal in that folder and run:

   ```
   java -jar ekud.jar
   ```

   A window titled **Ekud** should open. Type a command in the box at the bottom
   and press **Enter** (or click **Send**).
5. Try `help`, then a few commands from [Features](#features) below.

## Features

**Command format**

- Words in `UPPER_CASE` are values you type. For example, in `todo DESCRIPTION`,
  replace `DESCRIPTION` with `read book`.
- Command words are **lowercase** and **case-sensitive**. `todo` works; `TODO`
  does not.
- Extra text after commands that take no arguments (`list`, `help`, `bye`) is
  rejected.
- Leading and trailing spaces are ignored.

**Dates and times**

Deadlines, events, and `on` accept:

- Date: `2019-12-02` or `2/12/2019` (day/month/year)
- Optional time: `1800` or `18:00`

Ekud shows dates as `Dec 02 2019`, and times as `6:00 PM`.

### Adding a todo: `todo`

Adds a task with no date.

Format: `todo DESCRIPTION`

Example: `todo read book`

```
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
```

### Adding a deadline: `deadline`

Adds a task that is due by a date or date-time.

Format: `deadline DESCRIPTION /by DATE`

Examples:

- `deadline return book /by 2019-12-02`
- `deadline submit report /by 2/12/2019 1800`

```
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
```

### Adding an event: `event`

Adds a task with a start and an end.

Format: `event DESCRIPTION /from START /to END`

Put `/from` before `/to`. The end cannot be before the start.

Example: `event meeting /from 2019-12-02 1400 /to 2019-12-02 1600`

```
Got it. I've added this task:
  [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
Now you have 3 tasks in the list.
```

### Listing all tasks: `list`

Shows every task, numbered from 1. Use those numbers with `mark`, `unmark`, and
`delete`.

Format: `list`

```
Here are the tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Dec 02 2019)
3. [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
```

If there are no tasks, Ekud says `Your task list is empty.`

### Finding tasks: `find`

Lists tasks whose **description** contains the given text. Matching ignores
case. Dates are not searched.

Format: `find KEYWORD`

Extra words are part of the search text, so `find read book` looks for
`read book`.

Examples:

- `find book` matches `read book` and `return book`
- `find BOOK` matches the same tasks
- `find lecture` prints `No tasks matching "lecture".` if nothing matches

### Listing tasks on a date: `on`

Shows deadlines due on that calendar day, and events whose start–end range
includes that day. Todos are never included.

Format: `on DATE`

Example: `on 2019-12-02`

```
Here are the deadlines/events on Dec 02 2019:
1. [D][ ] return book (by: Dec 02 2019)
2. [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
```

### Marking a task as done: `mark`

Marks the task with that list number as done (`[X]` in the status box).

Format: `mark NUMBER`

Example: `mark 1`

```
Nice! I've marked this task as done:
  [T][X] read book
```

### Marking a task as not done: `unmark`

Clears the done mark on that task.

Format: `unmark NUMBER`

Example: `unmark 1`

### Deleting a task: `delete`

Removes that task. Later tasks shift down so numbers stay consecutive.

Format: `delete NUMBER`

Example: `delete 2`

### Viewing help: `help`

Shows a short list of commands and date formats.

Format: `help`

### Exiting: `bye`

Saves nothing extra (earlier commands already saved) and closes Ekud.

Format: `bye`

```
Bye. Hope to see you again soon!
```

### Saving the data

Ekud saves after every change (`todo`, `deadline`, `event`, `mark`, `unmark`,
`delete`). You do not save by hand.

The file is `data/ekud.txt` inside the folder you ran the JAR from. If the file
is missing, Ekud starts with an empty list. If a line in the file is corrupt,
that line is skipped and the rest still load.

## Command summary

- **Todo:** `todo DESCRIPTION` — e.g. `todo read book`
- **Deadline:** `deadline DESCRIPTION /by DATE` — e.g. `deadline return book /by 2019-12-02`
- **Event:** `event DESCRIPTION /from START /to END` — e.g. `event meeting /from 2019-12-02 1400 /to 2019-12-02 1600`
- **List:** `list`
- **On a date:** `on DATE` — e.g. `on 2019-12-02`
- **Find:** `find KEYWORD` — e.g. `find book`
- **Mark done:** `mark NUMBER` — e.g. `mark 1`
- **Unmark:** `unmark NUMBER` — e.g. `unmark 1`
- **Delete:** `delete NUMBER` — e.g. `delete 2`
- **Help:** `help`
- **Exit:** `bye`

## FAQ

**What do the coloured bubbles mean?**
Your messages sit on the right. Ekud replies on the left. Adding a task,
changing a mark, deleting, and errors each use a different colour so you can
spot them quickly.

**Can I use the app from the terminal instead?**
Yes. From IntelliJ, run `ekud.Ekud`. The commands are the same; only the window
is different.

**How do I move my tasks to another computer?**
Copy `data/ekud.txt` into the `data` folder next to `ekud.jar` on the other
computer (create `data` if it does not exist).
