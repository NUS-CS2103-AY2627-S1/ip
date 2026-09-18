# Winston

Winston is a greenfield Java chatbot project: a task tracker with the manner of
an unflappable butler. It runs as a JavaFX window, and the original console
version is still there for anyone who prefers it. Given below are instructions
on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/seedu/mattchatbot/MattChatBot.java` file, right-click it, and choose `Run MattChatBot.main()` (if the code editor is showing compile errors, try restarting the IDE). The class keeps its original name; Winston is the name the product goes by. If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
   W   W  IIIII  N   N   SSS   TTTTT   OOO   N   N
   W   W    I    NN  N  S        T    O   O  NN  N
   W W W    I    N N N   SSS     T    O   O  N N N
   WW WW    I    N  NN      S    T    O   O  N  NN
   W   W  IIIII  N   N   SSS     T     OOO   N   N

   Good day. Winston, at your service.
   How may I be of assistance?
   ____________________________________________________________
   ```
   Winston then waits for your commands, and tracks three kinds of task --
   `todo`, `deadline` and `event`. Dates are written as `yyyy-MM-dd`, with an
   optional `HHmm` time. `mark N` / `unmark N` change a task's status,
   `update N` edits one in place, `delete N` removes one, `list` shows
   everything, `on <date>` shows what falls on one day, `find <keyword>`
   searches descriptions, and `bye` exits:
   ```
   todo read book
   ____________________________________________________________
   Very good. I have noted it:
     [T][ ] read book
   You now have 1 entry in your list.
   ____________________________________________________________
   deadline return book /by 2019-10-15
   ____________________________________________________________
   Very good. I have noted it:
     [D][ ] return book (by: Oct 15 2019)
   You now have 2 entries in your list.
   ____________________________________________________________
   event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
   ____________________________________________________________
   Very good. I have noted it:
     [E][ ] project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)
   You now have 3 entries in your list.
   ____________________________________________________________
   on 2019-10-15
   ____________________________________________________________
   Your engagements for Oct 15 2019:
   1.[D][ ] return book (by: Oct 15 2019)
   2.[E][ ] project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)
   ____________________________________________________________
   mark 1
   ____________________________________________________________
   Excellent. I have marked it complete:
     [T][X] read book
   ____________________________________________________________
   list
   ____________________________________________________________
   Your list, as it stands:
   1.[T][X] read book
   2.[D][ ] return book (by: Oct 15 2019)
   3.[E][ ] project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)
   ____________________________________________________________
   find book
   ____________________________________________________________
   The entries matching your enquiry:
   1.[T][X] read book
   2.[D][ ] return book (by: Oct 15 2019)
   ____________________________________________________________
   delete 2
   ____________________________________________________________
   Consider it struck from the list:
     [D][ ] return book (by: Oct 15 2019)
   You now have 2 entries in your list.
   ____________________________________________________________
   bye
   ____________________________________________________________
   Very good. I shall be here when you return.
   ____________________________________________________________
   ```

   If a command is not understood, or is missing a part it needs, Winston says
   what was wrong and how to write it instead:
   ```
   deadline /by 2019-10-15
   ____________________________________________________________
   A deadline wants a description before the /by. Perhaps: deadline return book /by 2019-10-15
   ____________________________________________________________
   ```

Your tasks are saved automatically to `data/mattchatbot.txt` whenever the list
changes, and loaded again the next time you start Winston. The file keeps its
original name so that lists saved before the renaming still load. The file is created
on first use, so there is nothing to set up.

## Building and running with Gradle

```
./gradlew run
```

That opens the chat window. Type a command in the box at the bottom and press
Enter or click Send; the conversation appears above it. Your instructions sit
in a compact bubble on the right, while Winston's replies run the full width
of the window, marked by a brass rule down the left edge. `bye` closes the
window after showing its farewell. Every command listed above works the same
way as it does in the console.

The window is built with JavaFX. Its dependencies are declared for Windows,
Linux and both kinds of Mac, so the same JAR runs on any of them, and the
program starts from a small `Launcher` class rather than the JavaFX
`Application` subclass itself, which is what lets it run from a packaged JAR.

To use the console version instead:

```
./gradlew build
java -cp build/classes/java/main seedu.mattchatbot.MattChatBot
```

`./gradlew build` compiles the code, runs the tests and runs Checkstyle.
Gradle downloads the right Gradle version itself the first time, so nothing
needs installing beyond a JDK 25.

## Checking the coding style

```
./gradlew checkstyleMain checkstyleTest
```

Checkstyle enforces the SE-EDU Java coding standard using the configuration in
`config/checkstyle`. It also runs as part of `./gradlew build`, so a style
violation fails the build rather than waiting to be spotted in review.

## Measuring test coverage

```
./gradlew jacocoTestReport
```

That writes an HTML report to `build/reports/jacoco/test/html/index.html`.

The GUI classes are left out of the figure. They are tested by running the
program rather than by JUnit, so counting them would make the number describe
how much of the program has a window rather than how much of the testable code
is tested.

What remains uncovered is, deliberately, of three kinds: the failure branch of
an `assert`, which never runs while the assertion holds; two defensive branches
the compiler requires but nothing can reach; and the two entry points that read
the real save file, which a test has no business touching.

## Packaging as a JAR

```
./gradlew shadowJar
```

This produces `build/libs/mattchatbot.jar`, which bundles its dependencies,
JavaFX included. Copy it into a folder of its own and run it with:

```
java -jar "mattchatbot.jar"
```

The window opens, and the JAR creates its `data` folder alongside itself on
first use.

## Running from the command line

Compiling by hand needs JavaFX on the classpath, so `./gradlew run` is the
easier route. The console version has no such dependency and still compiles on
its own:

```
javac -d bin $(find src/main/java -name '*.java' -not -path '*/gui/*' -not -name 'Launcher.java')
java -cp bin seedu.mattchatbot.MattChatBot
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
