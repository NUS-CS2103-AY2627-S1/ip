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

For every Java creation, edit, or review, load and follow the project skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`.

## UI testing

After each code update that can affect command-line behavior:

1. Update `test/ui-test-plan.md` if its commands, inputs, or expected outputs have changed.
2. Invoke the project-local `test-ui` skill at `.codex/skills/test-ui` and report its test transcript.
3. For every command feature, include both valid and invalid inputs in the UI test plan. Place invalid inputs between valid ones when possible, then verify later output proves the invalid input did not change the task list.

## JUnit testing

Maintain JUnit tests for roughly the top 50% highest-value methods, prioritizing complex, core, and critical business logic. After each code change, update the relevant JUnit tests as needed to preserve this coverage target, and run them using Gradle.

## Git

Before proposing, reviewing, or creating any commit, load and follow the project skill at
`.codex/skills/seedu-git-standard/SKILL.md`.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
