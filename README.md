# FRIDAY

FRIDAY is a JavaFX desktop task assistant built for the NUS CS2103 individual project. It supports todos, deadlines,
events, search, chronological sorting, reminders, and recurring tasks through a compact chat interface.

See the [FRIDAY User Guide](docs/README.md) for installation and command instructions.

## Development

This project requires Java 25.

```bash
./gradlew run
./gradlew test
./gradlew checkstyleMain checkstyleTest
./gradlew clean shadowJar
```

The distributable fat JAR is generated at `build/libs/friday.jar`.
