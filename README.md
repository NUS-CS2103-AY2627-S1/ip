# WWaffle

A Java 25 / JavaFX personal task manager with a warm café interface, checklist task displays,
concise replies, and selected music-themed responses.

## Run and build

Use JDK 25:

```bash
./gradlew run
./gradlew test checkstyleMain checkstyleTest
./gradlew clean shadowJar
java -jar build/libs/wwaffle.jar
```

The fat JAR is `build/libs/wwaffle.jar`. Saved tasks are in `data/wwaffle.txt` relative to the
launch directory. GUI manual smoke tests are listed in `tests/final-release-checklist.md`.

- [User Guide](docs/README.md)
- [Credits](docs/credits.md)
- [Release checklist](tests/final-release-checklist.md)

## Implemented increments

- **A-BetterGui:** responsive wrapping, distinct user/bot messages, task checklist rows,
  completed-task strikethrough, compact artwork, and a neutral palette.
- **A-Personality:** WWaffle name, café avatars, concise conversational responses,
  Minion reactions, and selected music references.
- **A-MoreErrorHandling:** invalid dates/indices/arguments, whitespace normalization,
  malformed-file protection, atomic file replacement where supported, and rollback after failed saves.
- **A-MoreTesting:** JUnit regression tests for commands, persistence, failed writes,
  corrupted input, whitespace, Unicode, task ordering, and response data.

This project builds on the course starter template. See the credits before distribution.
