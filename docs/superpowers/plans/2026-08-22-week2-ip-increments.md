# Week 2 iP Increments Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete and publish HABI's remaining Week 2 increments: typed tasks, inheritance, error handling, exceptions, deletion, collections, and enums.

**Architecture:** Evolve the current console app in the course-prescribed order. Keep command coordination in `Habi`, model tasks with a `Task` base class and `Todo`, `Deadline`, and `Event` subclasses, store them polymorphically in `ArrayList<Task>`, and introduce checked `HabiException` handling before adding deletion.

**Tech Stack:** Java 25.0.3 Zulu, Java standard library, the repository's Markdown-driven UI runner, Git lightweight tags, GitHub remote `origin`.

---

## File Map

- Modify `src/main/java/Habi.java`: command loop, parsing, validation, responses, and task-list mutations.
- Modify `src/main/java/Task.java`: shared task state and display format.
- Create `src/main/java/Todo.java`: todo task subtype.
- Create `src/main/java/Deadline.java`: deadline task subtype and `/by` value.
- Create `src/main/java/Event.java`: event task subtype and `/from`/`/to` values.
- Create `src/main/java/HabiException.java`: checked exception for invalid user commands.
- Create `src/main/java/TaskType.java`: enum and one-letter type icons.
- Modify `src/test/java/HabiTest.java`: focused model and end-to-end assertions.
- Modify `test/ui-test-plan.md`: exact console regression scenarios.

## Task 1: Prepare the required Java 25 runtime

**Files:**
- No repository files changed.
- Temporary runtime: `/private/tmp/habi-jdk25`

- [ ] **Step 1: Confirm the current runtime mismatch**

```bash
java -version
/usr/libexec/java_home -V
```

Expected: only Java 21 is listed; Java 25 is not installed system-wide.

- [ ] **Step 2: Download Java 25.0.3 without installing it system-wide**

Request network approval, then run:

```bash
curl -fL https://cdn.azul.com/zulu/bin/zulu25.34.17-ca-fx-jdk25.0.3-macosx_aarch64.tar.gz -o /private/tmp/habi-jdk25.tar.gz
mkdir -p /private/tmp/habi-jdk25
tar -xzf /private/tmp/habi-jdk25.tar.gz --strip-components=1 -C /private/tmp/habi-jdk25
```

Expected: `/private/tmp/habi-jdk25/Contents/Home/bin/java` and `javac` exist.

- [ ] **Step 3: Verify Java 25 and the Level-3 baseline**

```bash
/private/tmp/habi-jdk25/Contents/Home/bin/java -version
/private/tmp/habi-jdk25/Contents/Home/bin/javac -version
env PATH="/private/tmp/habi-jdk25/Contents/Home/bin:/usr/bin:/bin:/usr/sbin:/sbin" python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Expected: version `25.0.3`; the current UI case passes without a non-25 warning.

## Task 2: Implement Level-4 typed tasks

**Files:**
- Modify: `test/ui-test-plan.md`
- Modify: `src/test/java/HabiTest.java`
- Modify: `src/main/java/Task.java`
- Modify: `src/main/java/Habi.java`

- [ ] **Step 1: Replace the UI case with a typed-task scenario**

Use this exact input:

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 2
list
unmark 2
list
bye
```

The full expected block must retain the existing banner and dividers while
containing these exact task responses:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

- [ ] **Step 2: Update `HabiTest` for typed input**

Use:

```java
String input = "todo read book\n"
        + "deadline return book /by Sunday\n"
        + "event project meeting /from Mon 2pm /to 4pm\n"
        + "mark 2\nunmark 2\nlist\nbye\n";
```

Add:

```java
assertContains(output, "[T][ ] read book", "HABI should display a todo.");
assertContains(output, "[D][ ] return book (by: Sunday)",
        "HABI should display a deadline.");
assertContains(output, "[E][ ] project meeting (from: Mon 2pm to: 4pm)",
        "HABI should display an event.");
```

- [ ] **Step 3: Run tests and verify RED**

```bash
env PATH="/private/tmp/habi-jdk25/Contents/Home/bin:/usr/bin:/bin:/usr/sbin:/sbin" python3 .codex/skills/test-ui/scripts/run_ui_tests.py
mkdir -p /private/tmp/habi-test-classes
/private/tmp/habi-jdk25/Contents/Home/bin/javac -d /private/tmp/habi-test-classes src/main/java/*.java src/test/java/HabiTest.java
/private/tmp/habi-jdk25/Contents/Home/bin/java -cp /private/tmp/habi-test-classes HabiTest
```

Expected: failure because the current app lacks typed-task parsing and output.

- [ ] **Step 4: Extend `Task`**

Add `typeIcon` and `timingDetails`, retain the existing status methods, keep
`Task(String description)` as a compatibility constructor, and add:

