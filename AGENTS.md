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

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

Follow the project-specific skill `seedu-java-coding-standard` for all Java code in this repository. The project must comply with the SE-EDU Java coding standard (basic + intermediate), including naming conventions, layout, statements, and documentation requirements.

## Git conventions

Follow the project-specific skill `seedu-git-standard` for all future commits in this repository. All commit subjects and bodies must comply with the SE-EDU Git conventions.

## Testing coverage target

Maintain a minimum JUnit test coverage target of 50% for the highest-value business logic in the project. JUnit tests must be reviewed and updated after each code change to keep this target and the relevant behavior checks in compliance.

## UI testing workflow

After each code update that affects user-visible CLI behavior:
1. Review whether `test/ui-test-plan.md` needs a matching update.
2. Invoke the project skill `test-ui` via:
   `python3 .codex/skills/test-ui/scripts/run-ui-tests.py --plan test/ui-test-plan.md`
3. If a case fails, stop immediately and report both actual and expected outputs.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
