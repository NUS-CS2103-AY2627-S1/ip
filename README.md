# Kaya

Kaya is your kopitiam companion for keeping track of tasks, built for the
CS2103T individual project. Use short commands to manage todos, deadlines,
and events in a chat window. Grab a kopi and work through what's on your plate,
one thing at a time.

See the [user guide](docs/README.md) for command details and the
[testing guide](docs/Testing.md) for automated checks and manual GUI checks.

## Setting up in IntelliJ IDEA

Prerequisites: **JDK 25** and an IntelliJ IDEA version that supports Java 25.

1. Open the project directory in IntelliJ IDEA and let Gradle finish importing
   the dependencies.
1. Set the **Project SDK** to JDK 25 using the
   [IntelliJ SDK instructions](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk),
   and set the project language level to **SDK default**.
1. Ensure the **Gradle JVM** also uses JDK 25.
1. Open [Launcher.java](src/main/java/kaya/Launcher.java), then run
   `Launcher.main()`. The Kaya chat window should open with its greeting.

Keep Java packages under `src/main/java` and FXML/CSS files under
`src/main/resources`, following the existing Gradle project layout.

## Running from the project directory

Use Java 25 when running Gradle. On macOS with the course JDK installed through
SDKMAN, select it with:

```bash
sdk use java 25.0.3.fx-zulu
```

Open the graphical interface from the project root with:

```bash
./gradlew run
```

The GUI starts through `kaya.Launcher`. To use the console interface from
IntelliJ instead, run `main()` in [Kaya.java](src/main/java/kaya/Kaya.java).

## Building and running the executable JAR

Kaya requires Java 25. Build the executable JAR from the project root with:

```bash
./gradlew clean shadowJar
```

Gradle creates the JAR at `build/libs/kaya.jar`. To run it as a standalone
application, copy `kaya.jar` into an empty folder, open a terminal in that
folder, and run:

```bash
java -jar kaya.jar
```

Kaya creates its `data/kaya.txt` file relative to the folder from which the JAR
is run. The generated JAR and runtime data are build artifacts and should not be
committed to the repository.

## Acknowledgements

- Kaya started from the [Duke starter project](https://github.com/se-edu/duke)
  by **se-edu**. Its initial project structure and setup documentation were
  adapted for Kaya. The original template contributors are listed in
  [CONTRIBUTORS.md](CONTRIBUTORS.md).
- The JavaFX launcher, FXML loading, and chat-dialog structure were adapted from
  **se-edu's [JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html)**
  and its [starter repository](https://github.com/se-edu/javafx-tutorial),
  particularly [Part 4: Using FXML](https://se-education.org/guides/tutorials/javaFxPart4.html).
  The adapted components are [Main.java](src/main/java/kaya/Main.java),
  [Launcher.java](src/main/java/kaya/Launcher.java),
  [MainWindow.java](src/main/java/kaya/ui/MainWindow.java),
  [DialogBox.java](src/main/java/kaya/ui/DialogBox.java), and the
  [main-window](src/main/resources/view/MainWindow.fxml) and
  [dialog-box](src/main/resources/view/DialogBox.fxml) FXML files.
  Kaya customizes these with speaker labels, a responsive layout, its own
  styling, and task-command handling.
