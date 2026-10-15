# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Average
* IDE and level of expertise: IntelliJ, Amateur

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

For every Java source-code creation, modification, or review in this project, use the project-local `seedu-java-coding-standard` skill and follow its guidance.

## UI testing

After every source-code update:

1. Review `test/ui-test-plan.md` and update it when the change affects a command, its output, or another UI behavior covered by the plan.
2. Invoke the project-local `test-ui` skill and run the recorded UI test cases.
3. If a test fails, stop the test session as directed by the skill and report the expected output, actual output, and transcript path before making further code changes.

## Git

For every future branch creation, commit proposal, commit, or commit review, use the project-local `seedu-git-standard` skill and follow its guidance.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
