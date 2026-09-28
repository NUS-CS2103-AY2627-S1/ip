# Kotha project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/kotha/Kotha.java` file, right-click it, and choose `Run Kotha.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   #   #   ###   #####  #   #    #
   #  #   #   #    #    #   #   # #
   ###    #   #    #    #####  #####
   #  #   #   #    #    #   #  #   #
   #   #   ###     #    #   #  #   #
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

### Third-party libraries

Kotha uses the following third-party libraries and tools:

- [JUnit 5](https://junit.org/junit5/) for automated testing.
- [OpenJFX](https://openjfx.io/) for the graphical user interface.
- [Gradle](https://gradle.org/) for build and dependency management.

### AI-assisted work

OpenAI Codex was used throughout the development of this project to assist with:

- Refactoring task collection and task mutation logic into dedicated classes such as `TaskList` and `TaskOperations`.
- Implementing task features including keyword search and constraint tasks with `/after` and `/afterdate` triggers.
- Improving command validation, date/time handling, duplicate detection, storage recovery, and user-facing error handling.
- Adding Java assertions for important invariants.
- Implementing Kotha's split royal/rude personality, response messages, deterministic tests, GUI styling, and royal-mode typography.
- Setting up and explaining the Gradle build, Java package structure, and application entry points.
- Adding and expanding automated JUnit tests, including isolated storage tests and command-engine tests.
- Drafting and improving the User Guide and other project documentation.

The final implementation was reviewed and adapted by the project author.

### Image credits

The Kotha bot icon uses an internet famous meme image of an infant with an enlarged eyes filter.

- **Source/Author:** Anonymous / Viral internet culture (Original creator unknown)
- **Licence:** Unlicensed / Public Domain / Fair Use (Used strictly as a visual placeholder in a non-commercial product)
- **Note:** Every effort was made to trace the original creator. If you are the owner of this image and wish for its removal or proper attribution, please contact the repository maintainers.
