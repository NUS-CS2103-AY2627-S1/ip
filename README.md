# Chatty task manager

Chatty is a mission-control-inspired task manager built for the NUS CS2103T
iP project. It supports todos, deadlines, events, recurring tasks, persistent
storage, command search, and a responsive JavaFX interface.

The repository name remains unchanged as required by the course workflow.

## Setting up in IntelliJ IDEA

Prerequisites: JDK 25 and a current IntelliJ IDEA version.

1. Open the repository as an IntelliJ project.
2. Configure the project SDK as JDK 25 and use the SDK default language level.
3. Run `chatty.gui.Launcher.main()` to start the JavaFX interface.
4. Alternatively, run `chatty.Chatty.main()` to use Chatty in the console.

Keep `src/main/java` as the source root because Gradle and the IDE expect the
standard Java project layout.

## Building and testing

Run the complete automated check with Java 25:

```shell
./gradlew clean check
```

Build the executable fat JAR with:

```shell
./gradlew shadowJar
```

The generated application is written to `build/libs/Chatty.jar`. The artifact
keeps its established filename for build compatibility; the product shown to
users is Chatty.

## User guide

See `docs/README.md` for commands, examples, and error-handling behavior.

## Acknowledgements

- This project started from the NUS CS2103T iP starter template and course
  materials: https://nus-cs2103-ay2627-s1.github.io/website/schedule/week4/project.html
- The JavaFX application structure was adapted from the SE-EDU Duke GUI
  tutorial: https://se-education.org/guides/tutorials/javaFx.html
- The GitHub Actions workflow was adapted from the recommended SE-EDU Duke
  Gradle workflow: https://github.com/se-edu/duke/blob/master/.github/workflows/gradle.yml
