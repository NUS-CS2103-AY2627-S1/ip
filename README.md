# Esther

Esther is a playful task assistant for managing todos, deadlines, and events.
She saves tasks automatically and restores them when the application starts.

## Getting started

Use Java 25 and run Esther from the project root:

```text
./gradlew run
```

On Windows, use:

```text
.\gradlew.bat run
```

The complete command reference is available in the [User Guide](docs/README.md).

## Main commands

```text
todo read the project notes
deadline submit report /by 2026-09-18
event tutorial /from 2pm /to 4pm
list
find project
mark 1
unmark 1
delete 1
sort
bye
```

## Building

Run the test suite, Checkstyle, and create the fat JAR with:

```text
./gradlew clean test checkstyleMain checkstyleTest shadowJar
```

## Acknowledgements

Development was assisted by OpenAI Codex for brainstorming, implementation,
debugging, test generation, and documentation.

The project uses JavaFX for the graphical interface and the Gradle Shadow
plugin to create the distributable fat JAR.
