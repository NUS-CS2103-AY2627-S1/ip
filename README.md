# ET project template

This is a project template for a greenfield Java project. Its chatbot is named _ET_. Given below are instructions on how to use it.

## Acknowledgement of AI assistance

Sidharth Bairy (GitHub: `sidharthbairy`) used **OpenAI Codex** throughout the development of ET.
The assistance included generating and editing Java code, suggesting designs and refactorings,
debugging issues, configuring Gradle and JAR packaging, and developing the JavaFX interface
and chatbot personality. Codex also helped write and update JUnit tests, console UI test plans,
and project documentation, including this acknowledgement, and run builds and tests.

The extent of use included direct implementation of project features and tests, as well as
explanations, code review, and assistance with Git workflows. AI-generated and AI-assisted
work is therefore present across the implementation, tests, and documentation. Sidharth Bairy
remains responsible for the submitted work and its correctness.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/et/ET.java`, right-click it, and choose `Run ET.main()`.
   ET should open in a JavaFX chat window. If the code editor shows compile errors, try reloading the Gradle project
   or restarting the IDE.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the application JAR

The project uses the Shadow Gradle plugin to create an executable **fat JAR**: a single JAR that contains ET and all of its runtime dependencies.

1. From the project root, build the JAR:
   ```bash
   ./gradlew shadowJar
   ```
   On Windows, use `gradlew.bat shadowJar`.
1. Find the generated file at `build/libs/et.jar`.
1. Run ET with Java 25:
   ```bash
   java -jar build/libs/et.jar
   ```

To start the application directly through Gradle, run `./gradlew run`.

You can also use `./gradlew clean shadowJar` when you want Gradle to remove previous build output before creating a fresh JAR.
