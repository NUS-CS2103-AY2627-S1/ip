# Bob

Bob is a desktop task manager with a Minion personality. You chat with King Bob in a JavaFX window, typing
commands such as `todo read book` or `deadline return book /by 2026-12-02 1800`, and Bob keeps your list saved
between sessions.

## Features

- Add todos, deadlines and events, with dates and optional times.
- Mark, unmark, edit and delete tasks.
- List the tasks on, before or after a date, or the unfinished ones with the soonest dates.
- Find tasks by a word in their description.
- Catch mistakes such as dates that do not exist, events that end before they start, and duplicate tasks.
- Save tasks automatically, keeping a backup copy if the save file turns out to be damaged.

## User guide

See the [Bob User Guide](https://adwaithanoop.github.io/ip/) for the complete command reference.

## Setting up in IntelliJ

Prerequisites: JDK 25, update IntelliJ to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bob/Launcher.java` file, right-click it, and choose `Run Launcher.main()`
   (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a window titled
   `King Bob` opens with Bob's greeting.<br>
   To use the console version instead, do the same with `src/main/java/bob/Bob.java`. After the King Bob banner,
   you should see:
   ```
       ____________________________________________________________
             __ __  _                     ____        __
            / //_/ (_) ____   ____       / __ )____  / /_
           / ,<   / / / __ \ / __ \     / __  / __ \/ __ \
          / /| | / / / / / // /_/ /    / /_/ / /_/ / /_/ /
         /_/ |_|/_/ /_/ /_/ \__, /    /_____/\____/_.___/
                           /____/
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⠤⠔⠒⠒⠛⠛⠓⣒⣶⡦⠤⠤⠤⠤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠖⠋⠁⠀⠀⠀⠀⠀⠀⣠⢞⡝⣡⣴⣶⠶⢶⣷⣶⣝⠳⣄⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠖⠁⢀⡤⢶⠶⠿⠿⠶⣦⣤⡰⢣⢿⣾⡟⠁⠀⠀⠀⠈⠉⠻⡷⡜⣆⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠜⠁⣠⠞⣥⣺⣵⡶⠿⠿⣶⣦⣍⠳⡏⣾⠯⠁⣰⣶⣶⣦⠀⠀⠀⢹⢷⢸⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⣴⢃⣾⣿⠿⠉⠀⠀⠀⠈⠉⢻⣇⡁⢻⡀⠀⢿⣿⣿⡽⠀⠀⠀⢸⣿⣸⠃⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡏⠀⣸⡇⣼⣿⡯⠀⠀⣴⣿⣽⣷⠀⠀⢹⣷⠈⢻⡄⠀⠉⠉⠀⠀⠀⢠⣿⢣⣿⡄⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⠀⣿⣿⣧⢿⣿⠀⠀⠀⠻⣿⣿⡽⠀⠀⢸⣿⢳⣦⣙⠦⢄⣀⣀⣠⠾⣻⣵⡿⠁⢧⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣣⣾⣿⣿⣿⣾⡝⢿⡄⠀⠀⠀⠀⠀⠀⢀⣾⣣⣿⢿⢿⣿⣶⣒⣒⡿⠿⠛⠁⠀⠀⠘⡄⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿⣤⡙⢦⣀⣀⣀⣀⡤⣿⣵⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢫⠁⠀⠀⠈⠈⠛⠿⣿⣿⣒⣖⣒⣲⠿⠟⠋⠀⠀⠀⠀⠀⠀⢀⡄⠀⠀⠀⠀⠀⠀⠀⠘⣄⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡓⣦⣀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠐⠦⢤⣤⣀⣀⣤⠤⠖⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⣴⣿⡄⠀⠀
         ⠀⠀⠀⠀⢀⣤⣶⣿⣿⣷⣦⣄⠙⢿⣮⣽⡷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⣇⠀⠀
         ⠀⠀⢀⣾⣿⡟⡩⣽⣿⣿⣿⣿⣳⡀⠈⠻⢷⣮⢹⣷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣀⣀⣀⣀⣠⣶⣿⣿⡟⢹⠘⡆⠀
         ⠀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠀⠀⠀⠙⠻⣿⣿⣿⣶⣶⣶⡶⢶⣶⣾⣟⣿⠟⡿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠀⢸⠀⢳⠀
         ⠘⣿⣿⣿⣿⣿⣟⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⡿⠁⠞⣺⠬⠧⠭⠽⠵⠶⠿⡿⣿⣯⣿⢿⣿⣿⢁⣡⣴⣿⣶⣼⡀
         ⠀⠈⠙⢿⣿⣆⣿⣿⣝⣿⣿⣿⣿⣝⣦⠀⠀⠀⠀⠀⢠⣿⠙⠛⠒⠀⠠⣿⠄⠀⠀⠀⠀⠀⠀⢁⣻⡼⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⡇
         ⠀⠀⠀⠀⠙⠛⣾⣿⣿⣿⡿⢿⣿⣿⣿⣧⣤⣄⣀⢀⣸⣟⣀⠀⠀⠀⠀⢻⡀⠄⠀⠀⠀⠀⠀⠔⡿⠛⣿⢿⣿⣿⣿⣿⣿⣿⠟⠋⠀
         ⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠈⢩⡿⡐⠈⠁⠀⠀⠀⠀⠙⠲⠤⠤⠥⠧⠴⠿⠓⠀⠈⠜⢿⣿⣿⣿⣿⠃⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠉⢻⣿⣿⣿⣿⣿⣿⣿⣿⣷⡒⠋⠼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⣿⢿⡿⣿⣿⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⣻⡿⢦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠁⠐⡯⠯⣿⡿⠃⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⠏⠘⣿⣿⣿⡯⠙⠒⠦⣄⣄⣀⣐⣀⡄⠀⠐⠀⣤⡞⣩⣀⠄⠀⠀⠖⠀⢈⣁⡴⠛⠁⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠀⠀⠈⠙⠋⠁⠀⠀⠀⠀⢹⣿⢿⣿⣿⣿⡟⠛⣿⣿⣿⣿⣿⣿⠟⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣾⣿⣿⣿⠀⠀⣿⣿⣿⣿⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣿⣿⣿⣿⠁⠀⢠⣿⣿⣿⣿⣿⣶⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
         ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠛⠛⠛⠛⠋⠀⠉⠉⠉⠛⠛⠛⠛⠛⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     Bello! Me Bob!
     Wat yu want Bob do? Banana?
    ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or
move Java files to another folder outside of this folder path), as this is the default location some tools (e.g.,
Gradle) expect to find Java files.

