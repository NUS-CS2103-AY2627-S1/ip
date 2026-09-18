# Bobby project

This is a greenfield Java project for the Bobby chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bobby/Bobby.java` file, right-click it, and choose `Run Bobby.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____        _     _           
   | __ )  ___ | |__ | |__  _   _ 
   |  _ \ / _ \| '_ \| '_ \| | | |
   | |_) | (_) | |_) | |_) | |_| |
   |____/ \___/|_.__/|_.__/ \__, |
                            |___/ 
   ```

## Running with Gradle

Use the Gradle wrapper from the project root:

```
./gradlew run
```

To build the project:

```
./gradlew build
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Bobby commands

Bobby supports the following commands:

| Command | Format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo borrow book` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `deadline return book /by 2019-12-08` |
| Add an event | `event DESCRIPTION /from START /to END` | `event meeting /from 2019-12-02 1400 /to 2019-12-02 1600` |
| List tasks | `list` | `list` |
| Mark a task as done | `mark TASK_NUMBER` | `mark 1` |
| Mark a task as not done | `unmark TASK_NUMBER` | `unmark 1` |
| Delete a task | `delete TASK_NUMBER` | `delete 1` |
| Tag a task | `tag TASK_NUMBER #TAG` | `tag 1 #urgent` |
| Find tasks | `find KEYWORD` | `find book` |
| Exit Bobby | `bye` | `bye` |

Tags must start with `#` and cannot contain spaces. Tagged tasks show their tags in `list` and can be found with
`find`, for example `find #urgent`.
