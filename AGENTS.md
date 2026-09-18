# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java and Git standards

* Before writing or reviewing Java source or tests, read and follow
  `.codex/skills/seedu-java-coding-standard/SKILL.md`. Apply its SE-EDU
  basic and intermediate rules to new code and code being changed.
* Before proposing or creating commits, read and follow
  `.codex/skills/seedu-git-standard/SKILL.md`. Keep Java changes and
  standalone agent-instruction changes in separate, cohesive commits.
* Preserve branch names explicitly requested for course increments.
* These skills do not authorize commits or pushes; the Git authorization
  rule below still applies.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing

After each code update:

* Update `test/ui-test-plan.md` when the change affects commands or console output.
* Preserve coverage for existing commands when refactoring the command parser.
* Interleave valid and invalid commands in error-handling cases, then list the tasks to verify that rejected input did not change application state.
* Invoke the project-specific `test-ui` skill to run the UI test plan.
* Stop and report the actual and expected output if a UI test fails.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
