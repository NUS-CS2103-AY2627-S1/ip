# Blud project template

This is a project template for a greenfield Java project. Its chatbot is named _Blud_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/blud/Blud.java` file, right-click it, and choose `Run Blud.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the chatbot name as the output:
   ```
     ____  _            _
    | __ )| |_   _  ___| |
    |  _ \| | | | |/ __| |
    | |_) | | |_| | (__|_|
    |____/|_|\__,_|\___(_)
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

Blud reuses and builds on the following resources:

- Third-party libraries, including JavaFX and JUnit, as declared in `build.gradle`.
- SE-EDU course materials, including the JavaFX tutorials, Java coding conventions,
  and Git conventions.
- ChatGPT Codex was used only during the later half of the project to refine and
  update code with features for which AI usage was explicitly specified as allowable
  in the project tasks. I specified my requirements clearly and evaluated the AI's
  decisions critically before accepting any suggestions or allowing modifications.
  This assistance covered the `find` command, the `sort` command, the JavaFX GUI,
  more robust JUnit tests, Java code-convention refactoring, and refactoring code
  into organized packages.
