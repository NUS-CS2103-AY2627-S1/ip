# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Amateur level
* IDE and level of expertise: Amateur level

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

## UI testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change affects a recorded command, its expected output, or requires a new test case.
2. Invoke the project-specific `test-ui` skill to run the UI test plan. Report any failure, including the expected and actual output shown by the skill.

## JUnit test coverage

Maintain JUnit tests for approximately the top 50% of the codebase's highest-value methods, prioritizing complex, core, and critical business logic. Update or add the relevant JUnit tests after every code change so that the coverage target remains satisfied.

## Java coding standard

All Java source and test code must follow the project-specific `seedu-java-coding-standard` skill, based on the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Review touched Java files against that standard after every code change.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

All branch names and commit messages must follow the project-specific `seedu-git-standard` skill, based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Apply these rules to every future commit: use meaningful kebab-case branch names; write imperative, capitalized, period-free subjects no longer than 72 characters; and include a 72-column-wrapped body explaining what changed and why for non-trivial commits.
