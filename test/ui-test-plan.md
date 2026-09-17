# Quu — Ui Test Plan

Test cases for the `test-ui` skill. The runner reads this file, starts the
program once per test case, feeds each command in order, and compares the
output that follows each command against the expected output recorded here.

## Session block format

Each test case has one fenced ` ```session ` block.

- A line beginning with `> ` is **input** typed at the prompt.
- Every other line is **expected output** for the input immediately above it.
- Lines appearing *before* the first `> ` are the expected **startup** output.
  Omit them to skip checking the banner and greeting.

## Comparison rules

Output is normalised on both sides before comparing, so decoration does not
affect the result:

1. Leading and trailing whitespace is stripped from every line.
2. Blank lines are ignored.
3. Divider lines — 10 or more consecutive `_` or `-` characters — are ignored.
4. An expected line of exactly `...` matches zero or more output lines.

Everything else must match exactly, including spacing *inside* a line. So
`[T][ ] read book` and `[T] [ ] read book` are different, and the test fails.

Rules 1–3 exist because Level 0 states the horizontal lines are optional; they
are presentation, not behaviour. Rule 4 covers the banner, which Level 0 also
makes optional and whose content is the author's choice.

## Source of expected outputs

Expected outputs are taken from the iP specification's sample sessions
(Levels 0–4). Where the specification does not define a message — malformed
input, out-of-range indices — the test case is marked **project convention**
and records the message this project has chosen. Those may be changed freely;
the spec-derived ones may not — except where a later level supersedes an
earlier one, as Level 8 does to the free-text dates of Level 4. Such cases say
so in their **Source**.

## Running

```
java test/TestRunner.java
```

Exits `0` if every case passes, `1` on the first failure.

---

## TC-01 — Greet the user and exit

**Aim:** On startup the program greets the user, and `bye` prints the farewell and terminates.

**Source:** iP spec, Level 0.

```session
...
Hello! I'm Quu.
What can I do for you?
...
> bye
Bye. Hope to see you again soon!
```

---

## TC-02 — Add a todo

**Aim:** `todo` creates a ToDo, confirms with the `[T]` type icon and an unticked status box, and reports the new task count.

**Source:** iP spec, Level 4.

```session
> todo borrow book
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
```

---

## TC-03 — Add a deadline

**Aim:** `deadline … /by …` splits the description from the due date and renders it as `[D]` with the date in parentheses.

**Source:** iP spec, Level 4, updated for the Level 8 date format (`yyyy-mm-dd` in, `MMM d yyyy` out).

```session
> deadline return book /by 2026-06-06
Got it. I've added this task:
[D][ ] return book (by: Jun 6 2026)
Now you have 1 tasks in the list.
```

---

## TC-04 — Add an event

**Aim:** `event … /from … /to …` splits the description, start and end, and renders it as `[E]` with both times in one bracketed clause.

**Source:** iP spec, Level 4, updated for the Level 8 date format (`yyyy-mm-dd` in, `MMM d yyyy` out).

```session
> event project meeting /from 2026-08-06 /to 2026-08-08
Got it. I've added this task:
[E][ ] project meeting (from: Aug 6 2026 to: Aug 8 2026)
Now you have 1 tasks in the list.
```

---

## TC-05 — List tasks in insertion order

**Aim:** `list` numbers tasks from 1 in the order they were added, with each task's type and status icons. Guards against the ordering being decided by the storage structure rather than insertion.

**Source:** iP spec, Level 4, updated for the Level 8 date format (`yyyy-mm-dd` in, `MMM d yyyy` out).

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> deadline return book /by 2026-06-06
Got it. I've added this task:
[D][ ] return book (by: Jun 6 2026)
Now you have 2 tasks in the list.
> event project meeting /from 2026-08-06 /to 2026-08-08
Got it. I've added this task:
[E][ ] project meeting (from: Aug 6 2026 to: Aug 8 2026)
Now you have 3 tasks in the list.
> list
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2026)
3.[E][ ] project meeting (from: Aug 6 2026 to: Aug 8 2026)
```

---

## TC-06 — Mark a task as done

**Aim:** `mark N` sets the status of the Nth listed task and echoes it with `[X]`.

**Source:** iP spec, Level 3.

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> mark 1
Nice! I've marked this task as done:
[T][X] read book
> list
Here are the tasks in your list:
1.[T][X] read book
```

---

## TC-07 — Unmark a task

**Aim:** `unmark N` clears the status and reports it with the *not done* wording, distinct from the `mark` message.

**Source:** iP spec, Level 3.

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> mark 1
Nice! I've marked this task as done:
[T][X] read book
> unmark 1
OK, I've marked this task as not done yet:
[T][ ] read book
```

