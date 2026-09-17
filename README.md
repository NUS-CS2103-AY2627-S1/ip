# LeBron

LeBron is a desktop task-tracking chatbot for people who prefer typing to clicking, with a basketball-slang personality. It supports todos, deadlines, events, recurring tasks, marking/unmarking, searching, and saves your list automatically. It has both a JavaFX GUI and a text-based CLI, sharing the same underlying logic.

## Running it

**GUI** (recommended):
```
gradlew run
```

**Packaged jar** (bundles JavaFX, so it works on machines without JavaFX installed):
```
gradlew shadowJar
java -jar build/libs/LeBron.jar
```

**CLI** (no JavaFX window, plain text): run the `main` method in `lebron.LeBron` from your IDE.

## Setting up in IntelliJ

Prerequisites: JDK 25.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first).
2. Open the project into IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions), and set the **Project language level** to the `SDK default` option.
4. Once Gradle finishes syncing, locate `src/main/java/lebron/gui/Launcher.java`, right-click it, and choose `Run 'Launcher.main()'`. The LeBron chat window should open.

**Warning:** Keep `src/main/java` as the root folder for Java files (i.e., don't rename it or move Java files outside of this path), as this is the default location Gradle expects to find Java files.
