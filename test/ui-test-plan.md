# UI Test Plan

Text-based UI tests for the Elsa chatbot. Each test case below feeds a list of
commands to the program on standard input and compares the whole console output
against the expected output recorded here.

Run the tests with:

```
python .claude/skills/test-ui/scripts/run-ui-tests.py
```

## How the program is run

The runner builds the program once with Gradle, using this command:

```build
gradlew shadowJar
```

and then starts it once per test case with this command, both from the
repository root:

```run
java -ea -cp build/libs/elsa.jar elsa.Elsa
```

The tests therefore run the same artifact the project ships. The jar's manifest
names `elsa.gui.Launcher`, which opens the window, so these tests cannot use
`java -jar`: a window reads nothing from standard input and prints nothing to
the console. `-cp` puts the same jar on the classpath and then names the class
to start, which reaches the text interface inside the shipped artifact. Testing
the jar rather than the compiled classes still catches a jar that was built
without a class or a resource it needs at runtime.

`-ea` switches on the `assert` statements, which Java otherwise ignores. These
cases are the only ones that run the whole program rather than one method, so
they are where an assumption broken between two classes would show up. The
shipped jar is started without the flag, because an assertion checks this
code's own reasoning and is not a failure a user should be handed.

The build runs once, before the first case, so no case can test a stale jar.
The runner picks `gradlew.bat` on Windows and `./gradlew` elsewhere, so the
command written above stays the same on every platform.

The chatbot saves its tasks to `./data/elsa.txt` under the repository root and
reads them back at startup, so the runner puts that file into a known state
before every case:

- a case with a ```` ```data ```` block starts with exactly those lines saved;
- a case without one starts with no data file at all, as on a first ever run.

Cases are therefore independent of each other and of anything left behind by an
earlier run. Saving itself is still not checked directly, because it prints
nothing to the console; loading is, because what was loaded shows up in `list`.

## How outputs are compared

- The **whole** console output is compared, including the banner and greeting.
- Trailing whitespace on each line is ignored, as is any blank line at the very
  end of the output. This matters because the banner lines end in spaces that an
  editor may trim from this file.
- Everything else must match exactly, including leading indentation.

Because the greeting and the border style appear in every expected output,
changing either of them means updating every test case below. The runner prints
a line-by-line difference on failure, which shows exactly what to change.

## How to add a test case

Add a `###` heading, an `**Aim:**` line, an ` ```input ` block, and an
` ```expected ` block. Cases run top to bottom and the session stops at the
first failure.

A case may also carry an ` ```data ` block, placed before the input block. Its
lines are written to `./data/elsa.txt` before the program starts, which is how a
case sets up tasks that an earlier run is meant to have saved.

---

### TC-1 - Greet and exit immediately

**Aim:** Check that the chatbot greets the user on startup and exits on `bye`, with no tasks involved.

```input
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-2 - List when nothing has been added

**Aim:** Check that `list` on an empty task list reports the empty-list message instead of printing an empty block.

```input
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Into the Unknown.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-3 - Reject an unknown command and an empty todo description

**Aim:** Check the two errors from the Level-5 examples are reported with the OLAF!!! prefix and say how to correct the input, and that rejecting an input leaves stored tasks untouched.

```input
todo borrow book
todo
blah
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] borrow book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The description of a todo cannot be empty. Use: todo <description>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! That one's lost in the blizzard. Type "help" and I'll show you what I can do.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] borrow book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-4 - Mark a task as done

**Aim:** Check that `mark 2` reports the task it changed and that only that task shows `[X]` in a later `list`, confirming the 1-based number maps to the right task.

```input
todo read book
todo return book
mark 2
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] return book
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] return book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[T][X] return book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-5 - Unmark a task that was marked done

**Aim:** Check that `unmark` reverses a previous `mark`, returning the task to `[ ]` rather than leaving it done or removing it.

```input
todo read book
mark 1
unmark 1
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Back into the cold. I've marked this task as not done yet:
       [T][ ] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-6 - Add todos and list them

**Aim:** Check that `todo` adds a task marked `[T]`, confirms it with the running task count, and that the type marker survives being marked done.

```input
todo borrow book
todo read book
mark 2
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] borrow book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-7 - Add a deadline with a date

