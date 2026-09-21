# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Previously developed full-stack web apps & PWAs
* IDE and level of expertise: JetBrains IntelliJ, basic level of expertise

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard:

All Java code in this repository (main and test sources) MUST follow the se-edu Java coding
standard (basic + intermediate). Invoke the `$seedu-java-coding-standard` skill whenever writing,
reviewing, or refactoring `.java` files, and fix violations on contact. The skill distills
https://se-education.org/guides/conventions/java/intermediate.html; anything it does not cover
follows the Google Java Style Guide.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Unit testing

JUnit tests focus on the top ~50% highest-value methods in the codebase, prioritising complex, core, or critical business logic over trivial accessors, I/O glue, and exception holder classes. Follow Gradle and JUnit conventions: mirror the package structure under `src/test/java` and name each test class `FooTest` for the class `Foo` being tested. After every code change, review the affected classes and update or extend the JUnit test suite so it stays compliant with this coverage target, then run `./gradlew test` to verify that all tests pass. If a unit test fails, stop and report the actual and expected output before making further code changes.

## UI testing

After every code update, review `src/test/ui-test-plan.md` and update its test cases whenever the user-visible console behaviour has changed or requires additional coverage. Then invoke the `$test-ui` skill to run the plan. If a UI test fails, stop and report the actual and expected output before making further code changes.

## Git

All commit messages and branch names in this repository MUST follow the se-edu Git
conventions. Invoke the `$seedu-git-standard` skill whenever proposing or creating a
commit, naming a branch, or reviewing git history. The skill distills
https://se-education.org/guides/conventions/git.html.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
