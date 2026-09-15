# Jaku

Jaku is a calm desktop task companion for keeping everyday tasks, dates, events, and recurring commitments in one local plan.

## Requirements

Run Jaku with Java 25. On macOS, the course-supported setup is `sdk use java 25.0.3.fx-zulu`.

## Run Jaku

Open the JavaFX chat interface:

```sh
./gradlew run
```

For terminal-based regression testing, run the `jaku.Jaku` entry point from your IDE or compiled classes. Jaku stores tasks in `data/jaku.txt` by default; set the `jaku.dataFile` system property to use a different location.

If the saved-task file is missing, Jaku starts with an empty plan. If it cannot read the existing file safely, it enters recovery mode and leaves the file unchanged until it is repaired and Jaku is restarted.

## Build

Create the cross-platform runnable JAR:

```sh
./gradlew clean shadowJar
```

The output is `build/libs/jaku.jar`.

## Acknowledgements

This project used OpenAI Codex as a collaborative coding assistant for the Week 6 optional increments. The author reviewed, tested, and integrated the resulting changes.
