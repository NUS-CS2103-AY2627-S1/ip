# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basics up to CS2030s in java proficiency
* IDE and level of expertise: IntelliJ with Vim key bindings, new to IntelliJ

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

Use the project-local `.codex/skills/seedu-java-coding-standard` skill for every new or modified Java file. It is mandatory whenever code is generated or changed: apply it before compiling or testing, then review the complete diff for naming, layout, imports, braces, encapsulation, and production Javadocs. Use the SE-EDU basic and intermediate Java coding standard linked by that skill, with the Google Java Style Guide as the fallback for uncovered topics.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing

After each code update, review `test/ui-test-plan.md` and update it when the change affects the command-line interface or its expected output. Then invoke the repository-local `test-ui` skill to run the recorded UI test cases and preserve the generated console-session record. Stop and report the first failing case; do not continue the test session after a failure.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
