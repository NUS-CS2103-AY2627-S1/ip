# Peter

> “Your mind is for having ideas, not holding them.” — David Allen ([source](https://gettingthingsdone.com/))

Peter is a calm task navigator with both a JavaFX GUI and a text-based interface. It helps you chart todos, deadlines, and events while keeping every response clear, encouraging, and quick to scan. 🧭

Read the [Peter User Guide](https://peterpei666.github.io/ip/) for command examples and usage instructions.

## Features

- Add todos, deadlines, and events
- List, mark, unmark, and delete tasks
- Find tasks using a keyword
- View deadlines on a selected date
- Sort deadlines chronologically
- Save tasks automatically and restore them the next time Peter starts

## Getting started

Peter requires **JDK 25**. To run it from a terminal:

1. Clone this repository.
2. Open the project directory in VS Code or a terminal.
3. Make sure Java 25 is active.
4. Run `./gradlew run` for the GUI, or `./gradlew runText` for the text-based UI.
5. Enter a command such as `todo read book`.

You can learn more about Markdown used in this document from the [GitHub Flavored Markdown guide](https://guides.github.com/features/mastering-markdown/).

## Example commands

```text
todo read book
deadline return book /by 2026-09-10 1800
event project meeting /from 1400 /to 1600
list
mark 1
find book
view 2026-09-10
sort
bye
```

## Project progress

- [x] Manage todos
- [x] Manage deadlines
- [x] Manage events
- [x] Save and load tasks
- [x] Search tasks by keyword
- [x] Sort deadlines chronologically
- [x] Add a graphical user interface

## Java entry point

If you are learning Java, the `main` method is a useful place to begin exploring the code:

```java
public static void main(String[] args) {
    new Peter("data/peter.txt").run();
}
```

## Testing and style checks

Run the automated tests and Checkstyle checks before submitting changes:

```bash
./gradlew test checkstyleMain checkstyleTest
```

Generate the JaCoCo coverage report with:

```bash
./gradlew jacocoTestReport
```

The HTML report is written to `build/reports/jacoco/test/html/index.html`. The `check` task enforces at least
95% line coverage and 90% branch coverage for automatically testable, non-GUI code. Keeping these checks green
makes the project easier to understand and maintain.
