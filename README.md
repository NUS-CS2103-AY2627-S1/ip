# Daddy

Daddy is a Java 25 task manager with a polished JavaFX chat interface, task
persistence, scheduling commands, and a supportive coaching personality.

![Daddy showing a realistic task list](docs/Ui.png)

## Run Daddy

Download `Daddy.jar` from the
[GitHub releases page](https://github.com/YUJ1N06/ip/releases), place it in an
empty folder, and run:

```shell
java -jar Daddy.jar
```

See the [Daddy product website and user guide](docs/README.md) for features,
commands, input formats, and data-recovery behavior.

## Develop Daddy

The project requires Java 25. Run the complete automated verification suite
with:

```shell
./gradlew check jacocoTestReport
```

Keep `src/main/java` as the Java source root and `src/test/java` as the JUnit
test source root.
