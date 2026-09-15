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

Invoke and follow the project-specific `$seedu-java-coding-standard` skill whenever creating, editing, or reviewing
Java code. All Java code in this project must follow that standard.

## Verification after code updates

After every code update, complete these steps before reporting the task as finished:

1. Review `test/ui-test-plan.md` and update it when the code change adds or changes console commands, command sequences, or user-visible output. If no update is needed, preserve the existing plan.
2. Invoke the project-specific `$test-ui` skill and run its UI test workflow after the test plan is up to date.
3. If a UI test fails, stop the test session immediately and report the failed case with its actual and expected output. Do not report the code-update task as complete while the UI tests are failing.

## Git

Invoke and follow the project-specific `$seedu-git-standard` skill whenever proposing or creating commits or naming
branches. All future commits in this project must follow that standard.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
