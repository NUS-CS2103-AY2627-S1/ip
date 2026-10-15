# Suki

Suki is a personal assistant chatbot that helps you keep track of your tasks, built from the Duke project template for CS2103T.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/suki/Suki.java` file, right-click it, and choose `Run Suki.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____   _   _  _  __ ___ 
   / ___| | | | || |/ /|_ _|
   \___ \ | | | || ' /  | | 
    ___) || |_| || . \  | | 
   |____/  \___/ |_|\_\|___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building from the command line

The project is built with Gradle. The wrapper (`gradlew`) is committed, so
there is nothing to install first: it downloads the right Gradle version by
itself on the first run. All commands below are run from the project root,
and use `./gradlew` on macOS and Linux or `gradlew.bat` on Windows.

Run it:

```
./gradlew run
```

This opens the GUI. The original text interface is still there and is handy
for testing, since the core logic can be exercised without driving a window.
After a `./gradlew build`, start it with:

```
java -cp build/classes/java/main suki.Suki
```

### Running the tests

```
./gradlew test
```

The JUnit dependency is declared in `build.gradle`, so it no longer needs
to be downloaded by hand.

### Building the JAR

```
./gradlew shadowJar
java -jar build/libs/suki.jar
```

The JAR is self-contained and can be run from any directory. Suki saves tasks
to `data/suki.txt` relative to the directory it is started in, creating the
folder if needed.