**Aim:** Level-8 requires the text after `/by` to be understood as a date rather than kept as text, so this case checks both halves of that. A date typed as `2019-10-20` must be accepted and then shown in the reading format, `Oct 20 2019`, which is only possible if it was understood rather than copied. Text that is not a date must be refused with the form to use, where before Level-8 it would have been stored as it stood. The closing `list` shows the rejected line added nothing.

```input
todo borrow book
deadline return book /by 2019-10-20
deadline do homework /by no idea
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] borrow book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Oct 20 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! "no idea" is not a date. Write it as 2019-10-15, 15/10/2019 (day/month/year) or Oct 15 2019. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Oct 20 2019) -- overdue
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-8 - Add events and list all three task types together

**Aim:** Check that `event` splits the description, start date, and end date apart on `/from` and `/to`, displays the task as `[E]`, and that todos, deadlines, and events coexist in one list and can each be marked done.

```input
todo borrow book
deadline return book /by 2019-10-20
event project meeting /from 2019-10-21 /to 2019-10-22
mark 3
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] borrow book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Oct 20 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] project meeting (from: Oct 21 2019 to: Oct 22 2019)
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [E][X] project meeting (from: Oct 21 2019 to: Oct 22 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Oct 20 2019) -- overdue
     3.[E][X] project meeting (from: Oct 21 2019 to: Oct 22 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-9 - Reject empty deadline and event descriptions between valid adds

**Aim:** Check that each kind of task names itself in the error, with the correct article, and that a rejected add does not consume a slot or shift the numbering of the tasks after it.

```input
deadline
deadline return book /by 2019-10-20
event
event project meeting /from 2019-10-21 /to 2019-10-22
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The description of a deadline cannot be empty. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Oct 20 2019) -- overdue
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The description of an event cannot be empty. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] project meeting (from: Oct 21 2019 to: Oct 22 2019)
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[D][ ] return book (by: Oct 20 2019) -- overdue
     2.[E][ ] project meeting (from: Oct 21 2019 to: Oct 22 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-10 - Handle a blank line, surrounding spaces, and the wrong letter case

**Aim:** Check that an empty line is reported rather than silently ignored, that spaces around a command and its description are trimmed away, and that commands are case sensitive so `BYE` does not end the session.

```input

   todo   read book   
BYE
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! You didn't type anything. Try "todo <description>", or "list" to see what you have.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! That one's lost in the blizzard. Type "help" and I'll show you what I can do.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-11 - Reject task numbers that are missing, not numbers, or out of range

**Aim:** Check that each way of getting a task number wrong is reported differently rather than ending the session, and that a valid mark still works afterwards, showing the rejected commands changed nothing.

```input
mark 1
todo read book
mark abc
mark 99
mark 0
unmark
mark 1
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! There are no tasks yet, so there is nothing to mark. Add one with "todo <description>" first.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! "abc" is not a task number. Use a whole number, for example: mark 2.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! There is no task 99. You have 1 task, so use a number from 1 to 1.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! There is no task 0. You have 1 task, so use a number from 1 to 1.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! Which task? Use: unmark <task number>, for example: unmark 2.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-12 - Reject deadlines and events with missing or empty parts

**Aim:** Check that a missing separator, an empty description and an empty time are each reported differently with the correct usage, and that valid adds in between still number correctly.

```input
deadline x
deadline /by 2019-10-20
deadline homework /by
deadline submit report /by 2019-10-20
event meeting /from Mon
event project meeting /from 2019-10-21 /to 2019-10-22
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! I could not find "/by" in that. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The description of a deadline cannot be empty. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The due date after /by cannot be empty. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] submit report (by: Oct 20 2019) -- overdue
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! I could not find "/to" in that. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] project meeting (from: Oct 21 2019 to: Oct 22 2019)
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[D][ ] submit report (by: Oct 20 2019) -- overdue
     2.[E][ ] project meeting (from: Oct 21 2019 to: Oct 22 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-13 - Reject events with a missing separator or an empty part

**Aim:** Check all four ways an event's arguments can be wrong are reported separately, mirroring the deadline coverage in TC-12, since event parsing splits twice and a fault in either split would otherwise go unnoticed. The valid add at the end confirms the four rejections left the list empty rather than half filled.

```input
event meeting /to 4pm
event /from Mon /to 4pm
event meeting /from /to 4pm
event meeting /from 2019-10-21 /to
event standup /from 2019-10-21 /to 2019-10-22
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! I could not find "/from" in that. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The description of an event cannot be empty. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The start date after /from cannot be empty. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The end date after /to cannot be empty. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] standup (from: Oct 21 2019 to: Oct 22 2019)
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[E][ ] standup (from: Oct 21 2019 to: Oct 22 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-14 - Delete a task from the middle of the list

**Aim:** Check that `delete` reports the task it removed and the remaining count, and that the tasks after it are renumbered. The `mark 3` afterwards targets the task that moved up into position 3, proving the renumbering applies to later commands and is not just how `list` happens to print.

```input
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
delete 3
mark 3
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Jun 06 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Melted away. I've removed this task:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] join sports club
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2019) -- overdue
     3.[T][X] join sports club
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-15 - Reject bad delete numbers, then delete the last task

