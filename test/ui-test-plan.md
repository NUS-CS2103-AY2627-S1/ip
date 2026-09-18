# Computa UI test plan

- Program command: `java "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp _temp/ui-test-classes computa.Computa`
- Build command: `javac -encoding UTF-8 -d _temp/ui-test-classes src/main/java/computa/Computa.java src/main/java/computa/command/*.java src/main/java/computa/exception/*.java src/main/java/computa/storage/*.java src/main/java/computa/task/*.java src/main/java/computa/ui/Ui.java src/main/java/computa/util/*.java`
- Working directory: `.`
- Timeout seconds: `30`
- Reset files: `data/computa.txt`

Each test case starts a fresh program session. Output comparison is exact apart
from platform line endings and a final trailing newline.

## Manual test case: JavaFX chatbot GUI

### Aim

Verify that the JavaFX window starts through the separate launcher and presents
user commands as right-aligned bubbles and chatbot responses as left-aligned
bubbles. Confirm that user messages use pink bubbles with the user avatar,
chatbot responses use a white bubble with Computa's avatar, and invalid-command
replies have a red outline and pale red background. Confirm that conversation
bubbles reflow when the window is resized, and the
window scrolls to the newest message when the conversation exceeds its height.
Confirm that it supports entering commands, displaying responses, and ending the session.

### Inputs

```text
todo read book
list
bye
```

### Expected output

The window is titled `Computa`, shows the greeting in the conversation area, and
displays the entered commands and chatbot responses. The `list` response shows
`1.[T][ ] read book`; user bubbles are pink and display the user avatar, while
Computa's white response bubbles display its avatar. An invalid command produces
a pale red, red-outlined response bubble. After `bye`, the farewell is shown and
the command controls are disabled. Resizing the window does not clip message text or show a horizontal
scroll bar. After entering enough commands to exceed the conversation height,
the newest message is visible without manual scrolling.
The greeting and all lines from a `list` response each appear in one chatbot
bubble rather than as separate bubbles.

## Test case: exit immediately

### Aim

Verify that Computa prints its greeting and exits with the requested farewell.

### Inputs

```text
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: sort tasks by date

### Aim

Verify that the sort command orders dated tasks chronologically and places an
undated todo after them.

### Inputs

```text
deadline later /by 2020-01-10
todo buy milk
deadline sooner /by 2020-01-01
sort
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [D][ ] later (by: Jan 10 2020)
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] buy milk
Now you have 2 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [D][ ] sooner (by: Jan 01 2020)
Now you have 3 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
Tasks sorted by date.
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[D][ ] sooner (by: Jan 01 2020)
2.[D][ ] later (by: Jan 10 2020)
3.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: missing data file starts with an empty list

### Aim

Verify that Computa creates the missing data file and starts safely with no tasks.

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: load valid tasks and skip corrupted records

### Aim

Verify that valid records are loaded while malformed records are ignored without crashing.

### Setup

```text
python -c "from pathlib import Path; p=Path('data/computa.txt'); p.parent.mkdir(exist_ok=True); p.write_text('T | 1 | loaded task\nnot a valid task record\nT | 2 | invalid status\nX | 0 | unknown type\nD | 0 | missing due date |\nD | 0 | impossible due date | 2019-02-30\nE | 0 | missing range |  | 4pm\nE | 0 | impossible range | 2020-01-02 | 2020-01-01\nT | 0 | \nD | 0 | missing field\nE | 0 | missing field | from\nD | 0 | submit report | Friday\nD | 0 | stored date | 2019-10-15\n', encoding='utf-8')"
```

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][X] loaded task
2.[D][ ] submit report (by: Friday)
3.[D][ ] stored date (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: invalid input does not add a task

### Aim

Verify that an unknown command is rejected, while a following valid todo is stored and numbered correctly.

### Inputs

```text
blah
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Hmph! Making small talk won't get you anywhere.  ʕ ꈍᴥꈍʔ
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] read book
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: duplicate tasks are rejected

### Aim

Verify that adding an identical task shows an error without adding a second copy.

### Inputs

```text
todo read book
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] read book
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
Hmph! You already have that task on your list. No need to overwork yourself!
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: add and list a todo

### Aim

Verify that a todo is stored and displayed with the todo type and incomplete status.

### Inputs

```text
todo borrow book
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] borrow book
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: deadline and event details

### Aim

Verify that deadline and event date/time strings are stored and shown without date conversion.

### Inputs

```text
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 2 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[D][ ] return book (by: Sunday)
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: parse and query ISO dates

### Aim

Verify that ISO dates and date-times are parsed, displayed in a readable format,
and found by the date query command.

### Inputs

```text
deadline submit report /by 2019-10-15
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
event orientation /from 2019-10-14 /to 2019-10-16
deadline submit form /by 2/12/2019 1800
on 2019-10-15
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [D][ ] submit report (by: Oct 15 2019)
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
Now you have 2 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [E][ ] orientation (from: Oct 14 2019 to: Oct 16 2019)
Now you have 3 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [D][ ] submit form (by: Dec 02 2019 1800)
Now you have 4 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
Tasks on Oct 15 2019:
1.[D][ ] submit report (by: Oct 15 2019)
2.[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
3.[E][ ] orientation (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: invalid dates and date queries are rejected

### Aim

Verify that impossible ISO dates and malformed date queries do not add tasks or
alter the task list.

### Inputs

```text
deadline bad /by 2019-02-30
event bad /from 2019-10-15 /to 2019-99-99
event backwards /from 2020-01-02 /to 2020-01-01
deadline good /by 2020-01-01
on tomorrow
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Hmph! I can't understand that deadline date. Use yyyy-mm-dd.
____________________________________________________________
____________________________________________________________
Hmph! I can't understand that event date. Use yyyy-mm-dd.
____________________________________________________________
____________________________________________________________
Hmph! An event must end after it starts.
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [D][ ] good (by: Jan 01 2020)
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
Hmph! Enter a date in yyyy-mm-dd format.
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[D][ ] good (by: Jan 01 2020)
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: invalid commands

