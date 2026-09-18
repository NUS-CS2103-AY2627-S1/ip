# Kenbot User Guide

Kenbot is a chatbot for keeping track of things you need to do. You type a
command, it answers, and it remembers everything between runs by saving your
tasks to a file.

It opens as a window, and the same commands also work in a terminal if you
prefer one.

![Kenbot](Ui.png)

## Getting started

1. Make sure you have **Java 25** installed.
2. Download `kenbot.jar`, or build it yourself with `./gradlew shadowJar`.
3. Double-click the file, or run it from a terminal:

   ```
   java -jar kenbot.jar
   ```

4. A window opens. Type a command, press Enter, and Kenbot replies.

Kenbot keeps your tasks in `data/Kenbot.txt`, created next to wherever you start
it. You never need to make that folder yourself.

### Using the window

* **Send a message** either by pressing Enter or by clicking **Send**. Both do
  the same thing.
* **Your messages appear on the right** in blue, and Kenbot's replies on the
  left in white beside its picture. The two sides look different on purpose:
  your side is an echo of what you typed, Kenbot's is the part worth reading.
* **The conversation scrolls itself**, so the newest message is always the one
  you can see.
* **Mistakes are answered, not crashes.** If a command cannot be carried out,
  Kenbot replies with what went wrong and waits for the next one. A refusal is
  shown in red with an outline, so you can tell at a glance whether a command
  worked without having to read the reply.
* **Resize the window to suit you.** The conversation grows with it, so a wider
  window shows more of each message rather than more empty space.

### Running it in a terminal instead

Every command below works identically without the window:

```
java -cp kenbot.jar kenbot.Kenbot
```

Both modes read and write the same `data/Kenbot.txt`, so you can switch between
them freely and your tasks follow you.

## Commands at a glance

Each example below shows what Kenbot says back.

| Command | What it does |
| --- | --- |
| `todo DESCRIPTION` | adds a task with no date |
| `deadline DESCRIPTION /by DATE` | adds a task due by a date |
| `event DESCRIPTION /from DATE /to DATE` | adds a task spanning two dates |
| `list` | shows every task, numbered |
| `mark NUMBER` | marks a task done |
| `unmark NUMBER` | marks a task not done |
| `delete NUMBER` | removes a task |
| `find KEYWORD` | shows tasks whose description contains the keyword |
| `find #TAG` | shows tasks carrying that tag |
| `tag NUMBER TAG…` | adds one or more tags to a task |
| `untag NUMBER TAG…` | removes one or more tags from a task |
| `bye` | exits |

### Notes about the command format

* Words in `UPPER_CASE` are the values you supply.
  e.g. in `todo DESCRIPTION`, you might type `todo read book`.
* Items in square brackets are optional.
  e.g. `todo DESCRIPTION [#TAG]…` can be `todo read book #fun`, or just
  `todo read book`.
* Items with `…` after them can be given more than once, including zero times.
  e.g. `[#TAG]…` can be left out, or given as `#fun`, or as `#fun #cs2103`.
* Commands are lower case. `Todo` and `LIST` are not recognised.
* Extra words after a command that takes none, such as `list now`, are ignored.

## How to write dates

Dates go in as `yyyy-mm-dd`, and you may add a time after a space:

```
2019-10-15
2019-10-15 1800
```

Kenbot shows them back in a friendlier form: `Oct 15 2019` and
`Oct 15 2019 1800`.

Anything that is not a real date is refused, so `Sunday` and `15-10-2019` will
both be rejected with a reminder of the accepted format.

## Adding a to-do: `todo`

A task with nothing but a description.

Format: `todo DESCRIPTION [#TAG]…`

Example: `todo read book`

```
Got it, that's on the list:
  [T][ ] read book
That makes 1 task.
```

## Adding a deadline: `deadline`

A task that has to be finished by a particular date.

Format: `deadline DESCRIPTION /by DATE [#TAG]…`

Example: `deadline return book /by 2019-10-15`

```
Got it, that's on the list:
  [D][ ] return book (by: Oct 15 2019)
That makes 2 tasks.
```

## Adding an event: `event`

A task that runs from one date to another. Times are optional on either end.

Format: `event DESCRIPTION /from DATE /to DATE [#TAG]…`

Example: `event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600`

```
Got it, that's on the list:
  [E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
That makes 3 tasks.
```

## Listing your tasks: `list`

Example: `list`

Format: `list`

```
Here's what you've got:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
```

