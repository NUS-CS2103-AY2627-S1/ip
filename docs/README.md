# Mochi User Guide

Mochi is your little pocket of calm: a desktop assistant for recording todos,
deadlines, and events, one bite at a time. It remembers tasks between launches
and highlights commands that need a little fix.

![Mochi desktop interface](Ui.png)


## Quick start

1. Install Java 25 and run `java -version` to check your version.
2. Download **mochi.jar** from the [latest release](https://github.com/pinxinfang/ip/releases/latest).
3. Put it in a folder of your choice, open a terminal in that folder, and run:

   ```text
   java -jar mochi.jar
   ```

Press Enter or click **Send** to submit a command. Resize the window to give
long replies more room. **Clear** clears the conversation and restores the
greeting; it does not delete tasks. Errors have an orange panel labelled
**MOCHI · NEEDS A LITTLE FIX**, with an explanation of what to correct.

### Running from source

To build the app yourself, you need JDK 25 and the complete project folder, including
`gradlew` and `gradlew.bat`. Run commands from the folder containing
`build.gradle`.

On macOS/Linux, run:

```text
./gradlew run
```

On Windows, run:

```text
gradlew.bat run
```

The first run may need an internet connection to download dependencies. In
IntelliJ IDEA, set the Project SDK to JDK 25 and the language level to
`SDK default`.

## Adding tasks

Enter `todo` followed by a description:

```text
todo read the assigned chapter
Tucked away! I've added this task:
  [T][ ] read the assigned chapter
```

Use `/by` for a deadline date in `yyyy-MM-dd` format:
`deadline submit report /by 2026-09-18`

Use `/from` and `/to` for an event:
`event project meeting /from Mon 2pm /to 4pm`

Event times may be free-form text. For calendar-date events, use
`event study retreat /from 2026-09-18 /to 2026-09-20`. ISO dates must be real
dates and the end date must be later than the start date. Identical start
and end values are rejected. Other free-form times are stored as entered;
Mochi does not interpret their chronological order.

Commands are lowercase. Leading/trailing spaces and extra spaces between
the command and its details are accepted. Put spaces around `/by`, `/from`,
and `/to`; use each required marker exactly once. Task details cannot contain
`|`, tabs, or newlines because these would make saved records ambiguous.
Tasks with repeated descriptions are allowed.

## Managing tasks

`list` displays all tasks. `mark 2` and `unmark 2` change task 2's status,
while `delete 2` removes it. `find book` searches descriptions without regard
to letter case. `sort` arranges tasks alphabetically and saves the new order.
Use `bye` to close Mochi (in either desktop or terminal mode).

Task numbers refer to the current full list, starting at 1. After `find`,
run `list` to check the full-list number before marking or deleting a task.

## Saving tasks

Mochi stores tasks in `data/mochi.txt`. The file is created automatically and
existing tasks are loaded when Mochi starts. The application reports a
friendly error if saved data is unreadable or malformed. In that case, task
changes are blocked to preserve the original file. Back up `data/mochi.txt`,
repair the reported line or move the file aside, and restart Mochi. Moving
it aside starts a fresh list without deleting your backup.

If a save fails, Mochi keeps the previous task list and shows an error.
Check that the folder is writable and has available space, then retry the
command. Keep using the same working folder to load the same tasks.

## Sorting tasks

Enter `sort` to arrange all tasks alphabetically by description, ignoring
letter case. Tasks with matching descriptions keep their relative order.
Task types, dates, and completion status do not affect sorting.

For example, after adding `todo Zebra` and `todo apple`, `sort` displays:

```text
Here are the tasks in your list:
1.[T][ ] apple
2.[T][ ] Zebra
```

The new order is saved and retained after restarting Mochi. Use the new list
numbers for `mark`, `unmark`, and `delete`. Sorting an empty list is valid.
`sort` accepts no arguments: `sort date` reports
`Oops! The sort command does not take extra details.` without changing the list.
The existing saved-task format remains compatible.

## Suggested first test

Enter these commands one at a time:

```text
todo borrow library book
deadline submit assignment /by 2026-09-18
event group meeting /from Tue 3pm /to 4pm
list
find book
mark 1
unmark 1
sort
delete 2
list
```

The first three commands create three tasks. `find book` should show the
library-book task, `mark 1` should mark it done, and `delete 2` should remove
the second task in the current list. Close Mochi, start it again, and enter
`list` to verify that the remaining tasks were saved.

## Optional terminal mode

To run Mochi without the desktop window:

```text
./gradlew build
java -cp build/classes/java/main:build/resources/main mochi.Mochi
```

On Windows, use `;` instead of `:` in the classpath:

```text
gradlew.bat build
java -cp "build\classes\java\main;build\resources\main" mochi.Mochi
```

Type `bye` to exit terminal mode.

## Packaged JAR

To create a self-contained JAR, run `./gradlew shadowJar` (or
`gradlew.bat shadowJar` on Windows). Start it with:

```text
java -jar build/libs/mochi.jar
```

The JAR still requires JDK 25. Copy the `data` folder with it if you want to
keep an existing task list.

## Troubleshooting

- **Java is missing or is not version 25:** install/select JDK 25 and restart
  the terminal or IntelliJ IDEA.
- **`NoClassDefFoundError: javafx/application/Application`:** reload or link
  `build.gradle` as a Gradle project in IntelliJ IDEA, then run the `run`
  Gradle task. Alternatively, launch from the project folder with
  `./gradlew run`; running `mochi.Launcher` as a plain Java application does
  not include the JavaFX dependencies.
- **Permission denied for `./gradlew`:** run `chmod +x gradlew` once on
  macOS/Linux, then retry.
- **Tasks appear missing:** start Mochi from the same working folder where they
  were created; Mochi uses the relative path `data/mochi.txt`.
- **A command is rejected:** check its spelling and required parts. Deadlines
  require `/by` and a date; events require both `/from` and `/to`.

## Acknowledgements

Mochi builds on the [SE-EDU Duke project](https://github.com/se-edu/duke)
and the [course starter template](https://github.com/NUS-CS2103-AY2627-S1/ip).
The original JavaFX setup followed the course tutorial. OpenAI Codex helped
implement and review the GUI, Mochi's wording, error handling, automated tests,
and this guide. The current colour scheme and layout use standard JavaFX
controls and were developed for Mochi; no external artwork is included.
