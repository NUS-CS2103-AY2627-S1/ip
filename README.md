# Baby

Baby is a Java 25 desktop task-management chatbot with a winter-themed JavaFX interface. It
supports todos, deadlines, events, task searching, persistent storage, and friendly error handling.

See the [Baby User Guide](docs/README.md) for installation instructions, command formats, examples,
and usage notes.

## Build

```bash
./gradlew clean test checkstyleMain checkstyleTest shadowJar
```

The packaged application is created at `build/libs/Baby.jar`.
