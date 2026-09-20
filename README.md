# Harold project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_, my dog Harold. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
      1. After that, locate `src/main/java/harold/gui/Launcher.java`, right-click it,
         and choose `Run Launcher.main()`.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running Harold

Launch the JavaFX interface from the project root:

```shell
./gradlew run
```

The GUI provides a scrollable conversation, a command field, and a Send button.
Pressing Enter in the command field also submits the command. To use the original
command-line interface instead, run:

```shell
./gradlew runCli
```

## Creating the executable JAR

Run the Shadow task from the project root:

```shell
./gradlew shadowJar
```

On Windows, use `gradlew.bat shadowJar` instead. The fat JAR is created at
`build/libs/harold.jar`. Run it from the project root with:

```shell
java -jar build/libs/harold.jar
```

## Checking code style

Run Checkstyle against both production and test Java sources:

```shell
./gradlew checkstyleMain checkstyleTest
```

The broader `./gradlew check` task also runs Checkstyle together with the
project's other verification tasks. Checkstyle uses the SE-EDU rules in
`config/checkstyle/checkstyle.xml`.
