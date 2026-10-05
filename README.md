# E.V.

[![Java CI](https://github.com/aerodart/ip/actions/workflows/gradle.yml/badge.svg)](https://github.com/aerodart/ip/actions/workflows/gradle.yml)

E.V. is a JavaFX desktop task assistant that keeps track of your todos,
deadlines and events, and remembers them between sessions. It is themed after
*Spider-Man: Brand New Day*, with a heads-up display interface and a suit-AI
persona.

[User guide](https://aerodart.github.io/ip/)

## Features

- Add todos, deadlines and events
- Mark tasks as done or not done
- Delete tasks from the registry
- Search tasks by keyword, ignoring case
- Sort tasks alphabetically by description
- Automatic saving to and loading from `data/ev.txt`

## Getting started

Requires JDK 25.

Run from source:

```
./gradlew run
```

Or build a standalone jar and run that:

```
./gradlew shadowJar
java -jar build/libs/ev.jar
```

## Building

```
./gradlew build
```

This compiles the code, runs the unit tests and produces `build/libs/ev.jar`.

## Acknowledgements

- The JavaFX GUI scaffolding is adapted from the SE-EDU JavaFX tutorial:
  <https://se-education.org/guides/tutorials/javaFx.html>
  This covers `Main`, `Launcher`, `DialogBox` including its `fx:root` construction
  and `flip()` method, the FXML view skeletons, and the Gradle shadow-jar setup.
- The project skeleton is forked from the CS2103T iP template:
  <https://github.com/NUS-CS2103-AY2627-S1/ip>
- Artwork is third-party and used here only for a student project. None of it is
  original work:
  - Chat background, from peakpx:
    <https://www.peakpx.com/en/hd-wallpaper-desktop-gobwf>
  - E.V.'s avatar, a J.A.R.V.I.S. interface still, via ScreenRant:
    <https://screenrant.com/spider-man-homecoming-jarvis/>
  - The user's avatar, a frame from a Spider-Man GIF on Tenor:
    <https://tenor.com/en-GB/view/spider-man-gif-5310236965465327623>
- Claude (Anthropic) was used throughout this project as a coding
  assistant for reviewing code and testcases, diagnosing bugs and drafting parts of the documentation. All output was reviewed, tested and revised by me before being
  committed.

