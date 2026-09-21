# Wiz - Magical Task & Client Manager

Wiz is a magical desktop task and contact manager that helps travelers manage tasks, deadlines, gatherings, and client contacts through an intuitive Graphical User Interface (GUI) powered by text commands.

## Setting up in IntelliJ

Prerequisites: JDK 25, updated IntelliJ IDEA.

1. Open IntelliJ (if you are not on the welcome screen, click `File` > `Close Project` to close the existing project first).
2. Open the project in IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. Locate the `src/main/java/wiz/Launcher.java` or `src/main/java/wiz/Main.java` file, right-click it, and choose `Run Launcher.main()` or `Run Main.main()`.

**Warning:** Keep the `src/main/java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Testing

This project follows a test coverage target of 50%. JUnit tests are required to verify the behavior of the code.

JUnit tests must be updated after each code change to comply with this target.
