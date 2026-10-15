# Gnaix

Gnaix is a Java/JavaFX task-management assistant for tracking todos,
deadlines, events, dates, search results, and tags.

```text
  ____ _   _    _    _____  __
 / ___| \ | |  / \  |_ _\ \/ /
| |  _|  \| | / _ \  | | \  /
| |_| | |\  |/ ___ \ | | /  \
 \____|_| \_/_/   \_\___/_/\_\
```

## Requirements

- JDK 25
- Gradle wrapper included in this repository

## Running the application

To run the JavaFX GUI:

```bash
./gradlew run
```

On Windows:

```bash
.\gradlew.bat run
```

To build the executable JAR:

```bash
./gradlew build
```

On Windows:

```bash
.\gradlew.bat build
```

The shaded executable JAR is generated at:

```text
build/libs/gnaix.jar
```

Run it with:

```bash
java -jar build/libs/gnaix.jar
```

## Commands

| Command | Example |
| --- | --- |
| Add a todo | `todo read notes` |
| Add a deadline | `deadline submit quiz /by 2026-09-10` |
| Add an event | `event team meeting /from 2026-09-15 1000 /to 2026-09-15 1100` |
| List tasks | `list` |
| Mark done | `mark 1` |
| Unmark | `unmark 1` |
| Delete | `delete 1` |
| Find by keyword | `find quiz` |
| Find by date | `date 2026-09-10` |
| Find by tag | `tag #school #urgent` |
| Exit | `bye` |

Tags can be added to todos, deadlines, and events by placing them at the end
of the command, such as:

```text
todo revise CS2103T #school #urgent
```

## Testing

Run the automated tests:

```bash
./gradlew clean test
```

Run Checkstyle:

```bash
./gradlew checkstyleMain checkstyleTest
```

Run the full build:

```bash
./gradlew build
```

## Project structure

- `src/main/java/gnaix`: application, parser, storage, GUI, and command logic
- `src/main/java/gnaix/task`: task model classes
- `src/main/resources`: JavaFX FXML, CSS, and image resources
- `src/test/java`: JUnit tests
- `docs`: user-facing and feature documentation

Keep `src/main/java` as the Java source root so Gradle and the IDE can locate
the source files correctly.

## Credits

Character-style GUI images are credited to *Family Guy* and used for this
non-commercial educational project.