```java
public Task(String typeIcon, String description, String timingDetails) {
    this.typeIcon = typeIcon;
    this.description = description;
    this.timingDetails = timingDetails;
    this.isDone = false;
}

@Override
public String toString() {
    return "[" + typeIcon + "][" + getStatusIcon() + "] "
            + description + timingDetails;
}
```

- [ ] **Step 5: Parse valid typed commands in `Habi`**

Create tasks with these exact mappings:

```java
Task todo = new Task("T", command.substring(5).trim(), "");

String deadlineArguments = command.substring(9).trim();
int byIndex = deadlineArguments.indexOf(" /by ");
Task deadline = new Task("D",
        deadlineArguments.substring(0, byIndex).trim(),
        " (by: " + deadlineArguments.substring(byIndex + 5).trim() + ")");

String eventArguments = command.substring(6).trim();
int fromIndex = eventArguments.indexOf(" /from ");
int toIndex = eventArguments.indexOf(" /to ", fromIndex + 7);
Task event = new Task("E",
        eventArguments.substring(0, fromIndex).trim(),
        " (from: " + eventArguments.substring(fromIndex + 7, toIndex).trim()
                + " to: " + eventArguments.substring(toIndex + 5).trim() + ")");
```

Print additions with:

```java
System.out.println("Got it. I've added this task:");
System.out.println("  " + task);
System.out.println("Now you have " + tasks.size() + " "
        + (tasks.size() == 1 ? "task" : "tasks") + " in the list.");
```

Print `task` directly in list, mark, and unmark responses.

- [ ] **Step 6: Run the full Java 25 tests and verify GREEN**

Repeat Step 3. Expected: UI runner reports all cases passed; compilation and
`HabiTest` exit 0.

- [ ] **Step 7: Commit, tag, and push**

```bash
git add src/main/java/Habi.java src/main/java/Task.java src/test/java/HabiTest.java test/ui-test-plan.md
git commit -m "Add typed todo, deadline, and event tasks" -m "Implement the Level-4 command formats and preserve status operations across all task types."
git tag Level-4
git push origin master Level-4
```

## Task 3: Refactor to A-Inheritance

**Files:**
- Modify: `src/test/java/HabiTest.java`
- Modify: `src/main/java/Task.java`
- Modify: `src/main/java/Habi.java`
- Create: `src/main/java/Todo.java`
- Create: `src/main/java/Deadline.java`
- Create: `src/main/java/Event.java`

- [ ] **Step 1: Add failing subtype tests**

```java
private static void verifyTaskSubtypes() {
    assertEquals("[T][ ] read book", new Todo("read book").toString(),
            "Todo should use the T icon.");
    assertEquals("[D][ ] return book (by: Sunday)",
            new Deadline("return book", "Sunday").toString(),
            "Deadline should display its due value.");
    assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)",
            new Event("project meeting", "Mon 2pm", "4pm").toString(),
            "Event should display its start and end values.");
}
```

Call it from `main`, compile, and expect missing-class errors.

- [ ] **Step 2: Create the subclasses with Javadocs**

```java
public class Todo extends Task {
    public Todo(String description) {
        super("T", description);
    }
}
```

```java
public class Deadline extends Task {
    private final String by;

    public Deadline(String description, String by) {
        super("D", description);
        this.by = by;
    }

    @Override
    protected String getTimingDetails() {
        return " (by: " + by + ")";
    }
}
```

```java
public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super("E", description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTimingDetails() {
        return " (from: " + from + " to: " + to + ")";
    }
}
```

- [ ] **Step 3: Refactor `Task` and `Habi`**

`Task` keeps `typeIcon` but removes stored timing text. Add:

```java
protected Task(String typeIcon, String description) {
    this.typeIcon = typeIcon;
    this.description = description;
    this.isDone = false;
}

public Task(String description) {
    this("T", description);
}

protected String getTimingDetails() {
    return "";
}

@Override
public String toString() {
    return "[" + typeIcon + "][" + getStatusIcon() + "] "
            + description + getTimingDetails();
}
```

Construct `Todo`, `Deadline`, and `Event` in `Habi`, while retaining
`ArrayList<Task>`.

- [ ] **Step 4: Run UI and Java tests**

Expected: all tests pass with unchanged output.

- [ ] **Step 5: Commit, tag, and push**

```bash
git add src/main/java/Habi.java src/main/java/Task.java src/main/java/Todo.java src/main/java/Deadline.java src/main/java/Event.java src/test/java/HabiTest.java
git commit -m "Model task types with inheritance" -m "Use Todo, Deadline, and Event subclasses while storing them polymorphically as Task objects."
git tag A-Inheritance
git push origin master A-Inheritance
```

## Task 4: Implement Level-5 validation

