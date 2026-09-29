# Emma

Emma is a command-line chatbot that keeps track of your tasks. You talk to her in
a terminal, and she remembers your tasks between runs by saving them to a file.

```
 _____
| ____|  _ __ ___   _ __ ___    __ _
|  _|   | '_ ` _ \  | '_ ` _ \   / _` |
| |___  | | | | | | | | | | | | | (_| |
|_____| |_| |_| |_| |_| |_| |_|  \__,_|

Emma
Hey there! I'm Emma.
What can I do for you?

user
todo read book

Emma
Got it, I've added this:
  [T][ ] read book
```

## Requirements

- **JDK 25.** The code uses switch expressions with `->` arms, so an older JDK
  will not compile it.
- **Nothing else.** JavaFX does not need installing separately: Gradle fetches it
  as a dependency, just as the wrapper committed here fetches Gradle itself.

## Building and running

Use `./gradlew` in Git Bash or `.\gradlew.bat` in PowerShell. The first command
downloads Gradle and JavaFX and takes a minute; later ones are quick.

```
./gradlew run
```

That opens Emma's window: the conversation scrolls above a text box, and you send
a command with the **Send** button or by pressing Enter. A command Emma cannot
carry out is answered in a red, outlined bubble, so a mistake is hard to miss.
Saying `bye` closes the window.

```
./gradlew run         build if needed, then open the window
./gradlew build       compile, check the style, run the tests, and package the JAR
./gradlew test        run the JUnit tests only
./gradlew shadowJar   package build/libs/emma.jar without running the tests
./gradlew clean       delete build/ and start fresh
```

When `build` fails, the reason is written to a report rather than only to the
terminal: style violations land in `build/reports/checkstyle/`, and failing tests
in `build/reports/tests/test/index.html`.

### The console version

Emma still runs as a plain terminal conversation, which is what the UI tests
drive. That half of the code uses neither JavaFX nor any packaged file, so plain
`javac` is enough:

```
javac -d out src/main/java/emma/*.java src/main/java/emma/command/*.java
java -cp out emma.Emma
```

## Building a JAR

`shadowJar` packages Emma and everything it needs into one runnable file:

```
./gradlew shadowJar
```

The JAR lands at **`build/libs/emma.jar`** and opens the window when run:

```
java -jar build/libs/emma.jar
```

It is a *fat* JAR — JavaFX included — so that one file is all another machine
needs beyond a JDK 25 runtime. Emma saves to `data/emma.json` **relative to the
folder you run it from**, so running the JAR elsewhere gives it its own task list.

### In VS Code

With the Extension Pack for Java installed, open the project folder and use the
**Run** code lens above `main` in
[Launcher.java](src/main/java/emma/gui/Launcher.java) for the window, or in
[Emma.java](src/main/java/emma/Emma.java) for the console version. Make sure VS Code
is configured to use JDK 25 (`Java: Configure Java Runtime`).

## Commands

| Command | What it does | Example |
|---|---|---|
| `todo DESCRIPTION` | Adds a task with no timing | `todo read book` |
| `deadline DESCRIPTION /by DATE` | Adds a task due on a date | `deadline return book /by 2019-10-15` |
| `event DESCRIPTION /from START /to END` | Adds a task spanning two dates | `event project meeting /from 2019-10-15 /to 2019-10-16` |
| `list` | Shows every task, numbered from 1 | `list` |
| `mark NUMBER` | Marks a task done | `mark 2` |
| `unmark NUMBER` | Marks a task not done | `unmark 2` |
| `delete NUMBER` | Removes a task | `delete 2` |
| `filter /type TYPE` | Shows only `todo`, `deadline` or `event` tasks | `filter /type deadline` |
| `filter /type deadline /due-by DATE` | Shows deadlines due on or before a date | `filter /type deadline /due-by 2019-10-15` |
| `filter /type event /at DATE` | Shows events running on a date | `filter /type event /at 2019-10-15` |
| `find TEXT` | Shows tasks with that text in their description | `find book` |
| `bye` | Exits | `bye` |

Tasks are shown as `[TYPE][STATUS] description`, where the type is `T`, `D` or
`E`, and the status is `x` once the task is done.

## Saved data

Emma writes her tasks to `data/emma.json` in the folder she is run from, and
reloads them on the next run. The file is saved after every change, and is
plain JSON so you can read or edit it by hand:

```json
[
  {
    "type": "D",
    "done": false,
    "description": "return book",
    "by": "2019-10-15"
  }
]
```

The `data/` folder is git-ignored, since the tasks are personal state rather
than part of the project. If the file is missing Emma starts with an empty list;
if it cannot be understood she says which line looks wrong and starts empty
rather than crashing.

## Project structure

All source lives in [src/main/java/](src/main/java/), split into two packages.

[emma/](src/main/java/emma/) — the app and the things it works with:

| File | Responsibility |
|---|---|
| `Emma.java` | Wires up the parts and runs the conversation |
| `Ui.java` | Everything printed to and read from the console |
| `gui/Launcher.java`, `gui/Main.java`, `gui/MainWindow.java`, `gui/DialogBox.java` | The JavaFX window and its chat bubbles |
| `Parser.java` | Turns a typed line into the command it asks for |
| `Task.java` | The shared behaviour of a task: description, done status, rendering |
| `Todo.java`, `Deadline.java`, `Event.java` | The three task types and their extra dates |
| `Dates.java` | Renders a `LocalDate` the way Emma shows it |
| `TaskList.java` | Holds the tasks in memory |
| `Storage.java` | Reads and writes the save file |
| `Json.java` | Quoting and escaping helpers for writing JSON |
| `EmmaException.java` | An error carrying the message Emma should show the user |

[emma/command/](src/main/java/emma/command/) — one class per command, each knowing
how to carry itself out:

| File | Responsibility |
|---|---|
| `Command.java` | What every command can do: `execute`, and whether it ends the session |
| `AddCommand.java` | The shared add-save-report work; subclasses only build the task |
| `AddTodoCommand.java`, `AddDeadlineCommand.java`, `AddEventCommand.java` | The three ways to add a task |
| `MarkCommand.java`, `DeleteCommand.java` | Change a numbered task |
| `ListCommand.java`, `FilterCommand.java` | Show tasks without changing any |
| `ByeCommand.java` | Ends the conversation |

[src/main/resources/](src/main/resources/) — files the window loads from the
classpath at startup rather than from disk:

| File | Responsibility |
|---|---|
| `css/emma.css` | The window's colours, fonts, padding and the red error bubble |
| `images/DaEmma.png`, `images/DaUser.png` | The two speakers' pictures |

These are packaged into the JAR, which is why the window runs from
`build/libs/emma.jar` but not from a folder of classes compiled by hand.

**Warning:** Keep `src/main/java` as the root folder for Java files (i.e., don't
rename those folders or move Java files elsewhere), as this is the default
location some tools (e.g., Gradle) expect to find Java files.

## Testing

There are two kinds of test, both run from the project root.

`./gradlew test` runs the JUnit suite in [src/test/java/](src/test/java/), which
covers the parser, every command, the task list, the three task types, the save
file and the chatbot itself. `./gradlew build` runs it too, so a failing test
fails the build.

[test/ui-test-plan.md](test/ui-test-plan.md) holds end-to-end cases for the
console interface: each one starts a fresh session, feeds Emma a list of
commands and compares what she prints against the expected output. They are run
by the `test-ui` Claude Code skill in [.claude/skills/test-ui/](.claude/skills/test-ui/).
