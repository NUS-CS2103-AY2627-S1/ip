# Bingus Project

A chatbot application developed in Java. Below are instructions on how to use it.

## Setting up in IntelliJ

Prerequisites: JDK 25, update IntelliJ to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run the application using either of these options:
   - In IntelliJ, locate `src/main/java/bingus/Launcher.java`, right-click it, and choose `Run Launcher.main()`.
   - In the terminal, run `./gradlew run` on macOS/Linux or `./gradlew.bat run` on Windows.
1. A Bingus chat window should open. Enter commands in the text field and press Enter or select **Send**.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## AI Declaration
Generative AI (Codex) was used at levels AI-2 to AI-4, depending on the task. Higher levels of AI use were used for chores that required little planning, such as identifying code-style issues and drafting code documentation.
