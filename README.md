# Bags

Bags is a Java desktop task manager for managing to-dos, deadlines, and
events. See the [Bags User Guide](https://clairep246.github.io/ip/) for
installation and command usage.

## Setting up in IntelliJ IDEA

Prerequisite: JDK 25. Update IntelliJ IDEA to the most recent version.

1. Open IntelliJ IDEA. If you are not on the welcome screen, click `File` >
   `Close Project` first.
2. Open the project in IntelliJ IDEA:
   1. Click `Open`.
   2. Select the project directory, then click `OK`.
   3. Accept the defaults for any further prompts.
3. Configure the project to use **JDK 25**, as explained in the
   [JetBrains JDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).
   In the same dialog, set **Project language level** to `SDK default`.

To launch the graphical interface, run `./gradlew.bat run` from the project
root on Windows, or `./gradlew run` on macOS/Linux.

**Warning:** Keep `src/main/java` as the Java source root. Do not rename these
folders or move Java files outside this path, as Gradle and other tools expect
this standard project layout.

## AI Assistance

I used OpenAI Codex as an AI-assisted development tool throughout this project.
All AI-generated suggestions and code were reviewed, adapted, and tested before
being included.

Codex assisted with the following work:

- Completing code and diagnosing coding errors.
- Generating and improving JUnit test cases.
- Drafting Javadoc comments, which I then reviewed and refined.
- Creating the JavaFX GUI and refactoring classes to support FXML-based GUI
  development.
- Adding assertions and refactoring code to improve code quality.
- Implementing extensions that improve the GUI and test quality.
- Refactoring lengthy or deeply nested methods to improve readability.
