# Orbit

Orbit is a Java 25 personal task assistant with a JavaFX chat interface and a CLI.
Read the [User Guide](docs/README.md) for commands and data recovery guidance.

## Develop in VS Code

Open this folder with Java support installed and select JDK 25. Confirm the
terminal uses Java 25 with `java -version`.

```powershell
./gradlew.bat check
./gradlew.bat run
./gradlew.bat clean shadowJar
```

On macOS/Linux use `./gradlew` instead. The fat JAR is `build/libs/orbit.jar`:

```text
java -jar build/libs/orbit.jar
```

For the CLI, run `java -cp build/libs/orbit.jar orbit.Orbit`.

The build packages JavaFX natives for Windows, macOS, and Linux. Testing on other
operating systems still requires a test drive on those systems.

Based on the SE-EDU Duke starter and JavaFX tutorial; see the User Guide credits.
