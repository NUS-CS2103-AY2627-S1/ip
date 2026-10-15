# Jelly

Jelly is a Java task-management chatbot with a JavaFX graphical interface.

## Running Jelly from `jelly.jar`

### Prerequisites

Install a 64-bit JDK 25 distribution that includes JavaFX. Zulu JDK 25 FX is
recommended. Confirm that Java is available by opening a terminal and running:

```text
java -version
```

The reported version should be 25 or later.

### Start the application

1. Download `jelly.jar`.
2. Place it in a folder where Jelly can store its task data.
3. Open a terminal in that folder.
4. Start Jelly with:

```bash
java -jar jelly.jar
```

The Jelly window will open. Your tasks are saved automatically in
`data/jelly.txt` next to the folder from which Jelly was started. Keep the
`data` folder if you want to retain your tasks between sessions.

Close the application window or enter `bye` to exit Jelly.

For the complete command list and usage formats, see the
[User Guide](docs/README.md).
