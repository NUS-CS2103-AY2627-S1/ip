# MeowMeow

A desktop task manager you operate by typing, built for CS2103T.

MeowMeow keeps todos, deadlines and events in one list, saves every change to
disk immediately, and refuses input that doesn't make sense rather than quietly
guessing at it.

![MeowMeow](docs/Ui.png)

**[User Guide](https://ethanlaiii.github.io/ip/)** — commands, date formats,
and what MeowMeow deliberately rejects.

## Running it

Requires **Java 25**.

```
java -jar meowmeow.jar
```

Download the JAR from the [releases page](https://github.com/ethanlaiii/ip/releases).
Run it from a folder of its own — MeowMeow creates a `data` folder in the
working directory for its save file.

## Building from source

```
git clone https://github.com/ethanlaiii/ip.git
cd ip
./gradlew run
```

Other useful tasks:

| Command | What it does |
|---|---|
| `./gradlew run` | Launch the app |
| `./gradlew test` | Run the test suite |
| `./gradlew build` | Compile, test, and assemble |
| `./gradlew clean shadowJar` | Build the distributable JAR into `build/libs/` |

The JAR bundles JavaFX, so it runs on any machine with a plain JDK 25 and no
separate JavaFX install.

## Project layout

```
src/main/java/meowmeow/
├── Launcher.java          entry point; works around a JavaFX classpath issue
├── MeowMeow.java          routes a command to the right operation
├── MeowMeowException.java errors that are shown to the user, not thrown at them
├── parser/                turns typed text into commands and tasks
├── storage/               reads and writes the save file
├── task/                  the task types, the list, and date handling
└── ui/                    JavaFX window, dialog boxes, and message formatting
```

Tests live under `src/test/java/meowmeow/`, mirroring the same packages.

## Acknowledgements

The JavaFX classes and FXML are adapted from the SE-EDU JavaFX tutorial, and
the project is built on the Duke template from the CS2103T teaching team. See
[CONTRIBUTORS.md](CONTRIBUTORS.md) for details of what was reused and how far it
was modified.