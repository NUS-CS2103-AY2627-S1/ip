# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner to Intermediate Level
* IDE and level of expertise: Beginner to Intermediate Level

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

Before creating, editing, refactoring, or reviewing Java code, invoke the
project-specific `$seedu-java-coding-standard` skill. All production and test
Java code must follow the SE-EDU basic and intermediate Java coding standard
described by that skill.

## Code-change verification

After every update to repository files:

* Invoke the project-specific `$present-changes-visually` skill and report the generated visual diff.

After every update to production code:

* Review `test/ui-test-plan.md` and update it when the change affects observable console behavior or requires new UI coverage.
* Invoke the project-specific `$test-ui` skill and report the result. Do not claim the code update is complete if the UI test session fails.
* Preserve LuigiBot's existing Luigi-style personality in all user-facing messages unless the user explicitly requests a wording or personality change.
* When an approved change intentionally alters console wording, update the corresponding expected output in `test/ui-test-plan.md` before invoking `$test-ui`.

## Git

Before proposing, reviewing, or creating commit messages, commits, or branch
names, invoke the project-specific `$seedu-git-standard` skill. Every future
commit message and branch name must follow the SE-EDU Git conventions
described by that skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
