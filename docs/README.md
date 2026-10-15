# SkyNET User Guide
_Inspired by the 1984 Terminator film..._

SkyNET is a chatbot for organizing to-dos, deadlines, and events.

![SkyNET chatbot demo](Ui.png)

## Features

| Command | Usage | Purpose |
|---|---|---|
| Add to-do | `todo DESCRIPTION` | Adds a task |
| Add deadline | `deadline DESCRIPTION /by yyyy-MM-dd HH:mm` | Adds a deadline |
| Add event | `event DESCRIPTION /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm` | Adds an event |
| List tasks | `list` | Displays all tasks |
| Find tasks | `find KEYWORD` | Searches task by keyword |
| Mark done | `mark NUMBER` | Marks a task as completed |
| Unmark | `unmark NUMBER` | Marks a task as incomplete |
| Delete | `delete NUMBER` | Removes a task |
| Undo | `undo` | Reverses the latest change |
| Exit | `bye` | Closes SkyNET |

<u>_In addition, the following additions are also supported:_</u>
1. Color-coded chatbot responses
2. Proper save-and-load file system
4. Background music (for the ambience :P)

## Date format

Use `yyyy-MM-dd HH:mm`, for example:

```text
deadline submit report /by 2026-12-01 18:00
```

## Frequently Asked Questions

* Will this chatbot actually target me...?

      Ans: No worries, this is just a parody, but we give you no guarantees.

* What if I accidentally deleted a task/made a mistake?

      Ans: Please utilize the 'undo' feature. However, note that changes in a previous session cannot be undone.

* Arnold Schwarzenegger?

      Ans: yes.