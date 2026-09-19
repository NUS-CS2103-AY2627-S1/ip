# Chai

Chai is a desktop task-tracking chatbot built with Java 25 and JavaFX. It supports todos, deadlines, events,
completion tracking, deletion, and keyword search through a command-oriented chat interface.

## Running Chai

Make sure JDK 25 is active, then run:

```shell
./gradlew run
```

The distributable JAR can be created and launched with:

```shell
./gradlew shadowJar
java -jar build/libs/chai.jar
```

## Checking code quality

Run the tests and Checkstyle checks with:

```shell
./gradlew test checkstyleMain checkstyleTest
```

Checkstyle reports are generated under `build/reports/checkstyle/` if a violation is found.

## Commands

- `todo <description>`
- `deadline <description> /by <yyyy-MM-dd>`
- `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`
- `list`
- `mark <task number>`
- `unmark <task number>`
- `delete <task number>`
- `find <keyword>`
- `sort` (orders deadlines by due date and events by start date, followed by todos)
- `bye`
