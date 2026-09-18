# Alice

Alice is a task manager for people who would rather type than click. She keeps
track of todos, deadlines and events, remembers them between runs, and is
faintly put out about having to do any of it.

She runs either as a JavaFX desktop app, with the conversation on the left and
a live list of your tasks on the right, or as a plain command-line program.
Both front ends share exactly the same command handling, so they can never
disagree about what a command does.

```
Oh. It's you.
Well? What do you want me to keep track of?
```

## Features

| Command | What it does |
|---|---|
| `todo <description>` | Adds a task with no date attached. |
| `deadline <description> /by <d/M/yyyy HHmm>` | Adds a task due at a given time. |
| `event <description> /from <d/M/yyyy HHmm> /to <d/M/yyyy HHmm>` | Adds a task spanning a period. |
| `list` | Shows every task, numbered. |
| `find <keyword> [more keywords]` | Shows tasks matching any of the keywords. |
| `mark <number>` / `unmark <number>` | Marks a task done or not done. |
| `delete <number>` | Removes a task. |
| `snooze <number> <days>` | Pushes a deadline back by a number of days. |
| `bye` | Exits. |

Tasks are saved to `data/alice.txt` after every change, and reloaded on start.
If that file cannot be written, Alice says so rather than pretending the change
was saved; if a line in it cannot be read, she skips that line and tells you
which one.

## Setting up in IntelliJ

Prerequisites: JDK 25, and a recent version of IntelliJ.

1. Open IntelliJ. If you are not on the welcome screen, close the current
   project first with `File` > `Close Project`.
1. Click `Open`, select this project's directory, and accept the defaults for
   any prompts that follow.
1. Configure the project to use **JDK 25** (not another version), as described
   in [the IntelliJ documentation](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).
   Set the **Project language level** field to the `SDK default` option in the
   same dialog.
1. Locate `src/main/java/alice/Launcher.java`, right-click it, and choose
   `Run Launcher.main()` to start the GUI. To run the command-line version
   instead, do the same with `src/main/java/alice/Alice.java`.

**Warning:** keep `src/main/java` as the root folder for Java files. Gradle and
other tools expect to find them there, so moving or renaming those folders will
break the build.

## Running it from the command line

```
./gradlew run          # launch the GUI, with assertions enabled
./gradlew check        # run the tests and the checkstyle rules
./gradlew build        # the above, plus build/libs/alice.jar
```

## Acknowledgements

* This project is built on the [se-edu iP template](https://github.com/se-edu/ip)
  used by CS2103/T.
* **TODO BEFORE SUBMITTING - replace this line:** state where the pixel art in
  `src/main/resources/images/DaAlice.png` came from, with a link and its
  licence, or say that you drew it yourself.
* Parts of this project were written with the help of an AI assistant, used as
  described in the course's policy on AI use.
