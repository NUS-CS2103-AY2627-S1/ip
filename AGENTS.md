# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Can program well enough
* IDE and level of expertise: Does not use an IDE, uses nvim

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

Before creating, editing, or reviewing Java code, use the project-specific
[`seedu-java-coding-standard`](.codex/skills/seedu-java-coding-standard/SKILL.md)
skill and follow [`docs/java-coding-standard.md`](docs/java-coding-standard.md).
This requirement applies to production and test code.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit test coverage

Maintain JUnit tests for approximately the top 50% highest-value production
methods. Prioritize complex methods, core business logic, persistence, parsing,
validation, state changes, and failure recovery over trivial constructors and
simple getters.

After every production code change, reassess the affected behavior and update
or add JUnit tests as needed to continue meeting this target. Run the full test
suite with `./gradlew test` before finishing the change.

## Git

Before proposing or creating commits, branches, merges, or tags, use the
project-specific
[`seedu-git-standard`](.codex/skills/seedu-git-standard/SKILL.md) skill. All
future commits must follow that skill's SE-EDU conventions.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
