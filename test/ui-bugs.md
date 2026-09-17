# UI Bugs

The following bugs were found during exploratory testing. Keep them as
regression-test candidates until the corresponding defects are fixed.

These failures were reproduced with Java 25.0.3.

## Large task numbers crash Panda

**Severity:** High — a single valid-looking command terminates the application.

All three commands accept an arbitrary sequence of digits, but values greater
than `Integer.MAX_VALUE` cause an unhandled `NumberFormatException` instead of
the normal usage error. The application exits before it can process any later
commands.

### Command

```sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test Panda
```

### Inputs

Run Panda separately for each task-number command because every session crashes
on its first command:

```text
mark 2147483648
bye
```

```text
unmark 2147483648
bye
```

```text
delete 2147483648
bye
```

### Expected behaviour after the fix

Each oversized number should display the same usage error as another invalid
task number, keep Panda running, and allow `bye` to exit normally. No Java
stack trace should be printed.

### Actual behaviour

The first input terminates Panda with:

```text
Exception in thread "main" java.lang.NumberFormatException: For input string: "2147483648"
```

## End of input causes an uncaught exception

**Severity:** Medium — running Panda with redirected or closed standard input
ends with an error rather than a clean exit.

### Command

```sh
javac -d out/ui-test $(find src/main/java -name '*.java') && printf 'list\n' | java -cp out/ui-test Panda
```

### Expected behaviour after the fix

After displaying the list, Panda should exit cleanly when no more input is
available, without a stack trace.

### Actual behaviour

Panda displays the empty list, then terminates with:

```text
Exception in thread "main" java.util.NoSuchElementException: No line found
```

## Event parser accepts malformed or reversed markers

**Resolution:** Covered by parser validation and event-range validation. Retain the commands below as regression cases.

**Severity:** Medium — malformed input creates an event with misleading start
and end values.

### Command

```sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test Panda
```

### Inputs

```text
event meeting /from 4pm /from 5pm
event meeting /to 5pm /from 4pm
list
bye
```

### Expected behaviour after the fix

Both event commands should be rejected with the documented usage error:
`event DESCRIPTION /from START /to END`. The list should remain empty.

### Actual behaviour

Both malformed commands are accepted. The first creates an event despite having
no `/to` marker, and the second reverses the entered values:

```text
[E][ ] meeting (from: 4pm to: 5pm)
[E][ ] meeting (from: 5pm to: 4pm)
```

## Task descriptions can inject terminal control sequences

**Severity:** Medium — crafted task text can clear or reposition a user's
terminal and make application output misleading.

Panda stores and echoes raw control characters in task descriptions. This test
uses the ANSI “clear screen” and “move cursor home” sequences.

### Command

```sh
javac -d out/ui-test $(find src/main/java -name '*.java') && printf 'todo safe\033[2J\033[Hforged\nlist\nbye\n' | java -cp out/ui-test Panda
```

### Expected behaviour after the fix

Panda should reject control characters or display an escaped, harmless form of
them. Task text must not be able to control the terminal.

### Actual behaviour

Panda accepts the task and writes the escape bytes back to the terminal in both
the add confirmation and task list. In a compatible terminal, `ESC[2J` clears
the screen and `ESC[H` moves the cursor to the top-left corner.

## An invisible description is accepted as a task

**Severity:** Low — a task can appear to have no description even though the
parser's ordinary empty-description check passes.

### Command

The octal bytes below encode a zero-width space (`U+200B`):

```sh
javac -d out/ui-test $(find src/main/java -name '*.java') && printf 'todo \342\200\213\nlist\nbye\n' | java -cp out/ui-test Panda
```

### Expected behaviour after the fix

Panda should reject a description that contains no visible text with the normal
`todo DESCRIPTION` usage error, and the task list should remain empty.

### Actual behaviour

Panda reports that the task was added and lists a visually blank task:

```text
1.[T][ ] [a zero-width space is present here but cannot be seen]
```

## A one-item list uses a plural task count

**Severity:** Low — add and delete confirmations contain a grammatical error.

### Command

```sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test Panda
```

### Inputs

```text
todo first
todo second
delete 1
bye
```

### Expected behaviour after the fix

Both confirmations produced when the list size is one should say:

```text
Now you have 1 task in the list.
```

### Actual behaviour

The first add and the deletion back to one item both say:

```text
Now you have 1 tasks in the list.
```
