# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner to Intermediate
* IDE and level of expertise: IntelliJ IDEA — Beginner to Intermediate; VS Code — Beginner to Intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard:

All Java source and test code in this project MUST follow the project-specific
`seedu-java-coding-standard` skill, based on the SE-EDU basic and intermediate
Java coding standard: <https://se-education.org/guides/conventions/java/intermediate.html>.
Use the Google Java Style Guide for topics not covered there. Apply the standard
to every new or modified Java file and correct existing violations when working
in the affected code.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing after code updates:

After every code update, review `test/ui-test-plan.md` and update its test cases when the change affects the UI commands, inputs, or expected console output. Then invoke the project-specific `test-ui` skill (`$test-ui`) even when the test plan did not need changes. Include the resulting console input/output transcript in the handoff. If a test fails, stop the session immediately and report the actual and expected outputs before making further code changes.

## JUnit test coverage target:

Maintain JUnit tests for approximately the top 50% of methods by testing the highest-value logic first, especially complex, core, or critical business behavior. Update the relevant JUnit tests after every code change to keep them compliant with this coverage target; add or revise tests whenever the changed behavior affects a covered method or introduces new high-value logic.

## Git

All future commits in this project MUST follow the project-specific
`seedu-git-standard` skill, based on the SE-EDU Git conventions:
<https://se-education.org/guides/conventions/git.html>. This includes commit
subject and body format, rationale-focused messages, and meaningful kebab-case
branch names. Apply these rules whenever proposing, creating, or amending a
commit. Do not commit or push unless explicitly asked.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
