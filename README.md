# Alfred

Alfred is a polite, cave-themed task-management butler. See the
[Alfred User Guide](docs/README.md) for commands and examples.

## Creating and running the executable JAR

Build an executable JAR from the project root:

```sh
./gradlew shadowJar
```

On Windows Command Prompt, run:

```bat
gradlew.bat shadowJar
```

Gradle creates `build/libs/alfred.jar`. Copy the JAR into an empty folder,
open a command window there, and run:

```sh
java -jar "alfred.jar"
```

The computer running Alfred needs Java 25 or a compatible newer runtime.
Running the JAR from its own folder keeps Alfred's saved task data there.

## Setting up in IntelliJ

Use JDK 25, then open this project directory in IntelliJ. If prompted, accept
the Gradle defaults and set the project SDK and language level to JDK 25. Run
the `alfred.Main` class to start the application.

Keep `src/main/java` as the Java source root because Gradle expects the
standard project layout.
