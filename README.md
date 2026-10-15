# Orion project

This is a greenfield Java project named _Orion_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/orion/Orion.java` file, right-click it, and choose `Run Orion.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```text
   _______________________________________________________
     ____       _
    / __ \_____(_)___  ____
   / /_/ / ___/ / __ \/ __ \
   \____/_/  /_/\____/_/ /_/
   
   Hello! I'm Orion, your friendly chatbot.
   What can I do for you?
   _______________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements
* GUI skeleton and JavaFX `<fx:root>` architecture adapted from the [se-edu/duke JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).

* This project heavily utilized Google Gemini to assist with various stages of development. Specifically, AI was used for:
   * Designing and troubleshooting all JavaFX-related UI components, including custom CSS styling and layout formatting.
   * Generating and expanding automated JUnit test suites to ensure code reliability.
   * Refactoring the core logic to better adhere to Software Engineering best practices.