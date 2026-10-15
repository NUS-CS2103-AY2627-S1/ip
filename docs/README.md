# Clue User Guide

![Project Showcase](Ui.png)

**Clue** is a chatbot with robust task recording features. 
Your tasks are persisted to disk, and can range from todos, deadlines, to events.
Saving is automatically done when the application is closed.

Type commands into the textbox, then simply press Enter or the Enter button to the left to input the command.

## Simple commands

`list`

List out all your saved tasks, numbered by creation order.

---

`bye`
Exits the program.

## Adding tasks

`todo <name>`

Creates a ToDo with the given name

---

`deadline <name> /by <datetime>`

Creates a Deadline with the given name, to be done by the given time.
Datetimes are in  \[YYYY-]MM-DD\[ HH:mm] format, where square brackets denote optional parts.

Examples:
`deadline task /by 10-23`
`deadline bask /by 11-10 09:53`

---

`event <name> /from <datetime> /to <datetime>`

Creates an Event with the given name that lasts in a range.
Datetimes are in \[YYYY-]MM-DD\[ HH:mm] format, where square brackets denote optional parts.

Examples:
`event thing /from 09-12 /to 09-15`

## Index based commands

`mark <index>`

Marks the `<index>`<sup>th</sup> task as done.
Indexes are as seen in `list` output.

---

`unmark <index>`

Unmarks the `<index>`<sup>th</sup> task as done.
Indexes are as seen in `list` output.

---

`delete <index>`

Deletes the `<index>`<sup>th</sup> task from the list.
Indexes are as seen in `list` output.

## Fuzzy Search
`find <term>`
Looks for instances of `<term>` in task names. Includes task names that may not match the given term exactly e.g. 1 or 2 letters off.
Tasks that match the term more exactly are listed first.


