# Goat

Goat is a task list you talk to. It keeps todos, deadlines and events, in a
terminal or in a window, and remembers them between runs.

```
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
find book
sort date
```

Download it from [the latest release](https://github.com/8ryanl33/ip/releases),
or build it yourself with the instructions below. The
[user guide](https://8ryanl33.github.io/ip/) covers every command.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/goat/Goat.java` file, right-click it, and choose `Run Goat.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
     ____   ___      _     _____ 
    / ___| / _ \    / \   |_   _|
   | |  _ | | | |  / _ \    | |  
   | |_| || |_| | / ___ \   | |  
    \____| \___/ /_/   \_\  |_|  
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Commands

| Command | Example |
| --- | --- |
| Add a todo | `todo read book` |
| Add a deadline | `deadline return book /by 2019-12-02 1800` |
| Add an event | `event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600` |
| List everything | `list` |
| Search descriptions | `find book` |
| Sort the list | `sort date` or `sort name` |
| Mark done / not done | `mark 1` / `unmark 1` |
| Delete | `delete 1` |
| Quit | `bye` |

Dates are written as `yyyy-MM-dd` or `yyyy-MM-dd HHmm`. A description may not
contain `|`, since that is what separates fields in the save file.

Goat says what to type when a command is not understood, so the list above is a
convenience rather than something to memorise.

## Building and running from the command line

The project is built with Gradle, so no Gradle installation is needed -- the
wrapper script fetches the right version itself. On macOS and Linux use
`./gradlew`; on Windows use `gradlew.bat`.

| What you want | Command |
| --- | --- |
| Run the chatbot | `./gradlew run` |
| Run the tests | `./gradlew test` |
| Compile, test and package | `./gradlew build` |
| Build just the JAR | `./gradlew shadowJar` |

## Distributing Goat as a JAR file

`./gradlew shadowJar` writes an executable JAR to `build/libs/goat.jar`. It is
self-contained, so it is the only file anyone needs in order to run Goat.

To use it:

1. Copy `goat.jar` into an empty folder.
1. Open a command window in that folder.
1. Run:
   ```
   java -jar "goat.jar"
   ```
   The double quotes are not normally needed, but they are if the folder path
   contains spaces or other special characters.

Goat saves the task list to `data/goat.txt`, created **in the folder the
command was run from**. Running the JAR from a different folder therefore
starts a separate list, which is why step 1 says to give it a folder of its
own.

The JAR is deliberately not committed to this repository, since generated
binaries do not belong in version control. It is published through the
repository's GitHub releases instead.
