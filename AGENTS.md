# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: >5000 lines of code
* IDE and level of expertise: I use them regularly in my coding (more than 1000 lines of code)

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

For every task that writes, edits, or reviews Java source or test code, invoke the project-specific `$seedu-java-coding-standard` skill before working with the code. All Java source and test code in this project must follow that skill's SE-EDU basic and intermediate coding rules. Use the Google Java Style Guide for topics the skill does not cover.

## Verification after code updates

After every source-code update and before considering the task complete:

1. Review `test/ui-test-plan.md` and update it when the change affects test aims, setup, inputs, expected outputs, or required coverage.
2. Invoke the project-specific `$test-ui` skill and run the documented UI test session.

Show the resulting console input/output record in the handoff. If a test fails, report the actual and expected outputs and do not run later test cases in that session, as required by the skill.

## Git

Before proposing or creating any commit message or branch, invoke the project-specific `$seedu-git-standard` skill. All future commits in this project must follow that skill's SE-EDU subject and body conventions, and all future branch names must follow its branch-naming conventions.
Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