**Aim:** Check that `delete` rejects a missing, non-numeric and out-of-range number the same way `mark` does, that the rejections leave the task in place, and that deleting the only task empties the list rather than leaving a stale entry behind.

```input
delete 1
todo read book
delete abc
delete 99
delete
delete 1
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! There are no tasks yet, so there is nothing to delete. Add one with "todo <description>" first.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! "abc" is not a task number. Use a whole number, for example: delete 2.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! There is no task 99. You have 1 task, so use a number from 1 to 1.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! Which task? Use: delete <task number>, for example: delete 2.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Melted away. I've removed this task:
       [T][ ] read book
     Now you have 0 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Into the Unknown.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-16 - Change the list in every way that triggers a save

**Aim:** Saving to `./data/elsa.txt` happens whenever the task list changes, and it must be invisible from the console: it may not print anything of its own, and it may not stop a command from finishing. This case runs every command that changes the list -- the three adds, `mark`, `unmark` and `delete` -- so a save that printed a stray line, or failed and turned a confirmation into an `OLAF!!!` error, would show up as a difference here. The closing `list` confirms the six changes left the task list in the state the saved file should mirror.

```input
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
mark 1
unmark 1
delete 2
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Jun 06 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Back into the cold. I've marked this task as not done yet:
       [T][ ] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Melted away. I've removed this task:
       [D][ ] return book (by: Jun 06 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-17 - Start with tasks saved by an earlier run

**Aim:** Level-7 requires the tasks to be read back from the hard disk at startup. This case starts with a data file holding one of each kind of task, so `list` must show all three with the descriptions, dates and done markers they were saved with, rather than the empty-list message. The `delete 2` and `unmark 1` afterwards check that loaded tasks are ordinary members of the list: they can be numbered, changed and removed exactly like tasks typed in this session. The saved deadline is from 2019 and not done, so it is overdue, and the chatbot reminds the user of it straight after the greeting; TC-31 checks that reminder more closely.

```data
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 | 2019-08-07
```

```input
list
delete 2
unmark 1
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here is what needs your attention:
       2.[D][ ] return book (by: Jun 06 2019) -- overdue
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019) -- overdue
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Melted away. I've removed this task:
       [D][ ] return book (by: Jun 06 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Back into the cold. I've marked this task as not done yet:
       [T][ ] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-18 - Keep the good tasks when some lines cannot be understood

**Aim:** A hand-edited data file can go wrong in several ways at once, and one bad line must not cost the user the tasks on every other line. This file has a done marker that is neither 1 nor 0, a deadline with no due time, an unknown type letter, and a blank line. The three faulty lines must each be reported with their line number and what is wrong, the blank line must pass without comment, and the two sound tasks must load and be usable. The warning has to say that saving rewrites the file, because the `todo` at the end does exactly that and the skipped lines are then gone.

```data
T | 1 | read book
T | 7 | bad marker
D | 0 | return book

X | 0 | mystery
T | 0 | buy milk
```

```input
list
todo water plants
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! I could not understand 3 lines of data/elsa.txt, so I have left them out:
       Line 2: "7" is not a done marker; it should be 1 or 0
       Line 3: a D task needs 4 fields, but this line has 3
       Line 5: "X" is not a task type; it should be T, D or E
     Your other tasks loaded normally. Saving will rewrite the file without the lines above, so edit the file now if you want to keep them.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][ ] buy milk
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] water plants
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-19 - Refuse text that would break the saved file apart

