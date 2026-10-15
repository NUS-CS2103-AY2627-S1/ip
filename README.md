# Atlas

[![Java CI](https://github.com/jarenyap/ip/actions/workflows/gradle.yml/badge.svg)](https://github.com/jarenyap/ip/actions/workflows/gradle.yml)

Atlas is a personal task-management chatbot with a Greek-oracle personality. It keeps track of your tasks — todos, deadlines, and events — and speaks to you in the voice of the pantheon.

## Features

| Command | Format | What it does |
| --- | --- | --- |
| `todo` | `todo <description>` | Adds a todo task |
| `deadline` | `deadline <description> /by <when>` | Adds a deadline task |
| `event` | `event <description> /from <start> /to <end>` | Adds an event task |
| `list` | `list` | Shows all tasks |
| `mark` | `mark <number>` | Marks a task as done |
| `unmark` | `unmark <number>` | Marks a task as not done |
| `delete` | `delete <number>` | Removes a task |
| `find` | `find <keyword>` | Shows tasks whose descriptions contain the keyword |
| `priority` | `priority <number> <high\|medium\|low\|none>` | Ranks a task; `none` clears the rank |
| `client add` | `client add <name> [/phone <number>] [/email <address>]` | Adds a client |
| `client list` | `client list` | Shows all clients |
| `client find` | `client find <keyword>` | Shows clients whose name, phone or email contains the keyword |
| `client delete` | `client delete <number>` | Removes a client |
| `bye` | `bye` | Exits Atlas |

Tasks and clients are numbered separately, so deleting a client never renumbers your tasks.

A task can be ranked with `priority <number> <high|medium|low>`. The level shows up in `list`
and `find` as a tag such as `[HIGH]`, is stored with the task, and is removed with
`priority <number> none`.

Example session:

```
> todo read the Odyssey
Got it. I've added: read the Odyssey
You now have 1 task(s) in the list.
```

## Setting up in IntelliJ

Prerequisites: JDK 25, update IntelliJ to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/atlas/Atlas.java` file, right-click it, and choose `Run Atlas.main()`. If the setup is correct, you should see something like the below as the output:

```
     _  _____ _        _    ____
    / \|_   _| |      / \  / ___|
   / _ \ | | | |     / _ \ \___ \
  / ___ \| | | |___ / ___ \ ___) |
 /_/   \_\_| |_____/_/   \_\____/

╭────────────────────────────────────────────╮
│ Hello! I'm Atlas, your personal assistant. │
╰────────────────────────────────────────────╯
╭────────────────────────╮
│ What can I do for you? │
╰────────────────────────╯
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running with Gradle

The Gradle wrapper lets you build and run Atlas without installing Gradle separately. Make sure Java 25 is active, then run these commands from the project root:

```
./gradlew run
```

To create the runnable JAR:

```
./gradlew shadowJar
```

The JAR is written to `build/libs/atlas.jar`.

## Testing

Run the JUnit unit tests with Gradle:

```
./gradlew test
```

Run the UI regression harness from the project root:

```
./test/ui-test.sh
```

It runs 34 scripted sessions against a clean build and verifies the expected output for every command and error path.

## Acknowledgements

Third-party libraries: [JavaFX](https://openjfx.io/) for the user interface, [JUnit 5](https://junit.org/junit5/) for the tests, and the [Gradle](https://gradle.org/) build tool.

AI assistance was widespread rather than localised. ChatGPT and Codex were used throughout the project as collaborators for design, implementation and tests, and ChatGPT generated the profile pictures and the background artwork.