**Files:**
- Modify: `test/ui-test-plan.md`
- Modify: `src/test/java/HabiTest.java`
- Modify: `src/main/java/Habi.java`

- [ ] **Step 1: Add a failing interleaved error case**

Use this input, including the initial blank line:

```text

todo
deadline return book
event project meeting /from Mon 2pm
mark two
mark 1
todo read book
mark 2
unmark 0
blah
list
bye
```

Expect, in order:

```text
OOPS! Please enter a command.
OOPS! The todo description cannot be empty.
OOPS! Use: deadline DESCRIPTION /by DATE_OR_TIME
OOPS! Use: event DESCRIPTION /from START /to END
OOPS! The task number must be a whole number.
OOPS! Task number 1 is out of range.
OOPS! Task number 2 is out of range.
OOPS! Task number 0 is out of range.
OOPS! I don't know what "blah" means.
```

The final list must contain only `1.[T][ ] read book`. Add matching
`HabiTest` assertions for the unknown-command message and preserved list.

- [ ] **Step 2: Run tests and verify RED**

Expected: current parsing throws or adds invalid text instead of reporting the
specified errors.

- [ ] **Step 3: Add direct validation in `Habi`**

Use this index helper:

```java
private static Integer parseTaskIndexOrPrintError(
        String command, String keyword, int taskCount) {
    String argument = command.substring(keyword.length()).trim();
    if (argument.isEmpty()) {
        printResponse("OOPS! Please provide a task number for " + keyword + ".");
        return null;
    }
    try {
        int taskNumber = Integer.parseInt(argument);
        if (taskNumber < 1 || taskNumber > taskCount) {
            printResponse("OOPS! Task number " + taskNumber + " is out of range.");
            return null;
        }
        return taskNumber - 1;
    } catch (NumberFormatException exception) {
        printResponse("OOPS! The task number must be a whole number.");
        return null;
    }
}
```

Use `printResponse(String message)` to enclose every message with the existing
divider. Validate every description and delimiter before mutation. Replace the
fallback with:

```java
printResponse("OOPS! I don't know what \"" + command + "\" means.");
```

- [ ] **Step 4: Run the Java 25 suite and verify GREEN**

Expected: both UI cases and `HabiTest` pass with no stack trace.

- [ ] **Step 5: Commit, tag, and push**

```bash
git add src/main/java/Habi.java src/test/java/HabiTest.java test/ui-test-plan.md
git commit -m "Handle invalid task commands safely" -m "Validate descriptions, delimiters, task numbers, and unknown commands without changing task-list state."
git tag Level-5
git push origin master Level-5
```

## Task 5: Refactor validation to A-Exceptions

**Files:**
- Create: `src/main/java/HabiException.java`
- Modify: `src/main/java/Habi.java`
- Modify: `src/test/java/HabiTest.java`

- [ ] **Step 1: Add a failing checked-exception test**

```java
private static void verifyHabiExceptionIsChecked() {
    assertTrue(Exception.class.isAssignableFrom(HabiException.class),
            "HabiException should be an exception.");
    assertTrue(!RuntimeException.class.isAssignableFrom(HabiException.class),
            "HabiException should be checked.");
}

private static void assertTrue(boolean condition, String message) {
    if (!condition) {
        throw new AssertionError(message);
    }
}
```

Call it from `main`, compile, and expect a missing-class error.

- [ ] **Step 2: Create the checked exception**

```java
/** Represents a recoverable error in a command entered for HABI. */
public class HabiException extends Exception {
    /** Creates an exception with a user-facing explanation. */
    public HabiException(String message) {
        super(message);
    }
}
```

- [ ] **Step 3: Refactor without changing output**

Make parsing helpers throw `HabiException` instead of printing and returning
sentinels. Catch only at the command-loop boundary:

```java
try {
    handleCommand(command, tasks);
} catch (HabiException exception) {
    printResponse(exception.getMessage());
}
```

- [ ] **Step 4: Run UI and Java tests**

Expected: all cases pass with byte-for-byte identical output.

- [ ] **Step 5: Commit, tag, and push**

```bash
git add src/main/java/Habi.java src/main/java/HabiException.java src/test/java/HabiTest.java
git commit -m "Represent command errors with HabiException" -m "Centralize recoverable validation failures at the command-loop boundary without changing user-visible behavior."
git tag A-Exceptions
git push origin master A-Exceptions
```

## Task 6: Implement Level-6 and record A-Collections

**Files:**
- Modify: `test/ui-test-plan.md`
- Modify: `src/test/java/HabiTest.java`
- Modify: `src/main/java/Habi.java`

- [ ] **Step 1: Add a failing deletion case**

