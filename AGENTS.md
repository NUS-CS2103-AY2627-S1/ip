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

## Java coding standard

All Java source and test code in this project must follow the project-specific
`.codex/skills/seedu-java-coding-standard` skill, based on the SE-EDU basic and intermediate
Java coding standard. Apply it to new and modified code and use the linked standard for details
not repeated in the skill.

## UI testing

The project-specific `test-ui` skill is in `test-ui/SKILL.md`. After every code update, update `test/ui-test-plan.md` when user-visible behavior or test coverage requires it, then invoke `test-ui` by running `python test-ui/scripts/run_ui_tests.py test/ui-test-plan.md`. The runner records console input and output and stops at the first failed case.

## JUnit test coverage

Focus JUnit tests on approximately the top 50% of the highest-value methods, prioritizing complex, core, or critical business logic. Update the relevant JUnit tests after every code change so that this coverage target remains satisfied.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
