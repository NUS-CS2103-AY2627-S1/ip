# Billy User Guide

![Billy](Ui.png)

Billy is a chatbot that keeps track of your tasks and has rather more to say
about them than it strictly needs to. It announces, it congratulates, and it
will comment on the state of your list if you let it get long. The one time
Billy drops the act is when something has gone wrong: errors are worded plainly,
because an error is not the place for a personality.

Tell it what you need to do and it remembers, including between runs: your list
is saved to `data/billy.txt` after every change and read back the next time
Billy starts.

Billy handles three kinds of task — **todos**, **deadlines** and **events** —
and can list them, search them, mark them done and delete them.

## Quick start

1. Check you have **Java 25** installed: run `java -version` in a terminal.
2. Download the latest `billy.jar` from the
   [releases page](https://github.com/aayanvatsa04/ip/releases).
3. Put it in a folder of its own. Billy saves your tasks beside the jar, in
   `data/billy.txt`, and creates that folder itself the first time.
4. Run it with `java -jar billy.jar`.
5. Type a command into the box at the bottom and press Enter. Try `help` to see
   everything Billy understands, or go straight to `todo read book`.

Java prints a few lines beginning `WARNING: A restricted method...` as Billy
starts. Nothing is wrong: JavaFX has to load the graphics libraries your
operating system draws windows with, and Java 24 onwards mentions it whenever
that happens. The window opens normally and you can ignore them. To start
without them:

```
java --enable-native-access=javafx.graphics -jar billy.jar
```

## Quick reference

| What you want | Type |
| --- | --- |
| Add a task with no date | `todo <description>` |
| Add a task with a due date | `deadline <description> /by <date>` |
| Add a task spanning a period | `event <description> /from <date> /to <date>` |
| See everything | `list` |
| See one day | `on <date>` |
| Search descriptions | `find <word>` |
| Mark done / not done | `mark <number>` / `unmark <number>` |
| Remove a task | `delete <number>` |
| See every command | `help` |
| Leave | `bye` |

Commands are not case sensitive, so `LIST` works as well as `list`.

## Writing dates

A date is written as `yyyy-MM-dd` or `d/M/yyyy`, so the 2nd of December 2019 is
either `2019-12-02` or `2/12/2019`. The day comes first in the slash form.

A time of day may follow, as four digits on the 24-hour clock: `2019-12-02 1800`
is six in the evening. Leaving the time out is allowed and means Billy makes no
claim about the hour, rather than assuming midnight.

Billy shows dates back to you as `Dec 2 2019`, or `Dec 2 2019, 6:00pm` when a
time was given.

## Adding a todo

A task with nothing attached to it.

Example: `todo read book`

```
Consider it written down:
  [T][ ] read book
That's 1 task on the books.
```

## Adding a deadline

A task that must be done by a particular date or time.

Example: `deadline return book /by 2019-12-02 1800`

```
Consider it written down:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
That's 2 tasks on the books.
```

## Adding an event

A task that runs from one date or time until another. An event that would end
before it starts is refused.

Example: `event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600`

```
Consider it written down:
  [E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
That's 3 tasks on the books.
```

## Listing every task

Shows the whole list, numbered the way you refer to the tasks.

Example: `list`

```
Behold, your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 2 2019, 6:00pm)
3.[E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
```

An empty list says so rather than showing a heading with nothing under it:

```
Nothing. Nada. An empty list. Suspicious.
```

## Seeing one day

Shows the tasks falling on a particular day. A deadline falls on its due date,
and an event covers every day it runs across, both ends included. Todos carry no
date and so never appear here.

Example: `on 2019-12-02`

```
Here's what Dec 2 2019 has in store:
2.[D][ ] return book (by: Dec 2 2019, 6:00pm)
3.[E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
```

If nothing falls on that day:

```
Nothing on Dec 5 2019. Enjoy the day off!
```

## Searching

Finds the tasks whose description mentions a word. Capitalization is ignored,
and part of a word counts, so `find book` also finds `textbook`.

Example: `find book`

```
Found these lurking in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 2 2019, 6:00pm)
```

Note that the numbers are the ones from the full list, so a task found this way
can be marked or deleted straight away without running `list` first.

If nothing matches, the word is quoted back so a typo is easy to spot:

```
Searched high and low. Nothing mentions 'zzz'.
```

## Marking a task done, or not done again

Example: `mark 1`

```
BOOM. Done and dusted:
  [T][X] read book
```

Example: `unmark 1`

```
Un-done! We've all been there:
  [T][ ] read book
```

## Deleting a task

Example: `delete 2`

```
Gone. Vanished. No trace:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
That's 2 tasks on the books.
```

## Leaving

Example: `bye`

```
Off you go! I'll be right here, guarding the list. Vigilantly.
```

## Asking what Billy understands

`help` lists every command, grouped by what it does, with the shorter words in
brackets.

Example: `help`

```
Everything I know how to do. Short forms in brackets, for the impatient.
Adding: todo (t), deadline (d, dl), event (e, ev)
Seeing: list (l, ls), on, find (f)
Changing: mark (m), unmark (um), delete (del, rm)
Other: help (h, ?), bye (exit, quit, q)
```

The listing is built from the commands themselves, so it always matches what
Billy actually accepts.

## Shorter ways to type a command

Every command answers to a shorter word as well as its full name, so a long
session need not be spelled out in full. `t read book` adds a todo, and `rm 2`
deletes the second task.

| Command | Also answers to |
| --- | --- |
| `todo` | `t` |
| `deadline` | `d`, `dl` |
| `event` | `e`, `ev` |
| `list` | `l`, `ls` |
| `on` | — |
| `find` | `f` |
| `mark` | `m` |
| `unmark` | `um` |
| `delete` | `del`, `rm` |
| `help` | `h`, `?` |
| `bye` | `exit`, `quit`, `q` |

These are read the same way as the full keywords, so `LS` works as well as `ls`.
`on` has none, being short already.

## Things Billy will say unprompted

Billy reacts to the state of your list as well as confirming what you asked for:

* Finishing the last task you had outstanding earns a line of its own.
* Deleting your last remaining task is announced as an empty list, rather than
  reported as a count of zero.
* Once the list reaches ten tasks, Billy has a word to say about that too.

Adding a task you already have is allowed, but pointed out:

```
Consider it written down:
  [T][ ] read book
That's 2 tasks on the books.
Heads up: that's the same as task 1. Type 'delete 2' if you didn't mean it.
```

## Using the window

Billy is a window, not a terminal. Type into the box along the bottom and press
Enter, or click **Send**.

What you type appears on the right in a coloured bubble; Billy answers on the
left. The two are shown differently on purpose, because the conversation is not
between equals: your commands are short, while Billy's answers can run to a list
of everything you have to do.

When something goes wrong, the reply is drawn as a red card with a warning sign
and a bar down its left edge, so a mistyped command is hard to scroll past. Only
failures look like that.

Drag any edge to resize. The text rewraps to fit, down to a narrow strip you can
leave open beside your work. Typing `bye` closes the window a moment later, and
closing the window yourself does no harm — your list is already saved.

## If something goes wrong

Billy explains rather than crashes, and the conversation carries on. Each of
these is answered with what to type instead:

* A command Billy does not know, or one with a part missing.
* A task number that names nothing, including `0` and negative numbers.
* A date it cannot read, including dates that do not exist such as `2019-02-30`.
* An event that ends before it starts, or that starts and ends at the same
  stated time. An all-day event is written with no times at all, as in
  `event conference /from 2019-12-02 /to 2019-12-02`, and is perfectly ordinary.
* The same marker given twice, such as two `/by` in one command.
* A description containing `|`. Billy separates the parts of a saved task with
  that character, so a description containing one could not be read back
  correctly. Everything else — slashes, percent signs, brackets — is fine.

If you add a task that matches one already on your list, Billy adds it anyway
and says which task it matches and which number to delete if you did not mean
it. Tasks you have already finished are not counted, so ticking off a chore and
adding it again is never questioned.

If the save file is damaged, Billy loads what it can, skips the lines it cannot
understand, and tells you how many it skipped, rather than losing the rest of
your list. If the file cannot be written at all, Billy says so and keeps the
change for the rest of the session.
