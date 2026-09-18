# Meowmeow

<img src="docs/Ui.png" alt="Meowmeow UI" width="600">

Meowmeow is a desktop task manager you talk to by typing - track todos,
deadlines, and events from a single chat-style window, no mouse required
(and it has opinions about your to-do list, whether you asked or not).

* **[Read the User Guide](https://cysiac.github.io/ip/)** for the full
  command reference.

## Features

* Add todos, deadlines (`/by`), and events (`/from`/`/to`)
* Mark tasks done/not done, delete them, or search by keyword
* Filter tasks by date, and set a priority (high/medium/low) on any task
* Tasks are saved automatically after every change - nothing to lose if
  you close the window

## Quick start (using the app)

1. Ensure you have Java 25 installed.
2. Download the latest `meowmeow.jar`.
3. Run it with `java -jar meowmeow.jar`, or double-click it.

## Building from source

Prerequisites: JDK 25.

* `./gradlew run` - launches the GUI.
* `./gradlew shadowJar` - builds `build/libs/meowmeow.jar`, a
  self-contained JAR you can run with `java -jar`.
* `./gradlew check` - runs the JUnit test suite and Checkstyle.

### Setting up in IntelliJ

1. Open IntelliJ (if you are not in the welcome screen, click `File` >
   `Close Project` to close the existing project first).
2. Open the project into IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as
   explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the
   `SDK default` option.
4. To run the app, locate `src/main/java/meowmeow/gui/Launcher.java`,
   right-click it, and choose `Run Launcher.main()`. This launches the
   GUI - the same app `meowmeow.jar` runs.
   (There's also a plain console version at
   `src/main/java/meowmeow/Meowmeow.java`, useful for quick testing
   without the GUI - right-click it and choose `Run Meowmeow.main()`.)
   ```
   ____________________________________________________________
   (=^-ω-^=)  Meowmeow
   I'm Meowmeow. Let's see what you've got.
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java
files (i.e., don't rename those folders or move Java files to another
folder outside of this folder path), as this is the default location
some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

This project makes pervasive use of AI assistance (Claude Code) for design
discussion, implementation, and keeping the JUnit and console UI test
suites in sync with behavioural changes - including the A-Personality and
A-BetterGui increments. Every AI-assisted change is reviewed, tested, and
committed by the project owner.
