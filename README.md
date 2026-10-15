# Kiaw

Kiaw is a desktop task management chatbot that helps users organise todos,
deadlines, and events using simple text commands.

Kiaw provides a JavaFX graphical interface and stores task data locally so
that tasks remain available between sessions.

## Features

Kiaw supports:

- Adding todos
- Adding deadlines with due dates
- Adding events with start and end dates
- Listing tasks
- Marking and unmarking tasks
- Deleting tasks
- Finding tasks by keyword
- Sorting dated tasks chronologically
- Persistent local storage
- Graceful handling of invalid commands and corrupted stored records
- A JavaFX graphical user interface

## Example commands

```text
todo read book

deadline submit assignment /by 2026-09-15

event project meeting /from 2026-10-01 /to 2026-10-02

list

find book

sort

mark 1

unmark 1

delete 1

bye
```

Dates should be entered in `yyyy-MM-dd` format.

## Running Kiaw

### Using Gradle

Run:

```text
./gradlew run
```

On Windows PowerShell:

```text
.\gradlew.bat run
```

### Using the JAR file

Generate the JAR using:

```text
./gradlew shadowJar
```

On Windows:

```text
.\gradlew.bat shadowJar
```

The generated JAR can be found in:

```text
build/libs/
```

## User Guide

See [`docs/README.md`](docs/README.md) for the detailed user guide.

## Acknowledgements

This project was developed with some assistance from ChatGPT by OpenAI.

The generated and suggested changes were reviewed, tested, and integrated by
the project author.

This project was developed using the CS2103 iP materials and SE-EDU Duke
project resources provided as part of the course.
