# Quu

Quu is a desktop chatbot for keeping track of tasks. You type at it, it answers in a chat
window, and it remembers your tasks between sessions.

It is written in Java with a JavaFX front end, and was built as the individual project for
[CS2103](https://nus-cs2103-ay2627-s1.github.io/website/).

**[Read the user guide](https://cookiemunchrr.github.io/ip/)** for what Quu can do and how
to ask it.

## Running Quu

Prerequisites: **JDK 25**.

Download `Quu.jar` from the [releases page](https://github.com/Cookiemunchrr/ip/releases),
then from the folder you put it in:

```
java -jar Quu.jar
```

Quu saves your tasks to `data/Quu.txt`, alongside wherever you ran it from.

## Building from source

```
./gradlew build          # compile, check the coding standard, run the tests
./gradlew run            # launch the GUI
./gradlew runCli         # run the same chatbot as a terminal program
./gradlew clean shadowJar  # build build/libs/Quu.jar
```

## Setting up in IntelliJ

Prerequisites: JDK 25, and a recent version of IntelliJ.

1. Open IntelliJ. If you are not on the welcome screen, click `File` > `Close Project`
   first.
2. Click `Open`, select the project directory, and click `OK`. Accept the defaults for any
   further prompts.
3. Configure the project to use **JDK 25**, as explained
   [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk). In the same dialog, set
   the **Project language level** field to the `SDK default` option.
4. Locate `src/main/java/quu/gui/Launcher.java`, right-click it, and choose
   `Run Launcher.main()`. If the code editor is showing compile errors, try restarting the
   IDE. The Quu window should open.

**Warning:** keep `src/main/java` as the root folder for Java files. Moving them elsewhere
breaks the defaults that Gradle and other tools rely on.

## Acknowledgements

- The project skeleton, the Gradle setup, and the JavaFX starter code that
  `quu.gui.Main`, `quu.gui.MainWindow` and `quu.gui.DialogBox` grew out of come from the
  CS2103 [JavaFX tutorials](https://se-education.org/guides/tutorials/javaFx.html) and the
  [`se-edu/duke`](https://github.com/nus-cs2103-AY2627S1/ip) project template.
- The coding standard followed throughout is
  [SE-EDU's Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
  at Intermediate level, and commits follow
  [SE-EDU's Git conventions](https://se-education.org/guides/conventions/git.html).
- The avatar images are placeholders that ship with the project template.
- Parts of this codebase were written with the assistance of Claude (Anthropic), used as
  described in the module's guidance on AI-assisted development.
