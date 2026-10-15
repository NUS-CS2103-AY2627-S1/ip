# Magnus

Magnus is a chatbot developed as a greenfield Java project. Given below are instructions on how to use it.

See the [published user guide](https://nguyen-phuc-thang.github.io/ip/) for installation and all commands.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/magnus/Launcher.java`, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, the Magnus chat window should open with this greeting:
   ```
   Greetings! I'm Magnus, your task tactician.
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating an executable fat JAR

Ensure that JDK 25 is active, then run the Shadow plugin's `shadowJar` task from the project root:

- macOS/Linux: `./gradlew shadowJar`
- Windows: `gradlew.bat shadowJar`

The task creates `build/libs/magnus.jar`. This is a fat JAR: it contains the application and its runtime dependencies, and its manifest identifies `magnus.Launcher` as the entry point for the graphical interface.

Run it from the project root with:

```shell
java -jar build/libs/magnus.jar
```

Running from the project root also ensures that Magnus reads and writes its task data at `data/magnus.txt`.

## Acknowledgement of AI use

AI assistance was used during the development of Magnus at a level between **AI-4 (Think and compare)** and **AI-5 (AI codes, you review)**. This involved considering implementation approaches before comparing them with AI-generated solutions, as well as using AI to generate implementations followed by human review of the code, tests, and application behaviour.

AI-generated work was evaluated and refined where needed to meet the project requirements. Responsibility for the final implementation and understanding of the code remains with the project author.
