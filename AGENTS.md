# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Adequate
* IDE and level of expertise: Adequate

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

Use the project-specific `seedu-java-coding-standard` skill for every Java code change or Java style review. All Java in this project must follow the SE-EDU basic and intermediate Java coding standard.

## UI testing

After every code update:

1. Update `test/ui-test-plan.md` when the change adds, modifies, or removes user-visible behaviour or relevant test coverage.
2. Invoke the project-specific `test-ui` skill and act on its result before reporting the change as complete.

## JUnit testing

Maintain JUnit coverage for approximately the top 50% highest-value methods in the codebase. Prioritize methods that contain complex logic, implement core business behaviour, or protect critical data and invariants; do not treat this as a raw line-coverage target.

After every code change, review the affected methods and update or add JUnit tests as needed to keep this target satisfied. Run the relevant Gradle JUnit tests and resolve any failures before reporting the change as complete.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
