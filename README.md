# Sace

Sace is a JavaFX task manager with the personality of a moonlit strategist. It
manages todos, deadlines, and events through concise commands and saves the quest
log automatically between sessions.

![Sace desktop interface](docs/Ui.png)

## Highlights

- Polished, resizable JavaFX chat interface with visually distinct error replies
- Todos, deadlines, and events in one persistent quest log
- Case-insensitive search across task descriptions
- Friendly validation for malformed commands, invalid dates, and task numbers
- Graceful recovery from missing, unreadable, or damaged data files
- Automatic rollback when a change cannot be saved
- In-app help through the `help` command

## Run Sace

Sace requires **Java 25**. Download `sace.jar` from the
[latest release](https://github.com/ChesterLZW/ip/releases), open a terminal in
the JAR's folder, and run:

```text
java -jar sace.jar
```

For every command and example, see the
**[Sace User Guide](https://chesterlzw.github.io/ip/)**.

## Build from source

Open the project with JDK 25, then run the Gradle wrapper from the repository
root.

```text
./gradlew clean check shadowJar
```

On Windows PowerShell, use:

```text
.\gradlew.bat clean check shadowJar
```

The executable fat JAR is generated as `build/libs/sace.jar`. The JavaFX entry
point is `sace.Launcher`.

## Documentation

- [User Guide](docs/README.md)
- [Contributors](CONTRIBUTORS.md)

Sace was developed from the NUS CS2103/T individual-project starter repository.