**Aim:** Tasks are stored with their fields separated by a bar with a space on each side, so a description or a time containing that text would be read back as extra fields and the task would return changed or not at all. The chatbot must refuse it when it is typed, where the user can still fix it, rather than accepting it and losing part of the task at the next startup. Every part the user supplies is checked, not only descriptions, so a time is tried here too. The valid `todo` at the end shows the four rejections changed nothing.

```input
todo a | b
deadline a | b /by 2019-10-20
deadline return book /by Sun | day
event project meeting /from 2pm | 3pm /to 4pm
todo read book
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The description of a todo cannot contain "|" with a space on each side, because that is how the parts of a stored task are separated. Use: todo <description>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The description of a deadline cannot contain "|" with a space on each side, because that is how the parts of a stored task are separated. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The due date after /by cannot contain "|" with a space on each side, because that is how the parts of a stored task are separated. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! The start date after /from cannot contain "|" with a space on each side, because that is how the parts of a stored task are separated. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-20 - Separate a command from its argument with a tab

**Aim:** A user who types or pastes a tab instead of a space has still named a command and given it an argument, so the chatbot should read it the same way rather than rejecting the whole line as an unknown command. Note for anyone editing this file: the two input lines below contain real tab characters, and replacing them with spaces would leave the case passing without testing anything.

```input
todo	read book
mark	1
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-21 - Reject dates in the data file that are not dates

**Aim:** The data file stores dates in the same form the user types them, so a file written before Level-8, or edited by hand, can hold text where a date belongs. Line 2 holds the free text a pre-Level-8 file would contain, and line 3 holds a date that is written correctly but does not exist, since February has no 30th. Both must be reported with the same wording the chatbot uses when the text is typed, and the sound lines above and below them must still load, so that one outdated line does not cost the user the rest of the file.

```data
D | 1 | return book | 2019-06-06
D | 0 | old style | June 6th
D | 0 | impossible | 2019-02-30
E | 0 | project meeting | 2019-08-06 | 2019-08-07
```

```input
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! I could not understand 2 lines of data/elsa.txt, so I have left them out:
       Line 2: "June 6th" is not a date. Write it as 2019-10-15, 15/10/2019 (day/month/year) or Oct 15 2019
       Line 3: "2019-02-30" is not a date. Write it as 2019-10-15, 15/10/2019 (day/month/year) or Oct 15 2019
     Your other tasks loaded normally. Saving will rewrite the file without the lines above, so edit the file now if you want to keep them.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[D][X] return book (by: Jun 06 2019)
     2.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-22 - Accept a date written in any of the accepted forms

**Aim:** A date may be typed in several forms, and all of them have to mean the same day once read. The first three lines write 15 October 2019 three different ways, including one in lower case, and each must come back as the single reading form `Oct 15 2019`, which can only happen if each was understood rather than copied. The fourth line is the example from the Level-8 statement: `2/12/2019` must be 2 December, not 12 February, so the day is read before the month. The last line asks for a day February does not have; it must be refused rather than quietly moved to the 28th, since a date silently changed is worse than one rejected. The closing `list` shows the four accepted dates side by side and the rejected one absent.

```input
deadline year first /by 2019-10-15
deadline slashes /by 15/10/2019
deadline month name /by oct 15 2019
deadline day before month /by 2/12/2019
deadline no such day /by 31/2/2019
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] year first (by: Oct 15 2019) -- overdue
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] slashes (by: Oct 15 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] month name (by: Oct 15 2019) -- overdue
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] day before month (by: Dec 02 2019) -- overdue
     Now you have 4 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! "31/2/2019" is not a date. Write it as 2019-10-15, 15/10/2019 (day/month/year) or Oct 15 2019. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[D][ ] year first (by: Oct 15 2019) -- overdue
     2.[D][ ] slashes (by: Oct 15 2019) -- overdue
     3.[D][ ] month name (by: Oct 15 2019) -- overdue
     4.[D][ ] day before month (by: Dec 02 2019) -- overdue
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-23 - List the tasks falling on one date

