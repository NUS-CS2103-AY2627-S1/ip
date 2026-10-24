# Project Clara

A JavaFX personal assistant chatbot built in Java as part of the NUS CS2103/T Individual Project.

Clara supports adding, listing, marking, unmarking, deleting, changing task priorities, and
automatically saving tasks.

## Getting Started

### Requirements

* Java 25

### Running the Application

Run Clara's JavaFX graphical interface with Gradle:

```bash
./gradlew run
```

Refer to the user guide `/docs/README.md` for supported behaviour.

## Project Structure

The project will gradually be expanded as new functionality is introduced.

```text
.
├── docs/
│   └── README.md       # Guide targeted for users
├── src/
│   ├── test/ # JUnit tests
│   └── main/
│       └── java/
│           └── clara/
│               ├── exception/
│               │   └── ClaraException.java  # Application exceptions
│               ├── gui/
│               │   ├── DialogBox.java       # Reusable message-bubble control
│               │   ├── Launcher.java        # JavaFX application entry point
│               │   ├── Main.java            # JavaFX application setup
│               │   └── MainWindow.java      # Main-window FXML controller
│               ├── parser/
│               │   └── Parser.java          # User command parser
│               ├── storage/
│               │   └── TodoFileHandler.java # File persistence and parsing
│               ├── task/
│               │   ├── Deadline.java        # Deadline task model
│               │   ├── Event.java           # Event task model
│               │   ├── Priority.java        # Task priority levels
│               │   ├── Task.java            # Base task model
│               │   ├── TaskList.java        # Task list manager
│               │   └── Todo.java            # To-do task model
│               ├── ui/
│               │   └── Ui.java              # User interaction and console output
│               └── Clara.java               # Application entry point
│       └── resources/
│           ├── css/
│           │   ├── dialog-box.css            # Message-bubble styling
│           │   └── main.css                  # Main-window styling
│           ├── images/
│           │   ├── Clara.png                 # Clara avatar
│           │   └── ClaraUser.png             # User avatar
│           └── view/
│               ├── DialogBox.fxml            # Dialog-box layout
│               └── MainWindow.fxml           # Main-window layout
├── .gitignore
├── AGENTS.md           # Instructions for AI coding agents
├── CLAUDE.md           # For Claude Code (redirects to `AGENTS.md`)
├── CONTRIBUTORS.md     # List of contributors to the project
├── CITATIONS.md        # Citation tracking
├── CITATION_RULES.md   # Citation requirements and guidance
├── PROJECT.md          # Description of the project, **intended for AI coding agents**
└── README.md           # Project documentation
```

### Project Guidelines

When modifying the project, keep the existing structure and naming conventions consistent. New functionality should be accompanied by appropriate tests as the test suite is introduced.

The [CS2103/T Project Duke specification](https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/index.html) is the authoritative source for project requirements and progression.

## Status

This project is currently at: **Level 10**. It will be developed incrementally throughout the iP.

## Acknowledgements

The Clara and user avatar illustrations in the GUI were generated using OpenAI image generation
through Codex. See `CITATIONS.md` [C-014].

The task priority feature was implemented with Codex AI assistance. See `CITATIONS.md` [C-018].


*Level 0 README: Generated and modified from a [ChatGPT chat thread](https://chatgpt.com/share/6a7dd837-21bc-83ec-824e-6ab1b746ac1f).* 
