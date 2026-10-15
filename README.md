# Bob project

This is a greenfield Java chatbot project named _Bob_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bob/gui/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, Bob's chat window should open. Type a command in the text box and press **Enter** or click **send** to get started. See the [User Guide](docs/README.md) for available commands.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


## Task priorities

Append `/priority low`, `/priority moderate`, or `/priority high` to an add command:

```text
todo read book /priority low
deadline submit report /by 2026-09-09 /priority high
event meeting /from 2026-09-09 1400 /to 2026-09-09 1600 /priority moderate
```

Omit the field or use `/priority none` for no tag. The field must come last,
appear only once, and use a lowercase value. Tagged tasks display, for example,
`[T][ ][priority: low] read book`. Priorities are saved across restarts; existing
saved tasks remain untagged. Task order and description-based search are unchanged.
Priorities are assigned when adding tasks.
