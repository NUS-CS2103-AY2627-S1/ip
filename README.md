# geen

geen is a personal assistant chatbot for tracking todos, deadlines, and events.
Given below are instructions for setting up and running the project.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, run `./gradlew run` from the project root to start the JavaFX GUI. To try the command-line version instead, locate the `src/main/java/duke/Duke.java` file, right-click it, and choose `Run Duke.main()` (if the code editor is showing compile errors, try restarting the IDE). If the command-line setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
     ____ _____ _____ _   _
    / ___| ____| ____| \ | |
   | |  _|  _| |  _| |  \| |
   | |_| | |___| |___| |\  |
    \____|_____|_____|_| \_|
   Hello! I'm geen.
   What can I do for you?
   ____________________________________________________________

   todo read book
   ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
   ____________________________________________________________

   deadline return book /by 2019-10-15
   ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Oct 15 2019)
   Now you have 2 tasks in the list.
   ____________________________________________________________

   event project meeting /from 2019-10-15 /to 2019-10-16
   ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
   Now you have 3 tasks in the list.
   ____________________________________________________________

   mark 1
   ____________________________________________________________
   Nice! I've marked this task as done:
     [T][X] read book
   ____________________________________________________________

   list
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][X] read book
   2.[D][ ] return book (by: Oct 15 2019)
   3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
   ____________________________________________________________

   find book
   ____________________________________________________________
   Here are the matching tasks in your list:
   1.[T][X] read book
   2.[D][ ] return book (by: Oct 15 2019)
   ____________________________________________________________

   schedule 2019-10-15
   ____________________________________________________________
   Schedule for Oct 15 2019:
   1.[D][ ] return book (by: Oct 15 2019)
   2.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
   ____________________________________________________________

   delete 2
   ____________________________________________________________
   Noted. I've removed this task:
     [D][ ] return book (by: Oct 15 2019)
   Now you have 2 tasks in the list.
   ____________________________________________________________

   unmark 1
   ____________________________________________________________
   OK, I've marked this task as not done yet:
     [T][ ] read book
   ____________________________________________________________

   list
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
   ____________________________________________________________

   todo
   ____________________________________________________________
   OOPS!!! A todo needs a description. For example: todo read book
   ____________________________________________________________

   blah
   ____________________________________________________________
   OOPS!!! Sorry, I don't understand that command. Try todo, deadline, event, list, mark, unmark, delete, find, schedule, or bye.
   ____________________________________________________________

   bye
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating an executable JAR

Run the following command from the project root:

```bash
./gradlew shadowJar
```

The cross-platform executable JAR file will be created at:

```text
build/libs/geen.jar
```

To test it as a user would, copy `geen.jar` into an empty folder, open a command
window in that folder, and run:

```bash
java -jar "geen.jar"
```

Do not commit `build/libs/geen.jar`; it is a generated build output. If the JAR
needs to be distributed, attach it to a GitHub release instead.

## Running the GUI

The resizable GUI uses compact, right-aligned user commands and wider geen reply
cards. Replies wrap as the window changes size, and the conversation scrolls to
the newest message. Errors have a red border and a "NEEDS A FIX" label, so they
are distinguishable without relying on colour alone. A failed command stays in
the input field for correction; press Enter or click Send to try again.

To run the JavaFX GUI from the command line, use:

```bash
./gradlew run
```

In IntelliJ, run the `duke.Launcher` class to start the GUI. The original
`duke.Duke` class still runs the command-line version.

To run the command-line version through Gradle, use:

```bash
./gradlew runCli
```

## Optional enhancements

- `A-BetterGui`: asymmetric message cards, labelled errors, clearer spacing and
  colours, responsive wrapping, and a compact command hint without large avatars.
- `A-MoreErrorHandling`: helpful blank-command errors and rejection of events
  ending before they start. Leading/trailing command whitespace is ignored in
  both interfaces. Same-day events remain valid.
- `A-MoreTesting`: additional parser and chatbot regression tests, isolated CLI
  transcripts, and native JavaFX interaction/layout tests.

Run the automated checks with Java 25:

```bash
./gradlew test
python3 test/run-ui-tests.py
./gradlew guiTest
```

`guiTest` needs a graphical display and is separate from the normal headless
build. It writes GUI screenshots to `build/reports/gui-tests/` for visual review.
The CLI transcript tests use temporary folders and do not modify your saved tasks.

## Credits

This project builds on the CS2103T iP starter template. The geen-specific GUI,
error handling, tests, documentation updates, and ASCII banner were written for
this project with AI assistance. The cross-platform JavaFX dependency setup is
adapted from the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html).
No external project code or third-party solution snippets were copied.
