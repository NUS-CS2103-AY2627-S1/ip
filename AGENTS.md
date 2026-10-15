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

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Wrap every commit message body line at 72 characters or fewer, and verify the wrapping before committing.
Do not commit or push unless explicitly asked.

## Coding and Git standards (mandatory)

* All Java code in this repository (including AI-generated code) must follow the SE-EDU Java coding standard (basic + intermediate). Before writing, editing, formatting, or reviewing Java code, load and follow the `seedu-java-coding-standard` skill. For topics it does not cover, use the Google Java Style Guide.
* All commit messages and branch names in this repository must follow SE-EDU Git conventions. Before proposing or creating a commit message or branch name, load and follow the `seedu-git-standard` skill.

## UI test maintenance

If you change LenZaBot's console behavior, keep `test/ui-test-plan.md` in sync.

Examples of changes that require updating the UI test plan:
- new commands
- changed command parsing
- changed success messages
- changed error messages
- changed list formatting or task rendering

After such changes, run the `test-ui` skill workflow or the bundled runner to verify the updated plan against the program output.

## JUnit test maintenance

Maintain JUnit tests for roughly the top 50% highest-value methods, prioritizing complex, core, or critical business logic.
Update the relevant JUnit tests after every code change so that this coverage target remains satisfied.
Keep tests under `src/test/java` using the same package structure as the production code, and run them using Gradle.