---

## TC-08 — Numbering survives non-adding commands

**Aim:** Commands that add nothing — `list`, an unknown word — must not consume a task number. Guards against the counter tracking inputs rather than tasks.

**Source:** iP spec, Level 4 (implied by `mark N` addressing listed positions).

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> list
Here are the tasks in your list:
1.[T][ ] read book
> todo return book
Got it. I've added this task:
[T][ ] return book
Now you have 2 tasks in the list.
> list
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
```

---

## TC-09 — Mark an out-of-range index

**Aim:** An index with no matching task raises `TaskNotFoundException`, reported to the user rather than crashing.

**Source:** project convention — not specified by the iP.

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> mark 99
There's no task at 99 use list to check available tasks.
```

---

## TC-10 — Mark a non-numeric index

**Aim:** A non-numeric argument raises `InvalidIndexException`, quoting back what the user typed.

**Source:** project convention — not specified by the iP.

```session
> mark abc
"abc" isn't a task number
```

---

## TC-11 — Mark with no argument

**Aim:** A bare `mark` reports the expected format instead of crashing. Regression guard for the `parts[1]` out-of-bounds access in the switch.

**Source:** project convention — not specified by the iP.

```session
> mark
Invalid format. Please follow this format: mark <task number>
```

---

## TC-12 — Unmark with no argument

**Aim:** A bare `unmark` reports the expected format instead of crashing. Companion to TC-11; the two commands share the same argument handling and have drifted apart before.

**Source:** project convention — not specified by the iP.

```session
> unmark
Invalid format. Please follow this format: unmark <task number>
```

---

## TC-13 — Unrecognised command

**Aim:** Input that is not a known command raises `UnknownCommandException` rather than being stored as a task.

**Source:** project convention — not specified by the iP.

```session
> blah
I don't know what "blah" does
```

---

## TC-14 — Todo with no description

**Aim:** A bare `todo` raises `MissingArgumentException` showing the expected format.

**Source:** project convention — not specified by the iP.

```session
> todo
Invalid format. Please follow this format: todo <task>
```

---

## TC-15 — Malformed deadline

**Aim:** A `deadline` missing its `/by` clause raises `MissingArgumentException` showing the expected format.

**Source:** project convention — not specified by the iP.

```session
> deadline return book
Invalid format. Please follow this format: deadline <task> /by <yyyy-mm-dd>
```

---

## TC-16 — Malformed event

**Aim:** An `event` missing its `/to` clause raises `MissingArgumentException` showing the expected format.

**Source:** project convention — not specified by the iP.

```session
> event project meeting /from Mon 2pm
Invalid format. Please follow this format: event <task> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
```

---

## TC-17 — An error does not consume a task number

**Aim:** A failed command must not advance the task counter. Guards against the counter tracking inputs rather than successful additions, now that errors take a different path through the switch.

**Source:** iP spec, Level 4 (implied by `mark N` addressing listed positions).

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> blah
I don't know what "blah" does
> mark 99
There's no task at 99 use list to check available tasks.
> todo return book
Got it. I've added this task:
[T][ ] return book
Now you have 2 tasks in the list.
> list
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
```

---

## TC-18 — Delete a task

**Aim:** `delete N` removes the Nth task, reports the correct remaining count, and renumbers the tasks after it.

**Source:** iP spec, Level 6.

```session
> todo a
Got it. I've added this task:
[T][ ] a
Now you have 1 tasks in the list.
> todo b
Got it. I've added this task:
[T][ ] b
Now you have 2 tasks in the list.
> todo c
Got it. I've added this task:
[T][ ] c
Now you have 3 tasks in the list.
> delete 2
Noted. I've removed this task:
[T][ ] b
Now you have 2 tasks in the list.
> list
Here are the tasks in your list:
1.[T][ ] a
2.[T][ ] c
```

---

## TC-19 — Delete with a bad argument

**Aim:** `delete` rejects an out-of-range index, a non-numeric index and a missing argument, using the same messages as `mark`. Guards against the three commands' shared argument handling drifting apart.

**Source:** project convention — not specified by the iP.

```session
> todo read book
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
> delete 99
There's no task at 99 use list to check available tasks.
> delete abc
"abc" isn't a task number
> delete
Invalid format. Please follow this format: delete <task number>
> list
Here are the tasks in your list:
1.[T][ ] read book
```
