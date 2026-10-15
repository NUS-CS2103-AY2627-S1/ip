# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 500-2000 lines
* IDE and level of expertise: Slightly mediocre

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Project coding and Git standards

* Apply the project skill `$seedu-java-coding-standard` to every Java code change. This skill is based on the SE-EDU Java basic and intermediate coding standard and its linked fallback style guide.
* Apply the project skill `$seedu-git-standard` when preparing every future commit. Do not commit or push unless explicitly asked.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing after code changes

After every application code update:

1. Review `test/ui-test-plan.md` and update it when the change adds or alters commands, console output, or other user-visible behavior. Keep each affected test case's aim, inputs, and expected output current.
2. Invoke the `$test-ui` skill after reviewing the plan, even when the review determines that the plan needs no changes.

Do not consider the code update complete until this UI test session has finished. If a test fails, follow the skill's stop-on-first-failure rule and report the actual and expected outputs instead of continuing with later cases.

## JUnit test coverage target

Maintain JUnit tests for at least the top 50% of the codebase's highest-value methods, prioritizing complex, core, or critical business logic.

After every code change, review the affected JUnit tests and update or add tests as needed so that they remain accurate and continue to meet the 50% coverage target. Do not consider a code change complete until the relevant JUnit tests have been updated and pass.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
