# Bot67

Bot67 is a desktop task manager with a cheerful six-seven personality. It supports todos, deadlines, events,
task completion, deletion, keyword search, alphabetical sorting, and persistent local storage.

**Using Bot67?** Start with the [Bot67 User Guide](docs/README.md) for installation, a first-task walkthrough,
commands, and troubleshooting. Download packaged builds from [Releases](https://github.com/yileteoh/ip/releases)
when available.

**Working on the code?** Follow the developer setup below. Bot67 uses Java 25, Gradle, JavaFX, and JUnit 5.

## Setting up in IntelliJ IDEA

### Before opening the project

Install the following:

- **JDK 25**. A Java Runtime Environment alone is not sufficient because IntelliJ and Gradle need the Java compiler.
- A recent version of **IntelliJ IDEA**, either Community or Ultimate Edition.
- **Git**, if you intend to clone the repository from GitHub instead of downloading it as a ZIP file.

You do not need to install Gradle separately. The repository includes the Gradle Wrapper, which downloads and uses
the correct Gradle version automatically.

### Opening the project

1. Clone this repository or download and extract it to a permanent folder.
2. Open IntelliJ IDEA. If another project is open, select **File → Close Project** first.
3. Select **Open** and choose the repository's root folder—the folder containing `build.gradle`, `gradlew`, and
   `gradlew.bat`.
4. If IntelliJ asks whether to trust the project, select **Trust Project**.
5. When prompted, choose to import the project using **Gradle** and accept the remaining defaults.
6. Wait for the progress indicators to finish. The first import can take longer because Gradle must download JavaFX,
   JUnit, Checkstyle, and the Shadow plugin.

Keep `src/main/java` as the source root and `src/test/java` as the test source root. IntelliJ normally detects these
folders automatically during the Gradle import, so they should not be moved or renamed.

### Configuring Java 25

Both IntelliJ and Gradle must use Java 25. Configuring only one of them can cause confusing compilation errors.

1. Open **File → Project Structure → Project**.
2. Set **SDK** to JDK 25. If it is not listed, select **Add SDK → JDK** and choose the installed JDK 25 folder.
3. Set **Language level** to **SDK default** and apply the changes.
4. Open **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**. On macOS, open
   **IntelliJ IDEA → Settings** instead.
5. Set **Gradle JVM** to the same JDK 25 installation.
6. Select the **Reload All Gradle Projects** button in the Gradle tool window.

To verify the terminal configuration, open IntelliJ's **Terminal** tool window and run:

```text
java -version
```

The first line should begin with `java version "25` or `openjdk version "25`.

On macOS with the course SDKMAN setup, run `sdk use java 25.0.3.fx-zulu` before using the Gradle commands.

### Running Bot67

Use either of these methods:

- Open the Gradle tool window and run **Tasks → application → run**.
- Open IntelliJ's terminal and run `.\gradlew.bat run` in Windows PowerShell or `./gradlew run` on macOS/Linux.

The Bot67 window should open with the application name, welcome message, command guide, input field, and Send button.
Try `todo read book`, followed by `list`, to verify that commands and saved data work.

### Running automated checks

Before submitting a change, run the full test and style checks from IntelliJ's terminal:

```text
.\gradlew.bat test checkstyleMain checkstyleTest
```

On macOS/Linux, replace `.\gradlew.bat` with `./gradlew`. These run automated logic tests and coding-style checks;
they do not exercise the GUI. Follow [the UI test plan](test/ui-test-plan.md) for console scenarios and graphical
checks. The console entry point is `bot67.Bot67`; the normal application entry point is `bot67.Launcher`.

### Troubleshooting setup

- **Compilation errors immediately after opening:** wait for the Gradle import to finish, then reload the Gradle
  project. Restart IntelliJ if the errors remain.
- **Unsupported class-file or Java-version errors:** check both the Project SDK and Gradle JVM; both must use JDK 25.
- **JavaFX runtime components are missing:** launch Bot67 through the Gradle `run` task rather than running
  `Main.java` directly, so Gradle includes the JavaFX dependencies.
- **Gradle cannot download dependencies:** confirm that the computer has Internet access and that a firewall or proxy
  is not blocking Gradle.
- **`gradlew` is not executable on macOS/Linux:** run `chmod +x gradlew`, then try `./gradlew run` again.

## Building the JAR

Run `.\gradlew.bat clean shadowJar` in Windows PowerShell or `./gradlew clean shadowJar` on macOS/Linux.
The generated file is `build/libs/bot67.jar`. This fat JAR includes the runtime dependencies, including JavaFX
libraries for Windows, macOS, and Linux. It still requires Java 25 on the user's computer; verify it on the target
operating systems and architectures before claiming compatibility.

Copy the JAR into a new, writable folder, open a terminal there, and run:

```text
java -jar bot67.jar
```

Try adding, listing, and completing a task, then close and restart the app to check persistence. Its save file is
`data/bot67.txt` relative to the launch directory. Use a separate folder for smoke tests to avoid altering your
normal task list.

## Project layout

| Location | Purpose |
|---|---|
| `src/main/java/bot67` | Application entry points and command execution |
| `src/main/java/bot67/gui` | JavaFX window and message controllers |
| `src/main/java/bot67/parser` | Command and date validation |
| `src/main/java/bot67/task` | Task types and task-list operations |
| `src/main/java/bot67/storage` | Save-file loading, validation, and writing |
| `src/main/java/bot67/ui` | Text responses shared by the console and GUI |
| `src/main/resources` | FXML layouts, stylesheets, and images |
| `src/test/java` | JUnit tests |
| `test/ui-test-plan.md` | Console test cases and graphical checks |
| `docs/README.md`, `docs/Ui.png` | User Guide and product screenshot |

## Publishing the User Guide

The User Guide source is `docs/README.md`. Keep its commands consistent with the parser and its screenshot at
`docs/Ui.png` (case-sensitive). For GitHub Pages, select **Settings → Pages → Deploy from a branch**, then choose
**master** and **/docs**. Publish the reviewed documentation on that branch and check the rendered
[Bot67 website](https://yileteoh.github.io/ip/) after deployment. Check its tables, links, and screenshot in the
published page as well as in GitHub's Markdown preview.

Updating documentation on a feature branch alone does not update a Pages site configured to publish from `master`.

## Credits

- This project began from the NUS CS2103T individual-project template and follows the course's Duke tutorial
  progression.
- The JavaFX structure was adapted from the JavaFX tutorial supplied with the CS2103T individual-project template.
- The GUI images are third-party artwork with unverified source and licence details. See
  [contributors and credits](CONTRIBUTORS.md) for the outstanding attribution work and retained template credits.
- Development used OpenAI Codex for code review, implementation support, test design, and documentation refinement.
