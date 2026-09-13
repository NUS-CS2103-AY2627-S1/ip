# Odysseus User Guide

Odysseus is a personal-assistant chatbot that keeps a voyage log of your
tasks and Athena's Archive of study cards. It works through a conversation:
type a command, then press Enter.

## Before you start

Run the application with JDK 25. From the project root, launch the desktop
conversation window with:

```powershell
.\gradlew.bat run
```

Alternatively, build the application JAR with `./gradlew.bat shadowJar`; the
result is `build\libs\odysseus.jar`.

## Command format

* Commands and their keywords are lowercase.
* Text in `<angle brackets>` is information you supply. Do not type the
  brackets.
* Text in `[square brackets]` is optional.
* Task and card numbers begin at 1 and are shown by `list` and `cards`.

Odysseus explains how to correct a malformed command and leaves your existing
tasks and cards unchanged.

## Manage tasks

### Add a to-do

Use `todo` for something without a date or time.

```text
todo read book
```

### Add a deadline

Use `deadline` for work due on a date. Dates must use `yyyy-MM-dd`; a time may
follow the date.

```text
deadline submit reflection /by 2026-10-01
deadline call Penelope /by 2026-10-01 18:00
```

### Add an event

Use `event` for an activity with a start and end. Odysseus stores the start and
end text as you enter it.

```text
event project meeting /from Mon 2pm /to 4pm
```

### View and find tasks

Use `list` to show every task, including its number and completion state. Use
`find` to show tasks whose descriptions contain a keyword.

```text
list
find book
```

### Mark, unmark, or delete a task

Use the task number shown by `list`.

```text
mark 2
unmark 2
delete 2
```

`mark` records a completed task, `unmark` makes it outstanding again, and
`delete` permanently removes it from the voyage log.

## Study with Athena's Archive

Athena's Archive keeps question-and-answer cards in a separate local file, so
they remain available after you close Odysseus.

### Create and browse cards

Create a card with a prompt and answer. Give it a topic with `/topic`, or omit
that part to use the default `Odyssey` topic. Use `cards` to see every card or
only cards in one topic.

```text
learn nostos /answer homecoming /topic Greek terms
cards
cards Greek terms
```

### Review a card

Start a review with `review`, optionally limited to a topic. Odysseus selects a
card with the lowest mastery first, then the fewest reviews.

```text
review
review Greek terms
```

Answer directly to check your recall:

```text
answer homecoming
```

A correct answer increases mastery. An incorrect answer resets it to zero.
Answers are compared without regard to letter case or surrounding whitespace.

If you prefer to self-assess, reveal the answer and then record the result:

```text
reveal
mastered
again
```

`mastered` increases the active card's mastery; `again` resets it to zero for
another review. These commands require a card selected by `review`.

### Remove a card

Use the number shown by `cards` to permanently remove a card.

```text
forget 2
```

## End the conversation

Type `bye` to close the current Odysseus conversation.

```text
bye
```
