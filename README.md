# Griddel

```text
 _______    ______     ________  ______   ______   ______   __          
/______/\  /_____/\   /_______/\/_____/\ /_____/\ /_____/\ /_/\         
\::::__\/__\:::_ \ \  \__.::._\/\:::_ \ \\:::_ \ \\::::_\/_\:\ \        
 \:\ /____/\\:(_) ) )_   \::\ \  \:\ \ \ \\:\ \ \ \\:\/___/\\:\ \       
  \:\\_  _\/ \: __ `\ \  _\::\ \__\:\ \ \ \\:\ \ \ \\::___\/_\:\ \____  
   \:\_\ \ \  \ \ `\ \ \/__\::\__/\\:\/.:| |\:\/.:| |\:\____/\\:\/___/\ 
    \_____\/   \_\/ \_\/\________\/ \____/_/ \____/_/ \_____\/ \_____\/ 
                                                                        
```

## Griddel User Guide

Griddel is a friendly, persistent task-management chatbot designed to make everyday planning simple. It combines a command-line interface with a JavaFX graphical user interface, allowing tasks to be managed either through typed commands or an interactive desktop window.

The project focuses on clean separation of responsibilities: parsing user input, executing commands, managing tasks, saving data, and presenting responses are handled by separate components. This makes Griddel easier to use, test, and extend as new features are added.

## Features

- Create todo, deadline, and event tasks.
- Mark tasks as completed or restore them to an incomplete state.
- Delete tasks by their displayed number.
- Search for tasks using a keyword.
- Check which tasks occur on a particular date.
- Save tasks locally so they remain available after restarting the application.
- Assign, filter, and sort tasks by priority.
- Use either the JavaFX GUI or the command-line interface.
- Display responses using separate user and Griddel dialog boxes in the GUI.

## Example session

```text
Hello! I'm Griddel.
What can I do for you?

> todo revise JavaFX
Got it. I've added this task:
  [T][ ] revise JavaFX

> deadline submit IP /by 2026-09-12
Got it. I've added this task:
  [D][ ] submit IP (by: Sep 12 2026)

> list
Here are the tasks in your list:
  1. [T][ ] revise JavaFX
  2. [D][ ] submit IP (by: Sep 12 2026)
```

The exact response wording may vary depending on the current task list and command result.

## Requirements

- JDK 25
- IntelliJ IDEA (recommended for development)
- Internet access on the first Gradle build so dependencies can be downloaded

The Gradle build selects the appropriate JavaFX platform automatically. On Apple Silicon Macs, it selects the `mac-aarch64` JavaFX libraries.

## Setting up in IntelliJ IDEA

1. Open IntelliJ IDEA and choose **Open**.
2. Select the project directory, namely the directory containing `build.gradle`.
3. Configure the project SDK to use **JDK 25**.
4. Allow IntelliJ IDEA to import the Gradle project.
5. Run the Gradle `run` task, or run `jonathan.Launcher`.

## Running the application

From the project directory, run:

```bash
./gradlew run
```

This launches the JavaFX GUI. Enter commands in the text field and press **Enter** or click **Send**.

The GUI layout is defined by:

```text
src/main/resources/view/MainWindow.fxml
src/main/resources/view/DialogBox.fxml
```

Character images are stored in:

```text
src/main/resources/jonathan/images/
```

The command-line interface can be run by executing `jonathan.Jonathan.main` from IntelliJ IDEA. The CLI and GUI share the same parser, task list, storage, and command classes, so the same commands behave consistently in both interfaces.

## Supported commands

| Command | Example | Description |
| --- | --- | --- |
| `todo` | `todo read book` | Adds a todo task |
| `deadline` | `deadline submit report /by 2026-09-05` | Adds a task with a deadline |
| `event` | `event team meeting /from 2026-09-06 /to 2026-09-07` | Adds an event |
| `list` | `list` | Displays all tasks |
| `mark` | `mark 1` | Marks a task as done |
| `unmark` | `unmark 1` | Marks a task as not done |
| `delete` | `delete 1` | Deletes a task |
| `find` | `find book` | Finds tasks containing a keyword |
| `check` | `check 2026-09-05` | Displays tasks occurring on a date |
| `bye` | `bye` | Displays a goodbye message and ends the session |
| `priority` | `priority 1 high` | Assigns a priority to a task |
| `filter` | `filter priority high` | Displays tasks with the specified priority |
| `sort` | `sort priority` | Displays tasks from high to low priority without changing the original order |

Tasks are saved in:

```text
data/jonathan.txt
```

## How the application is organised

```text
User input
    ↓
Parser          Converts text into a command
    ↓
Command         Performs the requested task operation
    ↓
TaskList        Stores and updates tasks
    ↓
Storage         Persists tasks in data/jonathan.txt
    ↓
UI / JavaFX     Displays the response to the user
```

The JavaFX interface uses FXML for its layouts. `Main.java` loads the main window, `MainWindow.java` handles user interaction, and `DialogBox.java` creates the conversation bubbles with the appropriate character image.

## Building the bundled JAR

Run the checks:

```bash
./gradlew clean check
```

Build the bundled JAR:

```bash
./gradlew clean shadowJar
```

The JAR is generated at:

```text
build/libs/griddel.jar
```

Run it with:

```bash
java -jar build/libs/griddel.jar
```

The JAR includes the JavaFX native libraries for the operating system and architecture used during the build. Build a separate JAR for each target platform.

## Testing and code quality

```bash
./gradlew test
./gradlew check
```

## Project structure

```text
src/main/java/jonathan/
├── command/        Command implementations
├── parser/         User input parsing
├── storage/        Saving and loading tasks
├── task/           Task types and task list management
├── ui/             Command-line input and output
├── DialogBox.java  JavaFX dialog-box component
├── Main.java       JavaFX application entry point
├── MainWindow.java JavaFX main-window controller
└── Launcher.java   JavaFX launcher

src/main/resources/
├── view/               FXML layouts
└── jonathan/images/    GUI images
```