### Aim

Verify that missing todo descriptions and unknown commands produce friendly error messages without terminating the session.

### Inputs

```text
todo
blah
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Hmph! This is just an excuse to hang out with me, right?
Who says you get to spend empty time with me? ୧( ˵ ° ~ ° ˵ )୨
____________________________________________________________
____________________________________________________________
Hmph! Making small talk won't get you anywhere.  ʕ ꈍᴥꈍʔ
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: malformed task commands

### Aim

Verify that malformed deadline, event, and task-status commands are handled without crashing.

### Inputs

```text
deadline return book
event project meeting /from Monday
mark
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Hmph! A deadline needs a description and a /by date or time. 
 Do I HAVE to help you with everything?
____________________________________________________________
____________________________________________________________
Hmph! An event needs a description, /from date or time, and /to date or time. 
 Do I HAVE to help you with everything?
____________________________________________________________
____________________________________________________________
TOMARE!!!! Don't think you can mark tasks without doing them.
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: malformed date commands do not add tasks

### Aim

Verify that malformed deadlines and events are rejected before valid deadline and event commands are stored.

### Inputs

```text
deadline report
deadline report /by Friday
event meeting /from 2pm
event meeting /from 2pm /to 3pm
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Hmph! A deadline needs a description and a /by date or time. 
 Do I HAVE to help you with everything?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [D][ ] report (by: Friday)
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
Hmph! An event needs a description, /from date or time, and /to date or time. 
 Do I HAVE to help you with everything?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [E][ ] meeting (from: 2pm to: 3pm)
Now you have 2 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[D][ ] report (by: Friday)
2.[E][ ] meeting (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: invalid status indexes preserve task state

### Aim

Verify that invalid `mark` and `unmark` indexes do not change a valid task's completion state.

### Inputs

```text
todo read book
mark 2
mark 1
unmark 3
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] read book
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
TOMARE!!!! Don't think you can mark tasks without doing them.
____________________________________________________________
____________________________________________________________
Yatta! (ᗒᗨᗕ) I knew you could do it (✧ᴗ✧✿) 

  [X] read book
____________________________________________________________
____________________________________________________________
TOMARE!!!! Don't think you can mark tasks without doing them.
____________________________________________________________
____________________________________________________________
Gambare, Goshujin-Sama ! ˚‧º·( 。ᗒ ‸ ◕✿) 

  [ ] read book
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: delete and renumber tasks

### Aim

Verify that deleting a valid task removes it, renumbers the remaining tasks, and rejects an invalid delete index without changing the list.

### Inputs

```text
todo read book
todo return book
list
delete 1
list
delete 5
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] read book
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] return book
Now you have 2 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Goshujin-Sama, you don't want to do this with me anymore? (๑˃̣̣̥⌓˂̣̣̥)
  [T][ ] read book
Now you have 1 tasks in the list.
Not that I want to hang out with you anyway. (๑•́ ₃ •̀๑)
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][ ] return book
____________________________________________________________
____________________________________________________________
TOMARE!!!! Don't think you can mark tasks without doing them.
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: save task changes to disk

### Aim

Verify that adding and marking a task triggers the file-writing path while preserving the normal console behavior.

### Inputs

```text
todo persist me
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] persist me
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
Yatta! (ᗒᗨᗕ) I knew you could do it (✧ᴗ✧✿) 

  [X] persist me
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][X] persist me
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: find tasks by keyword

### Aim

Verify that `find` searches task descriptions case-insensitively and preserves
the original task numbers in the results.

### Setup

```text
python -c "from pathlib import Path; p=Path('data/computa.txt'); p.parent.mkdir(exist_ok=True); p.write_text('T | 1 | read book\nD | 1 | return book | June 6th\nT | 0 | buy bread\n', encoding='utf-8')"
```

### Inputs

```text
find BOOK
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Goshujin-Sama, can't you do this yourself?
Hmph. I guess I have no choice.
1.[T][X] read book
2.[D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```

## Test case: invalid and unmatched find commands

### Aim

Verify that a missing keyword is rejected and a keyword with no matches does
not display unrelated tasks or alter the session.

### Inputs

```text
find
todo read book
find cake
list
bye
```

### Expected output

```text
____________________________________________________________
                         COMPUTA
Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°
I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)
What can I do for you?
____________________________________________________________
____________________________________________________________
Hmph! Tell me what to find.
____________________________________________________________
____________________________________________________________
More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७
  [T][ ] read book
Now you have 1 tasks in the list. (⋟﹏⋞)
(.づ◡﹏◡)づ. When will we get some alone time together?
____________________________________________________________
____________________________________________________________
Goshujin-Sama, can't you do this yourself?
Hmph. I guess I have no choice.
No matching tasks found.
____________________________________________________________
____________________________________________________________
We've got so much to do (⋟﹏⋞)
I guess I'll have to spend more time with you (⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Noooo don't go!!! Hmph. Fine... Hope to see you again soon!
____________________________________________________________
```
