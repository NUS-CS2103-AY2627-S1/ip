# Kenbot

Kenbot is a chatbot that keeps track of your tasks. It opens as a JavaFX window,
and the same commands work in a terminal too. It remembers your tasks between
runs by saving them to a text file, so you can close it and pick up where you
left off.

It handles three kinds of task: plain to-dos, deadlines with a due date, and
events that run between two dates. You can mark them done, delete them, search
them by keyword, and label them with tags like `#cs2103` so related tasks can be
pulled up together.

**[Read the user guide](https://ken2k7.github.io/ip/)** for every command, with
worked examples of each.

```
 _  __          _           _
| |/ /___ _ __ | |__   ___ | |_
| ' // _ \ '_ \| '_ \ / _ \| __|
| . \  __/ | | | |_) | (_) | |_
|_|\_\___|_| |_|_.__/ \___/ \__|

                 Kenbot
```

## Running it

Prerequisites: **JDK 25**. The Gradle wrapper is included, so Gradle itself does
not need to be installed.

Start the app (opens the window):

```
./gradlew run
```

Start it in the terminal instead:

```
./gradlew build && java -cp build/classes/java/main kenbot.Kenbot
```

Run the tests:

```
./gradlew test
```

Build a standalone JAR, which lands in `build/libs/kenbot.jar`:

```
./gradlew shadowJar
```

That JAR carries everything it needs, so it runs on its own from any folder:

```
java -jar kenbot.jar
```

Kenbot keeps its tasks in `data/Kenbot.txt`, created next to wherever you start
it from. There is no need to make the folder yourself.

## Setting up in IntelliJ

1. Open IntelliJ (if you are not on the welcome screen, click `File` >
   `Close Project` first).
2. Click `Open`, select the project directory, and click `OK`. Accept the
   defaults for any further prompts.
3. Configure the project to use **JDK 25** (not another version), as explained
   [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk). In the same
   dialog, set **Project language level** to `SDK default`.
4. Locate `src/main/java/kenbot/Kenbot.java`, right-click it, and choose
   `Run Kenbot.main()`. If the editor is showing compile errors, try restarting
   the IDE first. A correct setup prints the banner shown above.

**Warning:** keep `src/main/java` as the source root for Java files. Do not
rename those folders or move Java files outside that path, since it is where
Gradle and other tools expect to find them.
