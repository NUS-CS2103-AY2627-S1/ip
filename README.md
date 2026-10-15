# Elsa

This is a greenfield Java project for a chatbot named Elsa. It started from the CS2103T project template. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/elsa/gui/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, the Elsa window opens and greets you.

Elsa also has a text interface, which is what the tests in `test/ui-test-plan.md` drive. Run it with `./gradlew runText` (on Windows, `gradlew.bat runText`), and it greets you with the banner below instead of opening a window:

```
 _____ _
|  ___| |___  __ _
| |__ | / __|/ _` |
|  __|| \__ \ (_| |
|_____|_|___/\__,_|
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Use of AI tools

Claude Code (Anthropic), running the Claude Opus 5 model, was used by Koh Yu Jian while working on this project. Its use was widespread rather than confined to a few places, so it is cited here rather than in comments beside individual methods.

It was used to:

* discuss the design of each increment, and the trade-offs between approaches, before any code was written;
* write and revise code under `src/main/java`, along with its Javadoc and explanatory comments;
* write the JUnit tests under `src/test/java` and the text-based UI test cases in `test/ui-test-plan.md`;
* draft commit messages;
* run Checkstyle, the JUnit suite and the UI test suite, and work out what their failures meant.

The author reviewed each suggestion before keeping it, directed the design decisions, and made every commit.
