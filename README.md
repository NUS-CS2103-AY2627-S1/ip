# Bambolino

Bambolino is a task companion with a JavaFX chat interface. Add to-dos, deadlines, and events,
find tasks, and track their completion. See the [user guide](docs/README.md) for commands.

## Run and develop

Use JDK 25 with JavaFX (the project uses Zulu `25.0.3.fx-zulu`). Open this repository in
IntelliJ IDEA and set the project SDK and Gradle JVM to Java 25.

- Run the GUI: `./gradlew run`.
- Run automated tests: `./gradlew test`.
- Build the packaged application: `./gradlew shadowJar`.
- Run the packaged application: `java -jar build/libs/bambolino.jar`.
- For the console interface, run `bambolino.Bambolino.main()` in IntelliJ IDEA.

Tasks are saved in `data/bambolino.txt` relative to the directory from which the application
is launched. A missing file starts an empty list; the folder and file are created when saving.

## Credits

This project is based on the [NUS CS2103/T iP starter template](https://github.com/NUS-CS2103-AY2627-S1/ip).
The task types and command interaction follow the course's
[Project Duke specification](https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/index.html).
Original template contributors are retained in [CONTRIBUTORS.md](CONTRIBUTORS.md).

OpenJFX supplies the GUI toolkit, JUnit supplies the test framework, and Gradle with Shadow
supplies the build and packaging tools (see `build.gradle`).

OpenAI Codex assisted with the storage-warning fix, regression test, and documentation in this update.
