# Yapper

Yapper is a JavaFX task companion with a conversational interface. Add to-dos,
deadlines and events, search your tasks, and track completed and pending work.

See the [user guide](docs/README.md) for commands and examples.

## Run

Install **Java 25** and check `java -version`. Build from the project folder:

```powershell
.\gradlew.bat clean build
java -jar build/libs/yapper.jar
```

On macOS/Linux, use `./gradlew clean build`. The JAR bundles JavaFX; a separate
JavaFX installation is unnecessary. A desktop environment is required.
Type a command into the window and press Enter or click **Send**.

## Develop

Open this folder as a Gradle project in IntelliJ. Set both the project SDK and
Gradle JVM to JDK 25, then reload Gradle. Run the Gradle `run` task to start the
GUI; its entry point is `yapper.Launcher`. The `runCli` task starts the console
interface. Keep production sources in `src/main/java` and resources in
`src/main/resources` so Gradle finds them.

- [Build and test instructions](docs/gradle.md)
- [JAR packaging and smoke tests](docs/jar.md)
- [Remaining course checks](docs/remaining-course-work.md)

Tasks are saved in `data/yapper.txt` relative to the working folder.

## Acknowledgements

Yapper began from the NUS CS2103 individual-project starter. Its JavaFX setup and
FXML conversation interface follow concepts from the
[SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
OpenAI Codex assisted with implementation, refactoring, tests, documentation,
and course-requirement checks; the student remains responsible for reviewing
and understanding the submitted work.

Download the [latest executable release](https://github.com/sheeppocket/ip/releases/latest) and read the [published user guide](https://sheeppocket.github.io/ip/). The bundled native libraries target Windows, Linux, and macOS x64; ARM64 is not bundled.
