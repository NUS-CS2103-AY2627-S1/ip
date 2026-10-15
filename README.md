# Elora

Elora is a desktop chatbot for tracking todos, deadlines, and events, controlled with a small
set of typed commands. It has both a JavaFX GUI (the default) and a plain console mode.

This started from the CS2103 individual project (iP) template, itself named after the Java
mascot _Duke_ - hence why some setup steps below still mention the template's origins.

For how to use Elora, see the [User Guide](docs/README.md).

## Setting up in IntelliJ

Prerequisites: JDK 25, update IntelliJ to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/elora/Launcher.java` file, right-click it, and choose `Run Launcher.main()`
   (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a chat window
   titled "Elora" should appear, with a greeting from Elora as the first message.
   To run the console version instead, run `src/main/java/elora/Elora.java`'s `main()` the same way.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running from the command line

```
./gradlew run          # launches the GUI (default)
./gradlew test         # runs the JUnit test suite
./gradlew shadowJar    # builds a runnable jar at build/libs/elora.jar
```

Java 25 is required. On macOS with SDKMAN, switch to it with `sdk use java 25.0.3.fx-zulu`.

## Acknowledgements

- This project was built with the assistance of [Claude Code](https://claude.com/claude-code) (Anthropic),
  used throughout development as an AI pair-programmer for the JavaFX GUI redesign, error-handling
  improvements, and this documentation.
- The Elora mascot artwork (`elora mascot.png`, cropped for use as the in-app avatar) was initially generated using ChatGPT and then cropped out.
