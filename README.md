# Chione

[![Java CI](https://github.com/Lew1sWong/ip/actions/workflows/gradle.yml/badge.svg)](https://github.com/Lew1sWong/ip/actions/workflows/gradle.yml)

Chione is a personal assistant chatbot that keeps track of your tasks. She is
named after the Greek goddess of snow, and speaks like it: calm, a little
poetic, and fond of winter. It is built as the individual project (iP) for
CS2103/T.

```
  ____  _   _  ___   ___   _   _  _____
 / ___|| | | ||_ _| / _ \ | \ | || ____|
| |    | |_| | | | | | | ||  \| ||  _|
| |___ |  _  | | | | |_| || |\  || |___
 \____||_| |_||___| \___/ |_| \_||_____|
```

The full user guide is at <https://lew1swong.github.io/ip/>.

## Features

Chione runs in a window (`./gradlew run`), and also as a plain text
conversation in the console (`./gradlew runText`); both understand the same
commands and answer in the same words.

| Command                                          | What it does                      |
|--------------------------------------------------|-----------------------------------|
| `todo DESCRIPTION`                               | Adds a task with no date          |
| `deadline DESCRIPTION /by WHEN`                  | Adds a task due by a given time   |
| `event DESCRIPTION /from START /to END`          | Adds a task spanning two times    |
| `list`                                           | Shows every task                  |
| `on DATE`                                        | Shows the tasks falling on a day  |
| `find KEYWORD`                                   | Shows the tasks matching a word   |
| `mark INDEX` / `unmark INDEX`                    | Marks a task as done / not done   |
| `delete INDEX`                                   | Removes a task                    |
| `bye`                                            | Ends the conversation             |

### Dates

Write a date as `2019-10-15`, or as `2019-10-15 1800` to include a time of day.
Chione shows it back in a friendlier form, e.g. `Oct 15 2019, 6:00pm`. A date
that does not exist, such as `2019-02-30`, is refused rather than moved to the
nearest real day, and an event has to end after it starts.

`on 2019-10-15` lists everything happening that day: deadlines falling on it, and
events running over it, including ones that started on an earlier day.

### Repeats

Adding a task that is already in the list is refused, so a `todo read book`
typed twice leaves one task, not two. Two tasks count as the same when they are
the same kind, have the same description (ignoring case) and, for a deadline,
are due at the same moment or, for an event, start and end at the same moments.
Whether the existing one is done makes no difference.

### Searching

`find book` shows every task whose description contains that text. The search
ignores case, and matches anywhere in the description rather than whole words
only, so `find oo` finds `read book` too. Only the description is searched — a
deadline is not found by the date it falls on. Use `on` for that.

### Saving

Your tasks are written to `data/chione.txt` after every change and read back when
Chione starts, so the list is still there the next time you run it. The file is
plain text, so you can open and edit it yourself if you ever need to.

Example session:

```
todo borrow book
    ____________________________________________________________
     Settled, like fresh snowfall. I've added:
       [T][ ] borrow book
     Your list now holds 1 task.
    ____________________________________________________________
```

## Building and running with Gradle

The wrapper script fetches the right version of Gradle itself, so nothing needs
to be installed beforehand. On Windows use `gradlew` in place of `./gradlew`.

| Command           | What it does                                      |
|-------------------|---------------------------------------------------|
| `./gradlew run`   | Builds Chione and starts it                       |
| `./gradlew build` | Compiles everything and runs the checks and tests |
| `./gradlew test`  | Runs the tests only                               |
| `./gradlew clean` | Deletes everything that was built                 |

Prerequisites: JDK 25. Nothing else — the wrapper downloads Gradle on first use.

## Running the released JAR

`./gradlew shadowJar` produces `build/libs/chione.jar`, a single file carrying
everything Chione needs. To run it:

1. Put the JAR in a folder of its own.
2. Open a command line in that folder.
3. Run it:

   ```
   java -jar chione.jar
   ```

The quotes in `java -jar "chione.jar"` are only needed if the path contains a
space or a character the shell treats specially.

Chione keeps its tasks in `data/chione.txt` **relative to the folder you run it
from**, so the JAR creates that folder beside itself the first time you add a
task. Running it from two different folders gives you two separate task lists.

The JAR is not kept in this repository — it is built from the source, and
published on the [releases page](../../releases).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/chione/Chione.java` file, right-click it, and choose `Run Chione.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the banner above followed by a greeting.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

* The window is built with JavaFX following the
  [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
  `Launcher`, `Main`, `MainWindow`, `DialogBox` and the two FXML layouts
  started from the tutorial's code and were adapted from there; the
  stylesheet, the resizable layout, the error bubbles and the circular
  avatars were added on top.
* The project structure, the Gradle setup and the Checkstyle configuration
  come from the CS2103/T iP starter repository, which also follows the
  [SE-EDU guides](https://se-education.org/guides/).
* The code in this project was written with Claude Code (Anthropic), used at
  level AI-5: the AI implemented each increment from the requirements given in
  the course website, and I reviewed the resulting code, asked for explanations
  of the design decisions behind it, and tested the behaviour before committing.
  The Week 6 increments (A-Personality, A-BetterGui, A-MoreErrorHandling) were
  done the same way, starting from the AI's suggestions for a personality and
  for GUI improvements, from which I chose the ones to keep.
