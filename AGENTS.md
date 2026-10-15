# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Advanced
* IDE and level of expertise: IntelliJ

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

# Testing and AI-generated code conventions

To keep code quality consistent and make automated review easier, follow these conventions for tests and AI-assisted changes:

- Test placement and naming:
  - Place JUnit tests under `src/test/java` mirroring the package structure of the classes under test.
  - Name test classes after the class being tested: `com.example.SomeClass` -> `src/test/java/com/example/SomeClassTest.java`.
  - Use the method naming convention: featureUnderTest_testScenario_expectedBehavior(). Examples:
    - `parseCommand_nullInput_throwsRalphException()`
    - `addTodo_listFull_throwsRalphException()`

- Assertions and imports:
  - Prefer explicit static imports for JUnit assertions (e.g., `import static org.junit.jupiter.api.Assertions.assertEquals;`) instead of wildcard static imports (`import static org.junit.jupiter.api.Assertions.*;`).

- Test coverage requirements for AI changes:
  - When modifying existing behavior or adding new features, include unit tests that exercise the change.
  - For features that touch business logic, add tests covering at least two non-trivial methods from two different classes where applicable.
  - Aim to include positive and negative (error) cases; test boundary conditions and common failure modes.

- Test style:
  - Keep tests deterministic and avoid relying on system state (time, environment variables) where possible. Use dependency injection or test doubles when needed.
  - Capture and assert observable behaviour (return values, state changes, exceptions, and outputs where appropriate).

- Running and validating tests:
  - Use the existing Gradle setup: `./gradlew test` (or `gradlew.bat test` on Windows) and ensure Java 25 is used.
  - Tests must pass locally before committing; do not commit failing tests.

These conventions help maintain readability, consistency, and reliability of tests across student projects and make AI-assisted contributions easier to review.

# Mandatory project standards

Follow the project-specific skills below for all work in this repository:

- Follow `.github/skills/seedu-java-coding-standard/SKILL.md` for all Java code and all code changes in this project.
- Follow `.github/skills/seedu-git-standard/SKILL.md` for all future commits and Git operations.
- For any Git commit message, ensure the subject line is in imperative mood, capitalized, and free of a trailing period, and include a rationale-rich body for non-trivial changes.
