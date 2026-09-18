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

All commit messages and branch names must follow the `seedu-git-standard` skill, which codifies the SE-EDU git conventions: https://se-education.org/guides/conventions/git.html. Load that skill before drafting a commit message or naming a branch. In particular, keep unrelated changes (e.g. application code vs. agent/process files) in separate commits rather than bundling them.

## Testing

Test coverage target: JUnit tests should cover the top ~50% highest-value methods across the codebase, prioritizing complex, core, or critical business logic (e.g. parsing, validation, persistence, state transitions) over trivial getters, pure I/O, or thin delegation.

Whenever code covered by this target changes — a method's logic, its signature, or its behavior — update the corresponding JUnit tests in the same change, so test coverage does not drift below the 50% target over time. Add tests for any new method that would rank in the top 50% by this same standard.

Tests live under `src/test/java`, mirroring the package of the class under test (e.g. `milo.task.Deadline` → `src/test/java/milo/task/DeadlineTest.java`). Run them with `./gradlew test`.

## Coding standard

All Java code in this project (`src/main/java` and `src/test/java`) must follow the `seedu-java-coding-standard` skill, which codifies the SE-EDU intermediate Java conventions (naming, layout, imports, Javadoc): https://se-education.org/guides/conventions/java/intermediate.html. Load that skill before writing or reviewing Java code, and bring existing code into compliance when you touch it.

The `checkstyle` Gradle plugin enforces this standard mechanically, configured via `config/checkstyle/checkstyle.xml` and `config/checkstyle/suppressions.xml`. Run it with `./gradlew checkstyleMain checkstyleTest`, and run it after any change to main or test source to catch violations before they're committed.