## Creating and running the fat JAR

From the project root, create a fat JAR containing Bob and its runtime dependencies, including JavaFX:

```shell
./gradlew shadowJar
```

On Windows, use `gradlew.bat shadowJar` instead. The generated JAR is located at `build/libs/bob.jar`. Run it from
the project root with:

```shell
java -jar build/libs/bob.jar
```

Bob saves tasks automatically in `data/bob.txt`, relative to the folder from which the JAR is run. If Bob finds
lines in that file it cannot read, it copies the file to `data/bob.txt.bak` before changing anything.

A ready-made `bob.jar` can also be downloaded from the [latest release](https://github.com/adwaithanoop/ip/releases)
and run the same way, with JDK 25. The JAR runs on Windows and Linux with an Intel or AMD processor, and on Apple
silicon Macs. It does not start on Intel Macs, as it bundles only one JavaFX build per operating system.

## Building and testing

Run the following command from the project root (`gradlew.bat check` on Windows):

```shell
./gradlew check
```

This runs the JUnit tests. To start the chat window without building the JAR, run `./gradlew run`.

The console output is tested separately against the cases in [`test/ui-test-plan.md`](test/ui-test-plan.md):

```shell
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

GitHub Actions runs `./gradlew check` on Windows, macOS and Linux for every push and pull request.

## Acknowledgements and reused code

* **Mouse wheel scrolling in the chat window:** the fix in `bob.ui.MainWindow`, which keeps the newest
  message in view without binding the scroll position, comes from the forum post
  [Enabling mouse wheel & trackpad scrolling in JavaFX ScrollPane](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/160)
  by Kieran M ([@Kimame04](https://github.com/Kimame04)). Thanks Kieran!
* **Chat avatars:** `DaBob.png` and `DaUser.png` in `src/main/resources/images/` are properties of
    Universal Studios/Illumination Entertainment
* **ASCII Art** Bob ASCII art was generated using this [online tool.](https://emojicombos.com/)
* **Use of AI:** AI was used in parts of this project to enhance my learning and speed up development process.
    The level of AI use hovered around AI-4, sometimes going to AI-3 and AI-5. I ensured that the changes made
    are intentional, and ran test cases to prevent regression. Reused code was also credited appropriately.
