# Hampster 🐹

Hampster is a small **Java 25** command-line task manager. It supports todo items, deadlines, events, listing, finding, marking, deleting, and saving tasks to storage. The name is a playful reference to the Java mascot _Duke_.

## Prerequisites

- JDK 25
- IntelliJ IDEA or a terminal
- Gradle Wrapper included in this repository

## Running the application

1. Open the project in IntelliJ IDEA, or open a terminal in the project directory.
2. Confirm that the project SDK and language level are set to **JDK 25**.
3. Start the interactive application with:

```powershell
.\gradlew run
```

4. When Hampster asks `Whaddya want?`, enter a command and press Enter.

## Available commands

- `todo <description>` — create a todo task
- `deadline <description> /by <date>` — create a deadline
- `event <description> /from <date> /to <date>` — create an event
- `list` — display all tasks
- `find <keyword>` — search task descriptions
- `mark <number>` and `unmark <number>` — update task status
- `delete <number>` — remove a task
- `bye` — exit Hampster

For the complete command syntax, see the [source files](src/main/java/hampster/command) and the [command parser](src/main/java/hampster/parser/CommandParser.java).

## Development checklist

- [x] Configure Java 25
- [x] Run the application from an interactive terminal
- [ ] Add more automated tests

**Warning:** Keep Java source files under `src/main/java`; Gradle expects this standard project layout. ~~Move Java files wherever you like.~~
