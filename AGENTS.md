# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Novice
* IDE and level of expertise: Novice

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

For every creation, modification, or review of Java code in this repository:

1. Invoke the project-specific `$seedu-java-coding-standard` skill.
2. Follow its SE-EDU basic and intermediate Java coding-standard rules for all production and test code.
3. Correct applicable coding-standard violations in the Java lines being changed and their directly related context.

## Required testing after code updates

After every update to the application code:

1. Review `test/ui-test-plan.md` and update it when the changed behavior requires a new or revised test case. Keep each test case's aim/rationale, input, and exact expected output in that file.
2. Invoke the project-specific `$test-ui` skill and run the UI test plan before reporting that the code update is complete.

If the test session fails, follow the skill's stop-on-first-failure rule and report the mismatch. Do not describe the code update as complete while its required tests are failing.

## Git

For every proposed or created commit and every new branch:

1. Invoke the project-specific `$seedu-git-standard` skill.
2. Follow its SE-EDU Git conventions for commit messages and branch names.
3. Keep each commit focused on one logical change instead of batching unrelated work.
4. Generate the project visual diff and show the exact proposed commit message before seeking approval.
5. Obtain the user's explicit approval immediately before creating each commit.

Use lightweight tags unless the user requests an annotated tag.
Do not push, merge, delete branches, or rewrite history unless explicitly asked.
