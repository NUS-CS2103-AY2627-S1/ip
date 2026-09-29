# Pavanmaxxer

Pavanmaxxer is a desktop task manager for people who prefer entering short
commands instead of navigating through menus. It supports todos, deadlines,
events, completion tracking, search, automatic saving, and duplicate-task
warnings.

![Pavanmaxxer interface](docs/Ui.png)

## Requirements

- Java 25
- macOS, Windows, or Linux with a graphical desktop

## Run the application

Download `pavanmaxxer.jar` from the latest GitHub release, place it in an empty
folder, open a terminal in that folder, and run:

```text
java -jar pavanmaxxer.jar
```

Pavanmaxxer creates `data/pavanmaxxer.txt` beside the JAR's working directory
and automatically saves changes there.

## Development

Run the application from source:

```text
./gradlew run
```

Run the automated checks:

```text
./gradlew test
./gradlew check
```

Build the distributable fat JAR:

```text
./gradlew clean shadowJar
```

The generated application is located at `build/libs/pavanmaxxer.jar`.

## Documentation

Read the complete [Pavanmaxxer User Guide](docs/README.md), or visit the
[published product website](https://pavan2184.github.io/ip/).
