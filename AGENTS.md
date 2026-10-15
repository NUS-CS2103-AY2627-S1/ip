# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 2
* IDE and level of expertise: 2

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

## UI test maintenance

After each code update, update `test/ui-test-plan.md` when the change affects the command-line UI or its expected output. Then invoke the project-local `test-ui` skill to run the recorded UI test cases. If Java 25 or another required dependency is unavailable, report that limitation clearly rather than silently skipping the test.

## JUnit test maintenance

Maintain JUnit coverage for approximately the top 50% highest-value methods, prioritizing complex, core, and critical business logic. After each code change, update the relevant JUnit tests as needed to keep this coverage target and run them using Gradle.

## Java coding standard

For every Java code change or review, invoke and follow the project-local `seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard/`. New and modified Java code must comply with the required basic and intermediate SE-EDU Java coding conventions.

## Git

For every commit-message proposal, review, or commit, invoke and follow the project-local `seedu-git-standard` skill in `.codex/skills/seedu-git-standard/`.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
