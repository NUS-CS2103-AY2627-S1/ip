# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Junior SWE
* IDE and level of expertise: IntelliJ, Beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java code change in this project, load and follow the project-specific
`seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard/`.
Review existing code touched by the change for compliance with that standard.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit test maintenance

Maintain JUnit tests for approximately the top 50% highest-value methods across all classes. Prioritize complex methods, core business logic, critical workflows, boundary conditions, and error handling; do not add tests solely to cover trivial constructors, getters, setters, or simple delegation.

After every code update, review and update the JUnit tests so that new or changed behaviour continues to comply with this coverage target. Run the complete JUnit test suite before reporting the code update as complete.

## UI test maintenance

After every code update, review `test/ui-test-plan.md` and update its test cases when the changed behaviour, commands, or console output require it. Then invoke the project-specific `$test-ui` skill to compile and run the planned console UI tests. Do this before reporting the code update as complete.

If a UI test fails, stop the test session immediately as required by the skill, and report the recorded actual and expected outputs. Do not continue with later test cases.

## Git

Before proposing or creating any commit in this project, load and follow the
project-specific `seedu-git-standard` skill in
`.codex/skills/seedu-git-standard/`. Ensure every commit message complies with
that standard.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
