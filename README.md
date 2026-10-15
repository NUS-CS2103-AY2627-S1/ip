# Kiwi

Kiwi is a **desktop chatbot** for managing to-dos, deadlines, and events.
Type a command in the window and press Enter (or **Send**) — Kiwi keeps your list on disk so it is still there the next time you open the app.

![The Kiwi chat window after listing tasks and adding a to-do](docs/Ui.png)

* **[User Guide](docs/README.md)** — how to install Kiwi and use every command
* **Setting up** (below) — how to run Kiwi from source in IntelliJ

## Features

* Add **to-dos**, **deadlines**, and **events**
* **List**, **mark**, **unmark**, **find**, and **delete** tasks
* See what falls on a date (`on`) and find a free work-day slot (`free`)
* Dates use `yyyy-MM-dd` (for example `2019-12-02`) and are shown as `MMM dd yyyy`
* Tasks are saved automatically to `data/kiwi.txt`

## Setting up in IntelliJ

Prerequisites: JDK 25, and a recent version of IntelliJ.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first).
1. Open the project into IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [IntelliJ's SDK guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a window titled **Kiwi** should open. Chat replies appear only in that window, not in the IntelliJ Run console.

Alternatively, from a terminal in the project directory:

```bash
./gradlew run
```

Or, after building `build/libs/kiwi.jar`:

```bash
java -jar build/libs/kiwi.jar
```

**Warning:** Keep the `src/main/java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

This project was bootstrapped from the [Duke](https://github.com/se-edu/duke) template created by the [SE-EDU initiative](https://se-education.org).
