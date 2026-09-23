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

## Java coding standard

For every Java source or test code creation, edit, refactor, or review in this project, invoke and follow the project-specific `$seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard/`. Treat its checklist as mandatory for all Java code in the repository, including package structure, naming, layout, imports, statements, comments, and Javadocs.

Before completing a Java change, audit every touched Java file against the skill and correct any violations introduced or exposed in the same logical unit.

## JUnit testing after code changes

Maintain JUnit tests for approximately the top 50% highest-value methods in the codebase. Prioritize methods containing complex logic, core application behavior, data parsing or persistence, important state changes, and error handling. Do not add low-value tests solely to increase the method count.

After every code update:

1. Review the affected classes and identify whether their JUnit tests need to be added or updated to maintain this coverage target.
2. Add or update tests for changed behavior, new edge cases, and regressions before considering the code update complete.
3. Run the complete JUnit suite using Gradle and report the result. If a JUnit test fails, stop and report the failure before making further changes.

## UI testing after code changes

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change affects a user-visible command, input, or output, or when it needs new UI test coverage.
2. Invoke the `$test-ui` skill to run the recorded UI test cases, even when the test plan did not need an update.
3. Report the test result. If a test fails, stop and report the actual and expected outputs before making further changes.

## Git

For every future commit message creation, proposal, amendment, or review, invoke and follow the project-specific `$seedu-git-standard` skill in `.codex/skills/seedu-git-standard/`. Treat its commit-message checklist as mandatory. Also follow its branch-naming rules whenever proposing or creating a branch.

Before creating a commit, inspect the staged changes and verify that the complete commit message complies with the skill. Do not create a non-compliant commit even if a draft message was supplied; revise the message and briefly explain the correction.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
