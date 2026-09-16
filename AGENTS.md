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

## Java coding standard

Follow the [SE-EDU Java basic + intermediate coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use four spaces for indentation, K&R braces, explicit and consistently ordered imports, camelCase names for variables and methods,
PascalCase names for classes, and SCREAMING_SNAKE_CASE names for constants. Keep lines at or below 120 characters and preferably
below 110 characters. Separate logical code blocks with blank lines and write clear English comments using American spelling.

The existing default-package layout is intentional for this starter project and should be preserved unless a task explicitly requests
a package migration.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI regression testing

After every Java code update, update `test/ui-test-plan.md` if the console behavior changed, then invoke the project `test-ui` skill. Do not report the update as verified unless its UI test plan passes with Java 25. If Java 25 is unavailable, report that blocker instead of using another Java version.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
