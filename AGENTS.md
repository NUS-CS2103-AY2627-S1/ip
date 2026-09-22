# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: decent
* IDE and level of expertise: decent

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every production and test Java code change in this project, use the project-local `seedu-java-coding-standard` skill and follow its SE-EDU intermediate Java coding-standard rules.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing after code updates

After every code update, complete these steps before handing off the change:

1. Review `test/ui-test-plan.md` and update it when the change affects a command, console output, supported input, or UI test coverage.
2. Invoke the project-local `test-ui` skill to run the recorded UI test cases. Follow its fail-fast reporting rules and include the console-session record in the handoff.

## JUnit test coverage

Maintain JUnit tests for approximately the top 50% highest-value production methods. Prioritize methods with complex control flow, core domain behaviour, parsing, persistence, validation, and other critical business logic; do not spend the target on trivial constructors, accessors, constants, or output wrappers.

After every production code change, review and update the relevant JUnit tests so the changed behaviour remains covered and the 50% target continues to focus on the highest-value methods. Run the JUnit suite before handing off the change.

## Javadoc coverage

For production Java code added or modified from this point forward, maintain Javadoc comments on at least half of all non-private classes, constructors, and methods. Check this coverage before handing off a code change; document non-private APIs whose purpose or behaviour is not already obvious.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
