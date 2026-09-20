# Week 4 iP Design

## Goal

Complete the Week 4 individual-project work, excluding peer review, by adding a cross-platform JavaFX GUI that follows Parts 1–4 of the SE-EDU JavaFX tutorial, retaining a testable console interface, configuring Checkstyle, applying varargs where useful, and updating the existing upstream pull-request description with the required GitHub Flavored Markdown elements.

## Scope

The implementation covers the following Week 4 iP items:

- `A-CheckStyle`, although the course labels it optional.
- `Level-10`, including at least JavaFX tutorial Parts 1–4.
- `A-Varargs`, using varargs only where they simplify an existing formatting need.
- The GitHub Flavored Markdown requirements for the existing iP pull-request description.

Peer reviews, the optional comparison with other students' submissions, team-project work, and CP3108A/B-only tutorial fork/tag/tweak requirements are outside scope. The student confirmed that they are not enrolled in CP3108A/B.

## Increment and Git Structure

The repository's established convention is retained:

1. Create `branch-A-CheckStyle` from `master`, implement and verify Checkstyle, commit it, merge it into `master`, and add the lightweight tag `A-CheckStyle`.
2. Create `branch-Level-10` from the updated `master`, implement and verify the GUI, commit it, merge it into `master`, and add the lightweight tag `Level-10`.
3. Create `branch-A-Varargs` from the updated `master`, introduce the small varargs improvement, commit it, merge it into `master`, and add the lightweight tag `A-Varargs`.

No code or tags will be pushed. The user explicitly authorized local branches, commits, lightweight tags, and the pull-request-description update.

## Checkstyle Design

The Gradle Checkstyle plugin and the SE-EDU configuration files will be added in the locations expected by the tutorial:

- `config/checkstyle/checkstyle.xml`
- `config/checkstyle/suppressions.xml`

The configured Checkstyle version will match the tutorial's `11.0.0`. Existing violations will be corrected without changing behavior. Both main and test source sets must pass their Checkstyle tasks.

## Chatbot Core

`Nexus` will become an instantiable chatbot engine while keeping its console entry point. Its public `getResponse(String input)` method will process one command and return the complete user-facing response for that command. This boundary lets the JavaFX controller and console UI reuse exactly the same command behavior.

The engine will own its `Storage` and `TaskList`. A no-argument constructor will use `data/nexus.txt`, while a path-taking constructor will support isolated automated tests. Existing commands remain available:

- `todo`, `deadline`, and `event`
- `list`
- `mark` and `unmark`
- `delete`
- `find`
- `bye`

Task mutations will still be saved immediately. Invalid commands, missing arguments, invalid dates, and out-of-range task numbers will return readable error messages rather than terminating the GUI. The console loop will print the greeting, pass each line to `getResponse`, print its response, and end after `bye`.

## JavaFX Architecture

The final structure will mirror tutorial Parts 1–4, adapted to the `nexus` package:

- `Launcher` contains the standalone `main` method and launches `Main`, avoiding the JavaFX classpath issue described by the tutorial.
- `Main` extends `Application`, loads `/view/MainWindow.fxml`, creates a scene, injects a `Nexus` instance into its controller, and shows the stage.
- `MainWindow` controls the scroll pane, dialog container, text field, and Send button declared in FXML.
- `DialogBox` is a reusable `HBox` custom control backed by `/view/DialogBox.fxml`. Factory methods create user and Nexus variants; the Nexus variant reverses its children and alignment.
- `MainWindow.fxml` defines the tutorial's 400-by-600 main layout and wires both Enter and the Send button to the same handler.
- `DialogBox.fxml` defines the wrapped message label, avatar, padding, and `fx:root` layout.
- `/images/DaUser.png` and `/images/DaDuke.png` provide the two tutorial-style speaker images.

The dialog container will scroll to the newest message automatically. Empty submissions will be ignored so the GUI does not add blank dialog pairs. A `bye` command will display the final response and then close the JavaFX application after a short delay, keeping the CLI-style exit behavior without putting JavaFX dependencies in the core engine.

## Gradle Configuration

The JavaFX configuration will follow the linked sample closely:

- Keep `mavenCentral()`.
- Set `javaFxVersion` to `17.0.7`.
- Add `javafx-base`, `javafx-controls`, `javafx-fxml`, and `javafx-graphics` dependencies for each of `win`, `mac`, and `linux`.
- Change the application entry point to `nexus.Launcher`.
- Continue using Java 25 and the existing Shadow plugin so the cross-platform executable JAR can be built.

The JavaFX libraries will not be configured through an additional JavaFX Gradle plugin because that would diverge from the course's required cross-platform dependency pattern.

## Varargs Design

The console response formatter needs to join different numbers of message lines in several commands. `Ui` will expose one focused varargs formatter accepting `String... lines`. `Nexus` will use it for greeting, task-operation, list, and error responses. This replaces repetitive newline concatenation and demonstrates varargs in a context where the number of lines genuinely varies.

## Testing

Core behavior will be developed test-first. Before changing production behavior, JUnit tests will establish the expected responses, persistence effects, error handling, search results, and `bye` response through real `Nexus` instances backed by temporary files. The existing console integration test will be preserved and adjusted to the refactored entry point only where necessary.

JavaFX view code will be checked at its practical boundaries:

- Gradle compilation confirms all Java controller and JavaFX APIs are valid.
- Resource and FXML loading will be exercised by launching the application.
- Both Enter and Send will map to the same FXML handler.
- A manual smoke test will cover visible startup, a task command, an error response, scrolling/alignment where observable, and `bye`.

Final verification will run the complete JUnit suite, both Checkstyle tasks, the full Gradle build, and the Shadow JAR task using Azul Zulu Java 25.0.3.

## Pull-Request Description

The existing upstream pull request is `NUS-CS2103-AY2627-S1/ip#383`. Its description will be edited, not supplemented by a comment, to contain all required elements: a heading, bullet list, numbered list, syntax-highlighted fenced code block, task list, emoji, blockquote, hyperlink, inline code, and bold/italic/strikethrough formatting. The text will describe Nexus accurately. If the browser is not authenticated, the finished Markdown will be handed to the user with the precise blocker noted.

## Sources

- Week 4 project requirements: https://nus-cs2103-ay2627-s1.github.io/website/schedule/week4/project.html
- JavaFX tutorial Part 1: https://se-education.org/guides/tutorials/javaFxPart1.html
- JavaFX tutorial Part 2: https://se-education.org/guides/tutorials/javaFxPart2.html
- JavaFX tutorial Part 3: https://se-education.org/guides/tutorials/javaFxPart3.html
- JavaFX tutorial Part 4: https://se-education.org/guides/tutorials/javaFxPart4.html
- Checkstyle tutorial: https://se-education.org/guides/tutorials/checkstyle.html