**Aim:** `on` answers a different question from `list`, and each kind of task answers it differently: a deadline falls on the one day it is due, an event on every day it runs including its first and last, and a todo on no day at all. The three queries here check all of that: Oct 15 finds the deadline and the running event, Oct 14 and Oct 16 find the event on its boundary days, where an exclusive comparison would wrongly leave it out, and Oct 20 finds nothing and must say so rather than print an empty heading. The todo never appears. The numbers shown are the ones the tasks have in the full list, 2 and 3 rather than 1 and 2, so that a number read here can be given straight to `mark` or `delete`; renumbering from 1 would make those commands act on the wrong task. The last two lines check that a missing date and an unreadable one are each reported.

```input
todo borrow book
deadline return book /by 2019-10-15
event conference /from 2019-10-14 /to 2019-10-16
on 2019-10-15
on 2019-10-14
on 2019-10-16
on 2019-10-20
on
on rubbish
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] borrow book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Oct 15 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks on Oct 15 2019:
     2.[D][ ] return book (by: Oct 15 2019) -- overdue
     3.[E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks on Oct 14 2019:
     3.[E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks on Oct 16 2019:
     3.[E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Nothing on Oct 20 2019.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! Which date? Use: on <date>, for example: on 2019-10-15.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! "rubbish" is not a date. Write it as 2019-10-15, 15/10/2019 (day/month/year) or Oct 15 2019. Use: on <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-24 - Mark a deadline overdue only when it is still owed

**Aim:** A deadline whose date has passed and which is not done is overdue, and nothing else is. The four tasks here cover each way that can go: a past deadline carries the note, a future one does not, a past one that has been marked done loses the note the moment it is marked, and an event never carries it whatever its dates, since an event that has finished is not a debt. Note the dates chosen: 2019 is in the past and 2999 in the future whenever these tests are run, so a case that depends on today's date still gives the same output every day. A date near today would make this case start failing on its own.

```input
deadline past deadline /by 2019-10-15
deadline future deadline /by 2999-01-01
deadline finished deadline /by 2019-10-15
mark 3
event past event /from 2019-10-14 /to 2019-10-16
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] past deadline (by: Oct 15 2019) -- overdue
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] future deadline (by: Jan 01 2999)
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] finished deadline (by: Oct 15 2019) -- overdue
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [D][X] finished deadline (by: Oct 15 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] past event (from: Oct 14 2019 to: Oct 16 2019)
     Now you have 4 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[D][ ] past deadline (by: Oct 15 2019) -- overdue
     2.[D][ ] future deadline (by: Jan 01 2999)
     3.[D][X] finished deadline (by: Oct 15 2019)
     4.[E][ ] past event (from: Oct 14 2019 to: Oct 16 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-25 - Find tasks by a keyword in the description

**Aim:** Check that `find` shows every task whose description contains the keyword and leaves out the ones that do not, and that each task keeps the number it has in the full list rather than being renumbered from 1. Keeping the number is what lets a number read here be given straight to `mark` or `delete`. One matching task is marked done first, so the result is shown to carry the task's real state rather than a fresh copy.

```input
todo read book
deadline return book /by 2999-06-06
todo buy milk
mark 1
find book
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Jun 06 2999)
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] buy milk
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the matching tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2999)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-26 - Find ignoring the case of the keyword

**Aim:** Check that the search ignores case, so a task written in capitals is found by a keyword typed in lower case. Someone looking for a task they wrote themselves should not have to remember how they capitalised it. The matching task is the second one, so this also shows a result numbered 2 rather than renumbered to 1.

```input
todo read book
todo buy MILK
find milk
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] buy MILK
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the matching tasks in your list:
     2.[T][ ] buy MILK
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-27 - Reject a find with nothing to look for, and report finding nothing

