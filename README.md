# Steph

Steph is a chat-style task manager for todos, deadlines and events. It runs as
a JavaFX chat window or in the console, and saves your list automatically.

**To run:** ensure Java 25 or later is installed (Mac users: use the JDK version prescribed in the
[Java installation guide](https://se-education.org/guides/tutorials/javaInstallationMac.html)),
download `steph.jar` from the [v1.0 release](https://github.com/preyjwal/ip/releases/tag/v1.0), and run:

```
java -jar steph.jar
```

See the [user guide](docs/README.md) for every command and troubleshooting.

To build the jar yourself: `./gradlew shadowJar` (output: `build/libs/steph.jar`).
