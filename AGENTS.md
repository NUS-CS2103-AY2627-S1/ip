# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Amateur
* IDE and level of expertise: Amateur

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

Follow the project-specific `seedu-java-coding-standard` skill for all Java code in this project. Apply it when creating, editing, reviewing, or formatting Java source and test code.

## Git

Follow the project-specific `seedu-git-standard` skill for all future branches, commits, and commit messages in this project.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## UI testing after code changes

After each code update, check whether `test/ui-test-plan.md` needs to be updated to cover the changed behavior. If it does, update the test plan before running tests.

After each code update, invoke the project-specific `test-ui` skill to run the UI tests recorded in `test/ui-test-plan.md`. Report the console input/output record from the test session, and if a test fails, stop immediately and report the expected and actual outputs.

## JUnit testing after code changes

Maintain JUnit coverage for the top ~50% highest-value methods, prioritizing complex, core, or critical business logic over trivial getters/setters and simple wiring code.

After each code update, check whether existing JUnit tests need to be updated or new JUnit tests need to be added to keep that 50% high-value coverage target. Run the Gradle JUnit test suite after code changes, in addition to the UI test suite.
