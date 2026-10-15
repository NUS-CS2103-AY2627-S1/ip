# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Have worked on different small projects in multiple modules
* IDE and level of expertise: Only use IDE for its text editing and running

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

## Java coding standard:

All Java code (main and test) in this project must follow the project-specific `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`, which encodes the SE-EDU intermediate Java coding standard. Consult it before writing or editing any `.java` file, and when reviewing code or discussing style/conventions.

## Console UI testing:

After every code update, update `test/ui-test-plan.md` when the observable console behaviour changes, then invoke the project-specific `test-ui` skill at `.codex/skills/test-ui/SKILL.md`. The skill runs the plan and records each console input/output session; stop at the first failed case.

## Git

Use lightweight tags unless the user requests an annotated tag.
All commit messages and branch names in this project must follow the project-specific `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`, which encodes the SE-EDU Git conventions (subject line format, body structure, branch naming). Consult it before drafting or creating any commit message or branch.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
