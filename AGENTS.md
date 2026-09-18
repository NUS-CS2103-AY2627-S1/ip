# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: beginner
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

Invoke and follow the project-specific `$seedu-java-coding-standard` skill whenever writing, modifying, or reviewing Java production or test code in this repository.

## Testing after code changes

After every code update:

1. Review `test/ui-test-plan.md` and update it when the changed behavior requires new or revised UI test cases. Keep each test case's aim, inputs, and expected output accurate.
2. Invoke the project-specific `$test-ui` skill and run the UI test plan. Do not consider the code update complete until this has been done.
3. If a UI test fails, stop the test session immediately and report the actual and expected outputs, as required by the skill.

## JUnit test coverage

Maintain JUnit tests for approximately the top 50% of high-value methods, prioritising core parsing, task-management, storage, date-handling, and command logic over trivial accessors.

After code changes, review and update the JUnit tests so this high-value coverage target remains satisfied, then run the complete Gradle test suite.

## Git

Invoke and follow the project-specific `$seedu-git-standard` skill whenever proposing or creating a commit in this repository.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
