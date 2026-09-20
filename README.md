# Todd

Todd is a task-management chatbot with a playful NPC personality and a JavaFX chat interface.
Track todos, deadlines, and events; find tasks, mark your progress, and check upcoming reminders.

## Run from IntelliJ

1. Open this repository as a Gradle project.
2. Set the project SDK and Gradle JVM to **Java 25**.
3. Run `./gradlew run` in IntelliJ's terminal (`gradlew.bat run` on Windows).
4. Type `help` in Todd to see command formats and examples. Type `bye` to close the app.

On this Mac, `sdk use java 25.0.3.fx-zulu` selects the configured Java 25 installation.

## Development checks

Run `./gradlew test checkstyleMain checkstyleTest` with Java 25.
Build the application with `./gradlew shadowJar`; the output is `build/libs/todd.jar`.

Tasks are stored in `data/todd.txt`, relative to the folder from which Todd is launched.
A missing file starts a new list. If an existing file cannot be loaded, Todd reports the problem
and disables task changes to protect the saved data. Fix the file or its access permissions,
then restart Todd. Back up the data file before editing it manually.

## Credits

See [CREDITS.md](CREDITS.md) for the project foundation, adapted GUI structure, and artwork credits.
