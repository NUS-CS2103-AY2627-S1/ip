# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner

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

## Testing

Maintain JUnit test coverage targeting the top ~50% highest-value methods in the codebase (prioritizing complex, core, or critical business logic over trivial getters/setters/pass-throughs). Tests live under `src/test/java`, mirroring the package structure of `src/main/java`. Run them with `gradlew.bat test`.

Whenever a code change touches a class that has tests, or introduces new non-trivial logic, update or add JUnit tests in the same change so coverage continues to meet that 50% target — don't let tests go stale relative to the code they cover. Where a method under test depends on I/O or another hard-to-control collaborator (e.g. file access, console output), prefer dependency injection plus a real temporary resource (e.g. JUnit's `@TempDir`) or a hand-written stub/test-double over mocking frameworks, to keep tests readable at this course's level.
