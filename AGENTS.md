# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Starting Year 2 National University of Singapore Computer Science Student
* IDE and level of expertise: IntelliJ, beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this project must follow the project-specific
`.codex/skills/seedu-java-coding-standard/SKILL.md`, based on the SE-EDU Java basic and
intermediate coding standard. Apply it to production and test code, including package naming,
source paths, naming, layout, imports, braces, line length, variable scope, encapsulation, and
Javadocs.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All future commits in this project must follow
`.codex/skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git conventions. This includes
imperative commit subjects, subject length and punctuation, detailed WHAT/WHY bodies for
non-trivial commits, 72-character body wrapping, and meaningful kebab-case branch names.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## UI testing after code updates

After every code update:

1. Review `test/ui-test-plan.md` and update it when the changed behavior requires new or revised test cases.
2. Invoke the project-specific `test-ui` skill to run the UI tests in the plan.

The `test-ui` skill must be invoked even when no test-plan update is needed. Follow its stop-on-first-failure behavior and report the console input, actual output, and expected output for any failure.
