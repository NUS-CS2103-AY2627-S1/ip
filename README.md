# Koara

![Koara's desktop interface](docs/Ui.png)

Koara is a modern desktop chatbot for managing personal tasks and training-client records. It combines fast command-based interaction with automatic local storage, clear error feedback, and a focused JavaFX interface.

## Features

- Create to-dos, deadlines, and events using validated ISO dates.
- List, search, mark, unmark, and delete tasks.
- Add, list, search, edit, and delete client records.
- Save tasks and clients automatically and recover gracefully from missing or invalid data files.
- Highlight invalid commands without ending the session or corrupting in-memory data.

See the **[Koara User Guide](https://itzkhoadao.github.io/ip/)** for every command, example, and data-file detail.

## Run from source

Koara requires **Java 25**.

1. Clone this repository and open a terminal in the project root.
2. Build the cross-platform application JAR:
   - Windows: `gradlew.bat clean shadowJar`
   - macOS/Linux: `./gradlew clean shadowJar`
3. Start Koara with `java -jar build/libs/koara.jar`.

In IntelliJ IDEA, configure the project SDK and language level as Java 25, then run `koara.gui.Launcher`.

## Development checks

Run the complete automated test and code-quality suite before committing:

- Windows: `gradlew.bat check`
- macOS/Linux: `./gradlew check`

The GitHub Actions workflow runs the same checks with Java 25 on Windows, macOS, and Ubuntu.

## Acknowledgements

### AI assistance

Dao Anh Khoa ([@itzkhoadao](https://github.com/itzkhoadao)) used **OpenAI Codex with the GPT-5.6 Sol model** extensively throughout the development of Koara. Approximately **90–95% of the development work involved AI assistance**. This is an estimate of the extent of AI involvement, rather than a measured percentage of AI-generated source lines.

Codex assisted with:

- **Implementation and command handling:** developing and refining the task model, to-do, deadline and event commands, task listing and searching, completion status changes, deletion, and validation of command arguments, dates and indexes.
- **Client management:** implementing and refining client creation, listing, searching, editing and deletion, including client fields, duplicate checks and phone-number validation.
- **Storage and error handling:** developing and debugging task and client persistence, automatic saving and loading, handling missing or malformed data files, and reporting read/write failures while preserving application state.
- **JavaFX interface and personality:** creating and refining the conversational interface, layouts and styling, window resizing, user/application message presentation, highlighted error feedback and Koara's response wording.
- **Testing and debugging:** writing and expanding JUnit tests for commands, parsing, task/client behaviour and storage; investigating failures and edge cases; preparing manual UI testing instructions; and running automated tests and code-quality checks.
- **Documentation:** drafting and revising the README, User Guide, command examples, launch instructions, storage descriptions and code documentation.
- **Build and release preparation:** configuring and refining the Java 25 Gradle build, GitHub Actions checks and cross-platform executable JAR packaging.
- **Development workflow:** planning implementation steps, reviewing proposed changes, and preparing Git commits, branches and merges.

Dao Anh Khoa remains responsible for understanding, reviewing and validating Koara's code, tests, documentation and submitted deliverables, including work produced or refined with AI assistance.
