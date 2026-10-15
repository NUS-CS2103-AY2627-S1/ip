# Nori

Nori is a desktop task companion built for the CS2103 individual project.

See the [User Guide](docs/README.md) for setup, commands, and troubleshooting.

![Nori desktop interface](docs/Ui.png)

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Locate `src/main/java/nori/Launcher.java`, right-click it, and choose `Run Launcher.main()` to open the GUI.

## Building and checking

With Java 25 selected, run `./gradlew test checkstyleMain checkstyleTest shadowJar`
(on Windows, use `.\gradlew.bat` instead of `./gradlew`). This runs the tests and
coding-standard checks and builds `build/libs/nori.jar`.

Launch it with `java -jar build/libs/nori.jar`. The optional command-line interface
is available by running `nori.Nori.main()` in IntelliJ.

See [Week 6 verification](docs/week6-verification.md) for the enhancement scope
and manual GUI checks.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
