# Walter

Walter is a JavaFX task-management chatbot. It manages todos, deadlines, events, and saved places through a conversational interface.

## Setting up in IntelliJ

Prerequisites: JDK 25 and the most recent version of IntelliJ.

1. Open IntelliJ. If you are not on the welcome screen, click `File` > `Close Project` to close the existing project first.
2. Open the project in IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions), as explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. Run `./gradlew run` from the project root. On Windows, use `gradlew.bat run`.
   Alternatively, run the `walter.Launcher` class from IntelliJ.

   If setup is correct, the JavaFX window opens with the title **Walter**.

> **Warning:** Keep the `src/main/java` folder as the root folder for Java files. Do not rename these folders or move Java files outside this path, as this is the default location where tools such as Gradle expect to find Java files.

## Acknowledgement of AI Usage

There is extensive AI usage to produce the work as shown here. Codex is prompted to generate the code for each increment. Manual testing of the code is done to ensure Codex has implemented the features required in each increment properly. The text generated for each command is modified from Codex to make it fit the intended personality for the chatbot to have, which is to be succinct and instructive. I looked through the code base after the increments and found no major problems.