The letter in the first brackets is the kind of task: `T` for to-do, `D` for
deadline, `E` for event. The second brackets hold an `X` once the task is done.

An empty list says so rather than showing nothing:

```
Nothing on the list yet.
```

## Marking a task done, or not done: `mark`, `unmark`

Use the number shown by `list`.

Format: `mark NUMBER` or `unmark NUMBER`

Example: `mark 1`

```
Nice, that's done:
  [T][X] read book
```

Example: `unmark 1`

```
Alright, back on the list:
  [T][ ] read book
```

## Deleting a task: `delete`

Example: `delete 2`

Format: `delete NUMBER`

```
Gone:
  [D][ ] return book (by: Oct 15 2019)
That makes 2 tasks.
```

The remaining tasks are renumbered straight away, so `list` always runs from 1
with no gaps.

## Tagging a task: `tag`, `untag`

A tag is a short label such as `#fun` or `#cs2103`. A task can carry any number
of them, and they are shown at the end of its line.

Format: `tag NUMBER TAG…` or `untag NUMBER TAG…`

You can also put tags straight on a new task, as `[#TAG]…` in the three
commands above.

The quickest way is to put them at the end when you create the task:

Example: `todo read book #fun #cs2103`

```
Got it, that's on the list:
  [T][ ] read book #fun #cs2103
That makes 1 task.
```

This works for deadlines and events too. Put the tags after the dates:
`deadline return book /by 2019-10-15 #urgent`.

Only `#` words at the **end** of the line become tags. A `#` with ordinary words
after it stays in the description, so `todo read #1 book` gives you a task
described as "read #1 book" with no tags.

To tag a task you have already added, use `tag` with its number. The `#` is
optional here, and you can give several at once:

Example: `tag 1 fun cs2103`

```
Tagged:
  [T][ ] read book #fun #cs2103
```

`untag` takes them back off again:

Example: `untag 1 fun`

```
Untagged:
  [T][ ] read book #cs2103
```

A tag is one word with no spaces, and cannot contain `|`. Capitals count:
`#Fun` and `#fun` are two different tags.

## Finding tasks: `find`

Searches the descriptions. Upper and lower case are ignored, and part of a word
counts, so `find book` also finds "bookshop".

Format: `find KEYWORD` to search descriptions, or `find #TAG` to search tags

Start the keyword with `#` to search **tags** instead: `find #cs` finds anything
tagged `#cs2103`. The `#` is what chooses which one is searched, so `find fun`
will not find a task tagged `#fun`. You need `find #fun`.

Example: `find book`

```
Found these:
1.[T][X] read book
2.[D][ ] return book (by: Oct 15 2019)
```

If nothing matches, Kenbot says so:

```
Nothing matches 'xyz'.
```

## Leaving: `bye`

Example: `bye`

Format: `bye`

```
Peace! See you soon!
```

In a terminal this ends the program. In the window it is only a goodbye. Close
the window itself when you are finished.

Either way your tasks are already saved: every command writes the file
immediately, so nothing is lost even if you close Kenbot without typing `bye`.

## Things worth knowing

* **Commands are lower case.** `Todo` and `MARK` are not recognised.
* **Descriptions cannot contain `|`.** Kenbot uses that character to separate
  fields in its save file, so a task containing one is refused when you type it.
* **The numbers `find` shows start from 1** and are not the numbers to use with
  `mark` or `delete`. Run `list` first if you need the real number.
* **An event cannot end before it starts.** If both ends fall on the same day
  the times decide it, so `1600` to `1400` is refused. A whole day, with no
  times given at all, is fine.
* **The same task twice is refused.** Adding an exact copy of something already
  on the list is almost always a double keypress, and two identical lines are
  impossible to tell apart when you later mark or delete one. Capitals count, so
  `read book` and `Read book` are two different tasks.
* **Each of `/by`, `/from` and `/to` may be given once.** Writing one twice used
  to have the second quietly swallowed into the date; it is now reported.
* **Tags are optional everywhere.** A task without any is stored and shown
  exactly as it was before tags existed, so a save file from an older Kenbot
  still opens and nothing needs converting.
* **A damaged save file does not lose everything.** If Kenbot cannot understand
  some lines, it loads the ones it can and tells you how many it skipped. In a
  terminal it says so as it starts; the window leaves the count in the terminal
  it was launched from.
* **The window can be resized**, down to 417 by 220 pixels and up to whatever
  your screen allows.
* **Long descriptions wrap** onto as many lines as they need, so nothing is cut
  off.
