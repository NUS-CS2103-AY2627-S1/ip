# Project context

## Mandatory Java coding standard

All Java code in this repository MUST follow the project skill
`.codex/skills/seedu-java-coding-standard` and the SE-EDU Java basic + intermediate
standard at https://se-education.org/guides/conventions/java/intermediate.html.
Apply it to new code, modified code, tests, and refactors. In particular, enforce
the naming, import ordering, four-space/K&R layout, 120-character line limit,
explicit imports, narrow variable scope, braces on all control flow, encapsulation,
and required public API Javadocs described by that skill.

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Low to medium
* IDE and level of expertise: Low

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

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## JUnit test coverage

Maintain JUnit tests for approximately the highest-value 50% of methods, prioritizing
complex, core, or business-critical logic. Update the relevant JUnit tests after every
code change so the test coverage remains compliant with this 50% target.

## UI testing

After every code update that can affect Gooble's console behavior, review `test/ui-test-plan.md` and update it when the expected UI behavior changes or a new command is added. Then invoke the project-local `test-ui` skill and run its complete test plan before reporting the change as complete. Keep regression cases for the greeting and exit flow, adding and listing tasks, marking and unmarking tasks, and every supported task type.
