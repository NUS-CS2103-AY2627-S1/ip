# Sheppy

Sheppy is a Java 25 task manager with a JavaFX chat window and an optional console interface.

See the [User Guide](docs/README.md) for setup, command examples, saving and troubleshooting.

## Running with Gradle

This project uses Gradle's standard Java layout. The application entry point is
`sheppy.gui.Launcher`. The console entry point is `sheppy.Sheppy`.

Use the included Gradle Wrapper so the project downloads and uses the pinned
Gradle version automatically:

```bash
./gradlew build
./gradlew run
```

The `run` task opens the GUI. Enter commands in the window and press Enter or
click Send. Gradle stays running until the window closes; this is normal.
For the console interface, run `./gradlew classes`, then
`java -cp build/classes/java/main sheppy.Sheppy`.

## Commands

Commands accept leading/trailing whitespace and repeated spaces or tabs.
Use each `/by`, `/from`, and `/to` marker only once in its command.
The `|` character is reserved for saved data and cannot appear in task commands.
Failed saves leave the previous task list intact. If loading fails, back up
and repair `data/tasks.txt`, then restart Sheppy before changing tasks.
Saving requires a filesystem that supports atomic replacement of files.

- `todo DESCRIPTION` adds a todo.
- `deadline DESCRIPTION /by yyyy-MM-dd` adds a deadline.
- `event DESCRIPTION /from START /to END` adds an event.
- `list` displays all tasks.
- `find KEYWORD` displays tasks containing the keyword.
- `sort` orders tasks alphabetically by description and saves the new order.
- `mark NUMBER` and `unmark NUMBER` update a task's completion status.
- `delete NUMBER` removes a task.
- `bye` exits Sheppy.

## Build and test tasks

```bash
./gradlew tasks     # list available tasks
./gradlew build     # compile, test, and assemble the project
./gradlew test      # run unit tests
./gradlew checkstyleMain checkstyleTest # check Java coding style
./gradlew shadowJar # create build/libs/sheppy.jar, including JavaFX
./gradlew clean     # remove generated build output
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

Gradle uses the Java 25 toolchain configured in `build.gradle`. IntelliJ and
VS Code can both import and run the same Gradle project; the IDE only provides
a graphical way to invoke the Gradle tasks.

The standard Gradle source directories are:

```text
src/main/java       application source
src/test/java       unit tests
```

The generated `build/` directory contains compiled classes and the JAR and is
ignored by Git. The `gradle/wrapper/` files and `gradlew` scripts are committed
so that everyone uses the same Gradle version.

## Creating and running the executable JAR

Create the JAR with:

```bash
./gradlew clean shadowJar
```

Gradle places the fat JAR at `build/libs/sheppy.jar`. With Java 25, run it directly:

```bash
java -jar build/libs/sheppy.jar
```

The JAR can also be copied into an empty folder and run there. Sheppy will
create its relative `data/tasks.txt` file in that folder when it saves tasks.
The ordinary `jar` task creates a thin JAR without JavaFX dependencies; use
`shadowJar` for distribution. Test the fat JAR on each intended OS before release.
The fat JAR includes native libraries for Windows/Linux x64 and both Intel and
Apple Silicon Macs. On Macs without JavaFX installed, the launcher extracts the
matching native libraries to a temporary folder. Other CPU architectures are
not bundled. A successful run with a JavaFX-enabled JDK alone does not verify
the packaged libraries; also smoke-test with a plain Java 25 JDK.

## Credits

The project began with the CS2103/T iP starter template; the original
contributors are retained in [CONTRIBUTORS.md](CONTRIBUTORS.md).
The JavaFX `MainWindow`/`DialogBox` and FXML organization adapts the approach in
the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart4.html).
Implementation and testing were developed with AI assistance, including Codex.
The native-library setup follows the search order documented in
[OpenJFX's native loader](https://github.com/openjdk/jfx/blob/jfx17/modules/javafx.graphics/src/main/java/com/sun/glass/utils/NativeLibLoader.java).

## Coding conventions

The Java source follows the SE-EDU basic and intermediate coding conventions:

- Use four spaces for indentation, not tabs.
- Keep lines at 120 characters or fewer.
- Use PascalCase for classes and lowerCamelCase for methods and variables.
- Use braces for control-flow blocks.
- Add Javadoc to public classes and methods and to non-obvious fields.

The `.editorconfig` file records the formatting rules for editors that support
EditorConfig, including VS Code.

## Checking Java style

Run Checkstyle for both application and test code with:

```bash
./gradlew checkstyleMain checkstyleTest
```

The rules in `config/checkstyle/checkstyle.xml` follow the SE-EDU Java coding
standard used by this project. Checkstyle reports are generated under
`build/reports/checkstyle/`.