Use:

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon /to Tue
delete 2
delete 5
list
bye
```

Require:

```text
Noted. I've removed this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
OOPS! Task number 5 is out of range.
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon to: Tue)
```

Add matching `HabiTest` assertions. Run tests and expect `delete` to be
reported as unknown.

- [ ] **Step 2: Implement deletion**

```java
if (command.equals("delete") || command.startsWith("delete ")) {
    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
    Task removedTask = tasks.remove(taskIndex);
    printResponse("Noted. I've removed this task:\n  " + removedTask
            + "\nNow you have " + tasks.size() + " "
            + (tasks.size() == 1 ? "task" : "tasks") + " in the list.");
    return;
}
```

- [ ] **Step 3: Run the Java 25 suite and verify GREEN**

Expected: all three UI cases and `HabiTest` pass.

- [ ] **Step 4: Commit, tag twice, and push**

```bash
git add src/main/java/Habi.java src/test/java/HabiTest.java test/ui-test-plan.md
git commit -m "Add task deletion with collection-backed storage" -m "Remove tasks by validated index and retain ArrayList polymorphism for dynamic task management."
git tag Level-6
git tag A-Collections
git push origin master Level-6 A-Collections
```

## Task 7: Implement A-Enums

**Files:**
- Create: `src/main/java/TaskType.java`
- Modify: `src/main/java/Task.java`
- Modify: `src/main/java/Todo.java`
- Modify: `src/main/java/Deadline.java`
- Modify: `src/main/java/Event.java`
- Modify: `src/test/java/HabiTest.java`

- [ ] **Step 1: Add a failing enum test**

```java
private static void verifyTaskTypes() {
    assertEquals("T", TaskType.TODO.getIcon(), "Todo icon should be T.");
    assertEquals("D", TaskType.DEADLINE.getIcon(), "Deadline icon should be D.");
    assertEquals("E", TaskType.EVENT.getIcon(), "Event icon should be E.");
}
```

Call it from `main`, compile, and expect a missing-class error.

- [ ] **Step 2: Create `TaskType`**

```java
/** Identifies the supported kinds of tasks and their display icons. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /** Returns the one-letter icon displayed for this task type. */
    public String getIcon() {
        return icon;
    }
}
```

- [ ] **Step 3: Replace raw type strings**

Change `Task` to store `TaskType type` and use `type.getIcon()` in
`toString()`. Its constructors become:

```java
protected Task(TaskType type, String description) {
    this.type = type;
    this.description = description;
    this.isDone = false;
}

public Task(String description) {
    this(TaskType.TODO, description);
}
```

Update subtype constructors to pass:

```java
super(TaskType.TODO, description);
super(TaskType.DEADLINE, description);
super(TaskType.EVENT, description);
```

- [ ] **Step 4: Run Java 25 UI and Java tests**

Expected: all output remains unchanged and all tests pass.

- [ ] **Step 5: Commit, tag, and push**

```bash
git add src/main/java/TaskType.java src/main/java/Task.java src/main/java/Todo.java src/main/java/Deadline.java src/main/java/Event.java src/test/java/HabiTest.java
git commit -m "Represent task kinds with an enum" -m "Replace raw task-type strings with a TaskType enum that owns each display icon."
git tag A-Enums
git push origin master A-Enums
```

## Task 8: Final verification

**Files:**
- Verify all repository files; no planned modifications.

- [ ] **Step 1: Run fresh Java 25 verification**

```bash
env PATH="/private/tmp/habi-jdk25/Contents/Home/bin:/usr/bin:/bin:/usr/sbin:/sbin" python3 .codex/skills/test-ui/scripts/run_ui_tests.py
mkdir -p /private/tmp/habi-test-classes
/private/tmp/habi-jdk25/Contents/Home/bin/javac -Xlint:all -d /private/tmp/habi-test-classes src/main/java/*.java src/test/java/HabiTest.java
/private/tmp/habi-jdk25/Contents/Home/bin/java -cp /private/tmp/habi-test-classes HabiTest
```

Expected: all UI cases pass; compilation and the Java harness exit 0.

- [ ] **Step 2: Check hygiene and local history**

```bash
git diff --check
git status --short --branch
git ls-files '*.class'
git log --oneline --decorate --graph -20
```

Expected: clean `master...origin/master`, no tracked `.class` files, and each
increment tag on its intended commit.

- [ ] **Step 3: Verify remote refs**

```bash
git ls-remote --heads --tags origin
```

Expected tags: `Level-0`, `Level-1`, `Level-2`, `Level-3`, `Level-4`,
`A-Inheritance`, `Level-5`, `A-Exceptions`, `Level-6`, `A-Collections`, and
`A-Enums`.

- [ ] **Step 4: Report evidence**

Report the Java 25 version, UI test count, commit/tag mapping, clean status,
and GitHub tags URL. Do not claim completion if any command above failed.
