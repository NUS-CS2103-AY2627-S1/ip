# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: novice
* IDE and level of expertise: beginner

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

## Java coding standard

For every Java code change in this project, use and follow the project-local `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard`. Apply it to production and JUnit test code.

## UI testing after code changes

After each code update, first update `test/ui-test-plan.md` when the change affects its test cases, commands, inputs, or expected output. Then invoke the project-local `test-ui` skill by running `bash .codex/skills/test-ui/scripts/run-ui-tests.sh` from the project root. If a test fails, stop the test session at that failure and report the actual and expected output before making further code changes.

## JUnit testing after code changes

After each code change, review and update the JUnit tests in `src/test/java` as needed. Maintain coverage for roughly the top 50% highest-value methods, prioritising complex, core, and critical business logic over trivial methods. Keep test files in the Gradle/JUnit package layout that mirrors the production class being tested, and run `./gradlew test` using Java 25.

## Git

For every future commit in this project, use and follow the project-local `seedu-git-standard` skill at `.codex/skills/seedu-git-standard`.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
