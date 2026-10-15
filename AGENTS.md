# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner

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

Invoke the project-specific `seedu-java-coding-standard` skill whenever creating, editing, formatting, or reviewing Java code. All Java code must follow the SE-EDU basic and intermediate Java coding standard described by that skill.

## UI testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the code change alters user-visible behavior or requires additional test coverage.
2. Invoke the project-specific `test-ui` skill and run its complete UI test plan.
3. If a test fails, stop the test session immediately and report the actual and expected outputs as directed by the skill.

## JUnit testing

* Maintain JUnit tests for at least the top 50% highest-value methods, prioritizing complex, core, and critical business logic.
* After each code change, update the JUnit tests as needed to keep that coverage target and run the complete JUnit test suite.

## Git

Invoke the project-specific `seedu-git-standard` skill whenever proposing or creating a commit message. All future commit messages must follow the SE-EDU Git conventions described by that skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
