# Nob

Nob is a JavaFX task manager for tracking to-dos, deadlines, and events through
short text commands.

See the [Nob User Guide](docs/README.md) for the complete command reference and
usage examples.

## Requirements

- Java 25
- Windows, macOS, or Linux

## Running from source

From the project root, run:

```sh
./gradlew run
```

On Windows, use `gradlew.bat run` instead.

## Building the application

Create the executable JAR with:

```sh
./gradlew clean shadowJar
```

The application is generated at `build/libs/nob.jar`. Start it with:

```sh
java -jar build/libs/nob.jar
```

The JAR includes the JavaFX dependencies for Windows, macOS, and Linux.

## Testing

Run the automated checks with:

```sh
./gradlew check
```

The console regression-test plan and latest recorded session are in the
[`test`](test) directory.

## Data storage

Nob stores tasks in `data/nob.txt`. If the file or its parent directory does
not exist, Nob starts with an empty task list and creates them when it next
saves a task.
