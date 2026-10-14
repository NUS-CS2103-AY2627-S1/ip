# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner to intermediate; still building fluency in Java, Git, and command-line workflows.
* IDE and level of expertise: IntelliJ IDEA on macOS; beginner level, needs step-by-step guidance for project setup, running, testing, committing, tagging, and pushing.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On this macOS setup, IntelliJ is currently using OpenJDK 25.0.1. If the terminal uses a different Java version, switch to the installed Java 25 version before compiling or running the project.

## UI testing

After each code update, check whether the UI test cases in `test/ui-test-plan.md` need to be updated to reflect the changed behavior. If needed, update the test plan before running tests.

After each code update, invoke the project-specific `test-ui` skill so the command-line UI tests are run from `test/ui-test-plan.md` and the console input/output record is reported.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
