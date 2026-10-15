# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner undergraduate programmer.
* IDE and level of expertise: IntelliJ IDEA, beginner level.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java code creation, edit, refactor, or review in this repository, load and follow the project-specific `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. All Java code must comply with its SE-EDU basic and intermediate coding conventions.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing

After every update to application code:

1. Review `test/ui-test-plan.md` and update it when the changed behavior requires new or revised test cases.
2. Invoke the project-specific `test-ui` skill to run the documented UI tests and record the test session.

## JUnit testing

Maintain JUnit coverage for approximately the top 50% highest-value methods in the codebase, prioritizing complex, core, and business-critical logic over trivial getters, constructors, or thin delegating methods.

After every code change, review the affected behavior and add or update JUnit tests as needed to continue meeting this coverage target. Run the complete JUnit suite with Gradle to verify that all tests pass.

## Git

For every future commit or branch-name proposal in this repository, load and follow the project-specific `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
