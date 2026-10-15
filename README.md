# Chocolate project template

This is a project template for the Chocolate chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/chocolate/Chocolate.java` file, right-click it, and choose `Run Chocolate.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   Chocolate
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running with Gradle

From the project root, build the application with:

```bash
./gradlew build
```

Run Chocolate with:

```bash
./gradlew run
```

Run the JUnit test suite with:

```bash
./gradlew test
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Packaging an executable JAR

Create the executable fat JAR with:

```bash
./gradlew clean shadowJar
```

The generated file is located at `build/libs/Chocolate.jar`. Copy only that JAR into an empty
folder, open a terminal in that folder, and run:

```bash
java -jar "Chocolate.jar"
```

The destination computer needs Java 25, but does not need Gradle or separately installed
application dependencies.
