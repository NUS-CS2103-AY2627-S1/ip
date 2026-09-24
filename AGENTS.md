# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [beginner]
* IDE and level of expertise: [beginner]

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

## Project standards:

All Java source code in this project, including production and test code, MUST
follow the project skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`.
That skill is based on the SE-EDU basic and intermediate Java coding standard;
use it for every Java implementation, review, and refactor.

All future commits MUST follow the project skill at
`.codex/skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git
conventions. Before committing, propose and review the commit message.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## UI test-plan maintenance

After every application code update, review and update `test/ui-test-plan.md` when the user-visible console behavior or test coverage changes. Then invoke the project-local `test-ui` skill to run the documented UI tests. Do not change application code while updating or running the test plan unless the user explicitly asks for an implementation change.
