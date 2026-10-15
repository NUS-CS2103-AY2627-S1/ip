# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: I would say I am a basic programmer with about 1 year of programming experience
* IDE and level of expertise: IntelliJ and about 4/10 expertise

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

## Java coding standard

All Java production code and tests in this repository must follow the project-specific skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`, based on the SE-EDU basic-plus-intermediate
Java coding standard. Apply it whenever Java code is created, edited, reviewed, or refactored.

## Git standard

All future commits and branch names in this repository must follow the project-specific skill at
`.codex/skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git conventions. Apply it before
proposing or creating any commit, and do not commit or push without explicit user authorization.

# Project-specific requirements

## Code-update validation

After every code update:

1. Update the JUnit tests affected by the change. Maintain unit-test coverage for approximately the top 50% of methods by prioritizing complex, core, or critical business logic. JUnit tests need to be updated after each code change to comply with this target.
2. Review `test/ui-test-plan.md` and add or update test cases, inputs, or expected outputs when the change affects the console UI. If the plan remains accurate, no test-plan edit is needed.
3. Invoke the project-specific `$test-ui` skill from the repository root, using the test plan as the source of truth.
4. Report the test session result and console transcript. The session must stop at the first failed test.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
