# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 6+ years of Java experience, beginner to Git
* IDE and level of expertise: IntelliJ, familiar

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this project MUST follow the project skill `$seedu-java-coding-standard`, based on the [SE-EDU Java basic + intermediate coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Apply it when writing, modifying, or reviewing Java code. In particular, enforce its naming, layout, import, scoping, encapsulation, braces, and Javadoc requirements; report any existing violations with absolute file paths and line numbers.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing after code updates

After every update to the application's code:

1. Review `test/ui-test-plan.md` and update it when the changed behavior adds, removes, or changes a console UI scenario or its expected output.
2. Invoke the `$test-ui` skill to run the planned console UI tests and record the session. If a test fails, stop the test session as required by that skill and report the failure before considering the code update complete.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
