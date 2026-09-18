# Aster

![Aster's chat window showing a task list and a highlighted error reply](docs/Ui.png)

Aster is a desktop chatbot that helps students keep track of todos, deadlines and events.
Type a short command, and Aster records the task, answers in a chat window and saves your list
automatically.

## Features

- Add todos, deadlines and events, with dates shown in an easy-to-read form
- Mark tasks as done or not done, and delete tasks you no longer need
- Search your tasks by keyword with `find`
- See your progress at a glance with `stats`
- Invalid commands are explained in red without changing your task list
- Automatic saving, so your tasks are still there the next time you open Aster

## User guide

Read the [Aster User Guide](https://minhhoanglenus.github.io/ip/) for every command, with examples.

## Getting started

1. Install Java 25. In a terminal, `java -version` should report version 25.
2. Download `Aster.jar` from the [latest release](https://github.com/MinhHoangLeNUS/ip/releases/latest).
3. Put `Aster.jar` in an empty folder, open a terminal in that folder, and run:

   ```
   java -jar Aster.jar
   ```

Aster keeps your tasks in `data/aster.txt` inside that folder.

## Building from source

With JDK 25 installed, run these from the project folder:

- `./gradlew run` starts Aster
- `./gradlew test` runs the tests
- `./gradlew shadowJar` builds `build/libs/Aster.jar`

## Acknowledgements

### Reused and adapted work

- The GUI structure (a separate `Launcher`, the FXML-based `MainWindow`, and a `DialogBox` that is loaded from
  FXML and flipped for Aster's replies) is adapted from the
  [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
- The Checkstyle configuration in `config/checkstyle/` is copied from
  [se-edu/addressbook-level3](https://github.com/se-edu/addressbook-level3) at commit `0bf2b1b9` (MIT License).
- The GitHub Actions workflow in `.github/workflows/gradle.yml` is copied from
  [se-edu/duke](https://github.com/se-edu/duke/blob/full-template/.github/workflows/gradle.yml)
  (`full-template` branch).
- The project started from the [NUS CS2103T individual project template](https://github.com/NUS-CS2103-AY2627-S1/ip),
  which provided the Gradle wrapper and the project layout.

### Libraries and tools

- [JavaFX](https://openjfx.io/) 17.0.7 for the graphical interface
- [JUnit](https://junit.org/) 5.14.4 for automated tests
- [Gradle Shadow plugin](https://github.com/GradleUp/shadow) 9.5.1 for packaging `Aster.jar`
- [Checkstyle](https://checkstyle.org/) 14.1.0 for code style checks

### Original artwork

The Aster flower mark is original artwork drawn by a Java2D program written for this project with AI assistance.
No image-generation model, font or external image was used.

### Use of AI tools

Aster was developed by Le Minh Hoang ([@MinhHoangLeNUS](https://github.com/MinhHoangLeNUS)) with extensive
AI assistance.

- **Claude Code (Anthropic)** assisted with planning, implementation, tests, documentation, debugging and verification.
- **Codex and ChatGPT (OpenAI)** assisted with reviewing plans and changes, coordinating the development
  workflow, and drafting prompts and commit messages.

I set the requirements and made the product and design decisions. I reviewed the plans and proposed changes,
requested corrections where needed, verified the resulting code, behaviour and documentation, and decided what
to accept. I performed the Git and GitHub operations myself and remain responsible for the submitted work.
