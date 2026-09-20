# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Advanced
* IDE and level of expertise: Advanced

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Coding standards (mandatory)

All agents working in this repository **must** follow these project skills:

* **Java code** — read and apply [.cursor/skills/seedu-java-coding-standard/SKILL.md](.cursor/skills/seedu-java-coding-standard/SKILL.md) when writing, reviewing, or refactoring any `.java` file (source or test). Standards are based on the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
* **Git commits** — read and apply [.cursor/skills/seedu-git-standard/SKILL.md](.cursor/skills/seedu-git-standard/SKILL.md) when proposing or creating commits. Standards are based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Gradle and fat JAR

* Run the app from source: `./gradlew run`
* Build the fat JAR: `./gradlew shadowJar` → output at `build/libs/axiom.jar`
* Run the fat JAR: `java -jar build/libs/axiom.jar`
* Run unit tests: `./gradlew test`

`build.gradle` uses the Shadow plugin (`com.gradleup.shadow`) with `application.mainClass = 'axiom.Axiom'`. The plain `jar` task is disabled; `shadowJar` is the distributable artifact.

## Git

Follow [.cursor/skills/seedu-git-standard/SKILL.md](.cursor/skills/seedu-git-standard/SKILL.md) for all commit messages and branch names.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Testing

This project uses two layers of automated tests:

* **UI tests** — console session tests in `test/ui-test-plan.md`, run via `python3 test/run-ui-tests.py`.
* **JUnit tests** — unit tests under `src/test/java/`, run via `./gradlew test`.

### JUnit test coverage target

Aim to cover the **top ~50% highest-value methods** in the codebase. Prioritize complex, core, or critical business logic (e.g. parsing, command handling, task-list operations) over thin wrappers, I/O, or the main application loop.

When adding or changing production code, **update the relevant JUnit tests in the same change** so they stay aligned with behavior and continue to meet the ~50% coverage target. Run `./gradlew test` before considering the work complete.

### JUnit conventions

* Mirror the main source package structure under `src/test/java/` (e.g. `axiom.task.Todo` → `axiom.task.TodoTest`).
* Name test classes `<ClassUnderTest>Test`.
* For longer test names, use `featureUnderTest_testScenario_expectedBehavior()` (e.g. `parse_invalidFormat_exceptionThrown()`).
