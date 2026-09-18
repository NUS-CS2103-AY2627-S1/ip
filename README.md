# Moss — Quiet Task Garden

Moss is a calm, garden-themed JavaFX task manager written in Java. Moss helps you plant todos, deadlines, and events, tend tasks as they are completed, prune tasks you no longer need, and arrange dated tasks chronologically. Tasks are saved automatically and restored the next time the application starts.

## Requirements

- Java Development Kit (JDK) 25
- IntelliJ IDEA, or a terminal with `javac` and `java`

## Running the GUI in IntelliJ IDEA

1. Open this project in IntelliJ IDEA.
2. Set the project SDK to JDK 25 and use the SDK default language level.
3. Open `src/main/java/alpha/gui/GuiLauncher.java`.
4. Right-click the file and select **Run `GuiLauncher.main()`**.

On macOS, if you use SDKMAN, select Java 25 before compiling:

```bash
sdk use java 25.0.3.fx-zulu
```

## Running with Gradle

Gradle is the recommended way to run Moss because it compiles the project and uses the configured JavaFX entry point:

```bash
./gradlew --console=plain run
```

The `--console=plain` option keeps Gradle's output compact while Moss is running. Without that option, this also works:

```bash
./gradlew run
```

On Windows, use:

```bat
gradlew.bat --console=plain run
```

## Packaging the application

To create a distributable executable JAR, run:

```bash
./gradlew shadowJar
```

The JAR is written to `build/libs/moss.jar`. The regular `build` task also
creates this JAR automatically:

```bash
./gradlew build
```

Copy `build/libs/moss.jar` into an empty folder and run it from that folder:

```bash
java -jar "moss.jar"
```

The JAR includes the application's runtime classes and its entry point, so no
Gradle installation or project files are needed in the distribution folder.

To run the JUnit tests with Gradle:

```bash
./gradlew test
```

## Running without Gradle

You can also compile and run the source files directly from the project root when using the Java 25 distribution bundled with JavaFX:

```bash
mkdir -p out
JAVA_HOME_PATH=$(java -XshowSettings:properties -version 2>&1 | awk -F'= ' '/java.home/ {print $2; exit}')
javac --release 25 --module-path "$JAVA_HOME_PATH/jmods" \
  --add-modules javafx.controls,javafx.fxml -d out $(find src/main/java -name '*.java')
cp -R src/main/resources/. out/
java --module-path "$JAVA_HOME_PATH/jmods" --add-modules javafx.controls,javafx.fxml \
  -cp out alpha.gui.GuiLauncher
```

## Moss's GUI

The GUI is designed as a small, focused task garden:

- The header identifies Moss and shows the current number of tasks.
- Tasks appear as compact cards with a task number, completion control, and delete control.
- The feedback panel uses green for successful actions, amber for loading warnings, and red for invalid commands.
- The command box at the bottom is Moss's main conversation point. Press **Enter** or click **Tend** to run a command.
- The window can be resized; the task area expands to use the available space.

Moss speaks in a gentle gardening metaphor: tasks are planted, completed tasks are tended, and unwanted tasks are pruned. The metaphor changes the wording and appearance only; the commands below remain the same.

## Commands

### Add a todo

```text
todo <description>
```

Example:

```text
todo read the software engineering textbook
```

### Add a deadline

Use `/by` to specify when the task is due:

```text
deadline <description> /by <date or time>
```

Example:

```text
deadline return library book /by 2019-06-06
```

### Add an event

Use `/from` and `/to` to specify the event's start and end times:

```text
event <description> /from <start time> /to <end time>
```

Example:

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

Dates can be entered as `yyyy-MM-dd`, such as `2019-10-15`. Date/time values can use `yyyy-MM-dd HHmm`, such as `2019-10-15 1800`, or the day-first format `d/M/yyyy HHmm`, such as `2/12/2019 1800`. Moss displays parsed values in a readable format such as `Dec 02 2019 18:00`.

### View, sort, and update tasks

```text
list
sort
find <keyword>
mark <task number>
unmark <task number>
delete <task number>
```

The `find` command searches task descriptions without regard to letter case. For example:

```text
find book
```

Task numbers are shown by the `list` command. For example:

```text
list
mark 2
delete 1
```

The `sort` command orders deadlines by due date and events by start time. Undated todos remain at the end. Moss saves the new order automatically.

### Exit

```text
bye
```

## Saving tasks

Moss saves the task list automatically after every add, delete, mark, and unmark operation. The data is stored in the relative path:

```text
data/moss.txt
```

The `data` folder and file are created automatically the first time a task is saved. Do not delete this file if you want to keep your tasks.

## Example session

```text
todo read a book
deadline submit assignment /by 2019-12-06
event team meeting /from 2019-12-06 1000 /to 2019-12-06 1100
find meeting
mark 1
bye
```