**Aim:** Check the two ways `find` produces no list: a keyword that matches nothing is reported as such rather than as an empty list, and a `find` with no keyword at all is refused with the usage, as every other command with a missing argument is. The two are told apart so the user knows whether they mistyped the keyword or forgot it.

```input
todo read book
find zebra
find
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Nothing matching "zebra".
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! What should I look for? Use: find <keyword>..., for example: find book.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-28 - List the commands with help

**Aim:** Check that `help` names every command the chatbot understands, and says how a date may be written. This is what the window's greeting sends a new user to, so a command missing here is one nobody is told about. Running `help` before and after adding a task also shows it reads rather than changes: the task list is the same afterwards, and the answer does not depend on what is in it.

```input
help
todo read book
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here is what you can ask me:

     Adding a task
       todo <description>
       deadline <description> /by <date>
       event <description> /from <date> /to <date>

     Seeing what you have
       list
       on <date>
       remind
       find <keyword>...

     Changing a task
       mark <task number>
       unmark <task number>
       delete <task number>

     Anything else
       help
       bye

     Write a date as 2019-10-15, 15/10/2019 (day/month/year) or Oct 15 2019.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-29 - Find by several keywords at once

**Aim:** Check that the words after `find` are separate keywords rather than one phrase, and that a task matching any one of them is shown. `find book milk` must return two tasks that have no word in common, which a phrase search could never do. The numbers shown are the ones the tasks have in the full list, 1 and 3, so a number read here can be given straight to `mark` or `delete`. The last two lines check the complaint when nothing matches: it names every keyword that was asked about, joined as English, so the user can see which of them were tried.

```input
todo read book
todo walk the dog
todo buy milk
find book milk
find zebra unicorn
find zebra unicorn dragon
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] walk the dog
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] buy milk
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the matching tasks in your list:
     1.[T][ ] read book
     3.[T][ ] buy milk
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Nothing matching "zebra" or "unicorn".
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Nothing matching "zebra", "unicorn" or "dragon".
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-30 - Remind about unfinished overdue deadlines and nothing else

**Aim:** B-Reminders asks for a way to be reminded about deadlines. `remind` shows the unfinished deadlines that are overdue or due within the next 7 days, and nothing else. The first `remind`, on an empty list, checks that having nothing to show is said in words rather than as an empty heading. The list then holds one task of every kind that must be left out: a todo, a 2019 deadline that is marked done, a 2019 event, and a deadline in 2999, which is never within a week. Only the two unfinished 2019 deadlines remain, and they are shown by due date rather than list order, so task 6 comes before task 2, each under its number in the full list so that it can be given straight to `mark` or `delete`. The last `remind 3` checks that text after the keyword is ignored, as it is after `list`. Deadlines due today, tomorrow or later this week cannot be tested here, because this plan has no fixed today; ElsaTest and TaskListTest cover those.

```input
remind
todo read book
deadline return book /by 2019-06-06
deadline pay fees /by 2019-06-01
mark 3
event camp /from 2019-06-01 /to 2019-06-03
deadline renew pass /by 2999-01-01
deadline old form /by 2019-05-30
remind
remind 3
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Nothing is overdue, and nothing is due in the next 7 days.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] return book (by: Jun 06 2019) -- overdue
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] pay fees (by: Jun 01 2019) -- overdue
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [D][X] pay fees (by: Jun 01 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)
     Now you have 4 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] renew pass (by: Jan 01 2999)
     Now you have 5 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] old form (by: May 30 2019) -- overdue
     Now you have 6 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here is what needs your attention:
       6.[D][ ] old form (by: May 30 2019) -- overdue
       2.[D][ ] return book (by: Jun 06 2019) -- overdue
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here is what needs your attention:
       6.[D][ ] old form (by: May 30 2019) -- overdue
       2.[D][ ] return book (by: Jun 06 2019) -- overdue
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-31 - Remind at startup, after the saved-file warning

**Aim:** A reminder the user has to ask for is easy to forget, so the chatbot also reminds them as the session starts, without being asked. The saved file here holds an unfinished 2019 deadline, which must be reminded about; a finished 2019 deadline, a todo and a 2999 deadline, which must not; and one unreadable line. The order of the opening blocks is the point: greeting, then the warning about the unreadable line, then the reminder, so the user hears that the file was damaged before being reminded of tasks read from it. The reminder shows task 2 under its number in the full list, because the unreadable line is skipped before the list is numbered. `list` afterwards confirms the numbering and that the reminder changed nothing.

