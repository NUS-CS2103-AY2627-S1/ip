# Crack

Crack is a chatbot that keeps track of your tasks: todos, deadlines and events.
Talk to it in a JavaFX chat window, or from the terminal. It saves your list to
a text file so everything is still there next time.

This page is about building and running Crack from source. For how to use it,
read the [user guide](docs/README.md).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/crack/gui/Launcher.java` file, right-click it, and choose `Run Launcher.main()` to open the GUI (if the code editor is showing compile errors, try restarting the IDE). `src/main/java/crack/Crack.java` runs the text-mode version instead.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


## Building and running

Prerequisites: JDK 25. Gradle comes with the wrapper, so there's nothing else to install.

```
./gradlew run          # launch the JavaFX GUI (crack.gui.Launcher)
./gradlew test         # run the unit tests
./gradlew checkstyleMain checkstyleTest   # check the code against the se-edu style
./gradlew shadowJar    # build a runnable jar at build/libs/crack.jar
```

To run the jar on its own:

```
java -jar build/libs/crack.jar
```

The jar bundles JavaFX, so it runs anywhere a JDK is installed.

### Text mode

The original terminal version still works. Gradle's `run` task is wired to the
GUI, so compile and run it directly:

```
javac -d build/text $(find src/main/java -name '*.java' -not -path '*/gui/*')
java -cp build/text crack.Crack
```

You can also pipe commands into it, which is handy for quick checks:

```
printf 'todo read book\nlist\nbye\n' | java -cp build/text crack.Crack
```

### Where tasks are saved

Both modes read and write `data/data.txt`, relative to the working directory,
so launch from the project root to see the same list every time. The user guide
covers the [save file](docs/README.md#where-your-tasks-are-kept) in full.
