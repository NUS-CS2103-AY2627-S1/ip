# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: beginner
* IDE and level of expertise: beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this project, including tests, must follow the project skill
[`seedu-java-coding-standard`](.codex/skills/seedu-java-coding-standard/SKILL.md).
Load and apply that skill before creating, editing, or reviewing Java code.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI test maintenance

After every code update, review `test/ui-test-plan.md` and update it when the change affects command inputs, expected console output, or UI behaviour. Then invoke the project `test-ui` skill at `.codex/skills/test-ui/SKILL.md`, run its console-test runner, and report its transcript and result. Do this before handing the change back to the user.

## JUnit test coverage maintenance

Maintain JUnit tests for approximately the top 50% highest-value methods: prioritize complex parsing, storage, command execution, validation, and task-state behavior over simple constructors or output-forwarding methods. After every code change, review and update the relevant JUnit tests so this coverage target continues to be met. Run the Gradle test suite and report its result before handing the change back to the user.

## Git

All future branch names and commit messages must follow the project skill
[`seedu-git-standard`](.codex/skills/seedu-git-standard/SKILL.md). Load and
apply that skill before preparing or creating a commit.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
