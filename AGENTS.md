# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium
* IDE and level of expertise: Medium

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

Invoke the project-specific `$seedu-java-coding-standard` skill whenever creating, editing, refactoring, or reviewing Java code, and ensure all Java code follows that skill.

## UI regression testing

After every code update in this repository:

1. Review `test/ui-test-plan.md` and update it when the change intentionally adds or changes console UI behavior, commands, test aims, inputs, or expected outputs. Do not change an expectation merely to make the current implementation pass. If no update is needed, explicitly report that the plan was reviewed and remains applicable.
2. Invoke the project-specific `$test-ui` skill and run the complete test plan before considering the code update complete. Follow the skill's fail-fast and console-transcript reporting requirements.

## Git

Use lightweight tags unless the user requests an annotated tag.
Invoke the project-specific `$seedu-git-standard` skill whenever proposing, creating, or reviewing Git commit messages or branch names, and follow it for all future commits.
Do not commit or push unless explicitly asked.
