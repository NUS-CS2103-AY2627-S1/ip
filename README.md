# Chris

Chris is a desktop task manager for users who prefer fast, text-based commands. It supports todos, deadlines, and events through a JavaFX graphical interface and saves task changes automatically.

![Chris application](docs/Ui.png)

## Useful links

- [Download the latest Chris JAR](https://github.com/hyunmin1211/ip/releases/latest/download/chris.jar)
- [Read the published User Guide](https://hyunmin1211.github.io/ip/)
- [View the User Guide on GitHub](docs/README.md)

## Features

- Add todos, deadlines, and events.
- List and search for tasks.
- Mark and unmark tasks.
- Delete tasks.
- Detect duplicate tasks.
- Save and restore tasks automatically.
- Handle invalid commands with helpful error messages.

## Running Chris

### Requirements

- Java 25

### Running the JAR

1. Download `chris.jar` from the latest GitHub release.
2. Place the JAR file in an empty folder.
3. Open a terminal in that folder.
4. Run:

   ```shell
   java -jar "chris.jar"
   ```

Chris stores its task data in `data/chris.txt`, relative to the folder from which the application is run.

## Building from source

Run the following command from the project root to test the project and create the executable JAR:

```shell
./gradlew clean test shadowJar
```

The generated JAR is located at `build/libs/chris.jar`.

## AI Use Declaration

I followed the course's AI-use guidance and restrictions throughout this project. I used AI tools across the project increments, primarily through the suggested prompts. Before accepting AI-generated suggestions, I wrote pseudocode for the intended logic, compared it with the generated code, and reviewed each change in detail. I tested the resulting behavior and made edits where necessary.
