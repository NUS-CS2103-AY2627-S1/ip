# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Non-existent
* IDE and level of expertise: What is an IDE?

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Coding and Git standards

All Java code and tests in this project must follow the project skill
`.codex/skills/seedu-java-coding-standard`. All future commits must follow
`.codex/skills/seedu-git-standard`, including imperative subject lines and
appropriate explanatory bodies.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## JUnit coverage target

Focus JUnit coverage on approximately the highest-value 50% of candidate methods in each changed class, prioritizing complex, core, or critical business logic. Update the relevant JUnit tests after every code change so that the tests continue to meet this 50% target.

## UI testing after code changes

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds or alters observable command-line behavior.
2. Invoke the project-specific `test-ui` skill at `.codex/skills/test-ui`.
3. Run the documented UI tests with Java 25. If a test fails, stop immediately and report the actual and expected output.
