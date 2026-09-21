# Ted

Ted is a cheeky teddy bear chatbot that keeps track of your todos, deadlines and events.

* To use Ted, download `ted.jar` from the [latest release](https://github.com/trigg770/ip/releases/latest)
  and read the [user guide](https://trigg770.github.io/ip/).
* To work on Ted's code, follow the instructions below to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/ted/Ted.java` file, right-click it, and choose `Run Ted.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
   Oh good, you're here. I'm Ted: professional teddy bear, part-time life coach.
   What do you need to get done today?
   ____________________________________________________________
   ```
1. To open Ted's window instead, run `src/main/java/ted/Launcher.java` the same way, or run `./gradlew run` from the project folder.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

* Libraries: [JavaFX](https://openjfx.io/) for the GUI and [JUnit 5](https://junit.org/) for the tests.
  The build uses Gradle with the Shadow, Checkstyle and JaCoCo plugins. All of these are also used by
  the course's [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) project.
* Ted's window follows the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
  `Launcher`, `Main`, `MainWindow` and `DialogBox`, and their FXML files, are adapted from it.
* Ted's picture is [Teddy Bear](https://openclipart.org/detail/2694) by Machovka, from Openclipart,
  released into the public domain.
* The user guide's layout, including its notes on command formats and its command summary, follows the
  [AddressBook-Level3 user guide](https://se-education.org/addressbook-level3/UserGuide.html).
* AI tools, all used by trigg770:
  * [Claude Code](https://www.anthropic.com/claude-code) was used throughout the project, from the first
    increment to the last. Working from trigg770's instructions, it wrote most of the code, tests and
    documentation, and drafted most commit messages. Most commits it helped write end with a
    `Co-Authored-By: Claude` line.
  * [OpenAI Codex](https://openai.com/codex/) was used in Weeks 3 and 6 to help write and review code,
    and to fix Javadoc, naming and spelling to follow the coding standard.
  * Alongside the tools, trigg770 directed the work, wrote some of the code, came up with Ted's
    personality, and reviewed the code both manually and with the tools.
