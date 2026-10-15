# PanPan

PanPan is a task-tracking chatbot with rather too much enthusiasm. It keeps
todos, deadlines and events for you, remembers them between runs, and talks
back in its own voice while doing it.

It runs two ways from the same code: a JavaFX chat window, and a plain console
loop for when you would rather stay in the terminal.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/pan/Pan.java` file, right-click it, and choose `Run Pan.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____      _     _   _   ____      _     _   _ 
   |  _ \    / \   | \ | | |  _ \    / \   | \ | |
   | |_) |  / _ \  |  \| | | |_) |  / _ \  |  \| |
   |  __/  / ___ \ | |\  | |  __/  / ___ \ | |\  |
   |_|    /_/   \_\|_| \_| |_|    /_/   \_\|_| \_|
   ```

To run the windowed version instead, use `./gradlew run`.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

- The project skeleton, the Gradle setup and `CONTRIBUTORS.md` come from the
  [se-education.org](https://se-education.org/) `ip` template.
- The JavaFX front end — `Launcher`, `Main`, `MainWindow`, `DialogBox` and the
  `fx:root` pattern in the FXML files — follows the se-education
  [JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html),
  Parts 1 to 4.
- The avatar images in `src/main/resources/images/` were sourced from the web;
  the original creator is not known.
- The Java coding conventions and the Git commit-message conventions followed
  here are the [se-education guides](https://se-education.org/guides/).
- Some code in this project was written with the assistance of Claude
  (Anthropic), reviewed and accepted by me.

See [docs/README.md](docs/README.md) for the user guide.
