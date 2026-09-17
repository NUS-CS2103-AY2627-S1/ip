# Nori

Nori is a calm desktop task-tracking chatbot built for the CS2103/T individual project.

For usage instructions, see the [Nori User Guide](docs/README.md).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/nori/Launcher.java`, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE).

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

The project is based on the course-provided Duke starter project and uses JavaFX.

OpenAI Codex was used extensively for implementation, refactoring, tests, documentation, and Git workflow. The project author is responsible for reviewing the resulting changes before submission.
