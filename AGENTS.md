# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * When proposing or creating a commit message or branch name, follow the `seedu-git-standard` skill.
  * Add Javadoc following the `seedu-java-coding-standard` skill (required for all classes and public methods, with the exceptions listed there).
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this project MUST follow the SE-EDU Java coding standard (basic + intermediate).
Whenever writing, editing, reviewing, or generating Java, read and follow the
`seedu-java-coding-standard` skill at `.cursor/skills/seedu-java-coding-standard/SKILL.md`.
Do not skip this skill. For topics not covered there, follow the Google Java Style Guide.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit tests

JUnit tests live under `src/test/java/`, mirroring the package structure of the code under test (for example, `kiwi.task.KiwiDate` → `kiwi.task.KiwiDateTest`).

**Coverage target:** focus JUnit tests on the top ~50% highest-value methods — prioritizing complex, core, or critical business logic (for example parsing, persistence, date handling, and task-list operations). Simple wrappers, console I/O, and thin command glue are lower priority and need not be fully covered.

When adding or changing application code under `src/main/java/`, update the relevant JUnit tests so they still match behaviour and the project stays within this target. Add tests for new high-value logic; revise or remove tests when signatures, behaviour, or error messages change.

Run `./gradlew test` to verify JUnit tests pass.

## Git

All commits in this project MUST follow the SE-EDU Git conventions.
Whenever proposing or creating a commit, writing a commit message, or naming a branch, read and follow the
`seedu-git-standard` skill at `.cursor/skills/seedu-git-standard/SKILL.md`.
Do not skip this skill.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

## After each code update

Whenever application code is changed (for example under `src/`), before considering the task done:

1. Update `test/ui-test-plan.md` if needed — add, revise, or remove cases so aims, inputs, and expected outputs still match the program's console behaviour.
2. Invoke the `test-ui` skill (read `.cursor/skills/test-ui/SKILL.md` and follow it) so the UI tests actually run. Do not skip this step after a code update.
3. Update JUnit tests under `src/test/java/` if needed so they still cover the top ~50% highest-value methods affected by the change (see **JUnit tests** above). Run `./gradlew test` and fix any failures before finishing.
