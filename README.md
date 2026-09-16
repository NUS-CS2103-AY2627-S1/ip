# Grow-er

Grow-er is a desktop task manager that lets you manage to-dos, deadlines, and events through text commands in a chat-style interface. Tasks are saved automatically between sessions.

![Grow-er application window](docs/Ui.png)

## What you can do

- Track to-dos, deadlines, and events with start and end times.
- Mark tasks as completed or incomplete.
- Find, sort, and delete tasks as your plans change.

## Quick start

1. Install **Java 25** and check your version with `java -version`.
2. Download `grower.jar` from [Releases](https://github.com/liownahum/ip/releases), if available, and place it in a folder of your choice. You can also build it from source using the instructions below.
3. Open a terminal in that folder and launch the app:

   ```sh
   java -jar grower.jar
   ```

4. Enter `todo read book`, then try `list` and `mark 1`.

Read the **[User Guide](docs/README.md)** for all commands, examples, date formats, and help with saved data or errors.

## Build and run from source

With **JDK 25** installed, run these commands from the project folder:

```sh
./gradlew shadowJar
java -jar build/libs/grower.jar
```

On Windows, use `gradlew.bat shadowJar` for the build step. The build packages the application and its dependencies into `build/libs/grower.jar`.

To work on the project in IntelliJ IDEA, open the project folder, import the Gradle project, and set both the project SDK and Gradle JVM to **JDK 25**. Run the `grower.Launcher` class to start the desktop app.

## Acknowledgement

- **User profile picture:** Plant illustration from [40+ Easy Cute Plant Drawing Ideas](https://sketchandfun.com/plant-drawing/) by Sketch and Fun.
- **Grower profile picture:** [Father Charles](https://amulet.fandom.com/wiki/Father_Charles), sourced from the Amulet Wiki on Fandom.
- **Application icon:** [Tree](https://openclipart.org/detail/170749/tree) by diamonjohn on Openclipart, released under [CC0 1.0 (public-domain dedication)](https://creativecommons.org/publicdomain/zero/1.0/).

## AI usage acknowledgement

The project author used OpenAI Codex to assist with various parts of the codebase. This includes using AI for debugging,
feedback on ideas and implementation, writing the Gui, A-BetterGui, A-Personality, A-MoreErrorHandling,
and A-MoreTesting tags of the code. Furthermore, some unit tests were written by codex. 