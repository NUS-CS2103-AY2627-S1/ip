# Mr Chatbot

This is a project template for a greenfield Java project. The chatbot is named "mr chatbot". Given below are instructions on how to use it.

For commands, recurring schedules, and saving your tasks, see the [Mr Chatbot User Guide](docs/README.md).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/mrchatbot/MrChatbot.java` file, right-click it, and choose `Run MrChatbot.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   mr chatbot
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Recurring tasks

Create a task that resets daily or weekly:

```text
recurring exercise /every day
recurring project meeting /every week /on monday
recurring weekly review /every week
```

Weekly tasks accept an optional `/on` followed by a full weekday name
(Monday through Sunday, ignoring case). Without `/on`, the schedule uses
the weekday the task is created. Daily tasks do not accept `/on`.

Each task starts incomplete. Daily tasks first reset tomorrow; weekly
tasks first reset on the next scheduled weekday. If that weekday is
today, the first reset is next week. Use the existing `mark`, `unmark`,
`find`, and `delete` commands with recurring tasks as usual.

Reset dates use the computer's local calendar. At startup and before each
command, expired tasks become incomplete and advance to the first future
scheduled reset. Marking a task done does not move its schedule. Missed
occurrences do not create extra tasks. The list displays the schedule
and next reset date; changes are saved across restarts. There is no
background timer, so an idle display updates when you issue a command.