```data
T | 0 | read book
D | 0 | return book | 2019-06-06
D | 1 | pay fees | 2019-06-01
X | 0 | mystery
D | 0 | renew pass | 2999-01-01
```

```input
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! I could not understand 1 line of data/elsa.txt, so I have left it out:
       Line 4: "X" is not a task type; it should be T, D or E
     Your other tasks loaded normally. Saving will rewrite the file without the line above, so edit the file now if you want to keep it.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here is what needs your attention:
       2.[D][ ] return book (by: Jun 06 2019) -- overdue
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2019) -- overdue
     3.[D][X] pay fees (by: Jun 01 2019)
     4.[D][ ] renew pass (by: Jan 01 2999)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-32 - Reject an event that ends before it starts

**Aim:** Check that an event whose end date falls before its start date is refused, and that the complaint is about the pair of dates rather than about either date on its own, which would send the user looking for a misspelling that is not there. The one-day event that follows checks the boundary: equal dates describe an event lasting a single day, which is ordinary and must still be accepted.

```input
event trip /from 2019-10-16 /to 2019-10-14
event trip /from 2019-10-15 /to 2019-10-15
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! An event cannot end before it starts, but you gave Oct 16 2019 to Oct 14 2019. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [E][ ] trip (from: Oct 15 2019 to: Oct 15 2019)
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[E][ ] trip (from: Oct 15 2019 to: Oct 15 2019)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-33 - Reject a parameter given more than once

**Aim:** Check that repeating `/by`, `/from` or `/to` is named as the mistake it is. Without this check the second copy is swallowed into the piece after it, which is then read as a date and fails, so the user is told their date is wrong when the date is fine. The valid deadline at the end confirms the three rejections stored nothing. Its due date is far in the future so that the overdue note does not appear and the case stays about the repeated parameter.

```input
deadline report /by Monday /by 2999-01-01
event meeting /from 2019-10-14 /from 2019-10-15 /to 2019-10-16
event meeting /from 2019-10-14 /to 2019-10-15 /to 2019-10-16
deadline report /by 2999-01-01
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! You gave "/by" more than once, and I don't know which one you meant. Use it just once. Use: deadline <description> /by <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! You gave "/from" more than once, and I don't know which one you meant. Use it just once. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! You gave "/to" more than once, and I don't know which one you meant. Use it just once. Use: event <description> /from <date> /to <date>
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] report (by: Jan 01 2999)
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[D][ ] report (by: Jan 01 2999)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

---

### TC-34 - Refuse to add a task that is already in the list

**Aim:** Check that the same task typed twice is refused rather than stored twice, and that the check weighs the right things. A deadline with the same wording but a different date is a different task and must be accepted. A task already marked done is still a duplicate, since repeating something already ticked off is as much a slip as repeating an unfinished one, and the refusal must leave the done marker alone. The two deadlines are due far in the future so that the overdue note does not appear.

```input
todo read book
todo read book
deadline report /by 2999-01-01
deadline report /by 2999-01-02
mark 1
todo read book
list
bye
```

```expected
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
      _____ _
     |  ___| |___  __ _
     | |__ | / __|/ _` |
     |  __|| \__ \ (_| |
     |_____|_|___/\__,_|
     Hello! I'm Elsa.
     Do you want to build a snowman?
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [T][ ] read book
     Now you have 1 task in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! You already have that one, so I didn't add it twice. Use "list" to see it.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] report (by: Jan 01 2999)
     Now you have 2 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Frozen in place. I've added this task:
       [D][ ] report (by: Jan 02 2999)
     Now you have 3 tasks in the list.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Let it go! I've marked this task as done:
       [T][X] read book
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     OLAF!!! You already have that one, so I didn't add it twice. Use "list" to see it.
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] report (by: Jan 01 2999)
     3.[D][ ] report (by: Jan 02 2999)
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     The cold never bother me anyways!
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```
