# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium (started in high school)
* IDE: Visual Studio Code

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

## Task decomposition and verification

For non-trivial tasks:

1. Break the work into smaller, independently verifiable problems where practical.
2. For each problem:
   1. Identify expected behavior, including normal cases, boundaries, edge cases, invalid input, and likely failure modes.
   2. Make the smallest appropriate change.
   3. Run the relevant tests and check that existing behavior remains intact.
3. If the user explicitly requests commits, make small, logically grouped commits after verifying each meaningful increment. Do not commit or push without explicit authorization.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standards

Before editing or reviewing Java code, invoke the project-specific `seedu-java-coding-standard` and `java-coding-standards` skills, then run the latter's `scripts/check_java_format.py` checker. Run the checker again after Java edits, and fix any reported violations. Follow the SE-EDU course-standard-first guidance, using Google Java Style only where the course standard is silent. After Java edits, format the changed code with Visual Studio Code's Format Document when available and inspect the diff; do not configure its Google Style formatter profile because it conflicts with this project's four-space indentation and 120-character limit.

Keep JUnit coverage focused on the top ~50% highest-value methods: prioritize core, complex, and critical behavior. Update the relevant JUnit tests after each code change to maintain this target.

## Git

Use lightweight tags unless the user requests an annotated tag.
Before proposing, validating, or creating any commit, invoke the project-specific `seedu-git-standard` and `git-commit-messages` skills. Ensure the final message complies with their subject and body requirements, revising a user-supplied message when necessary before committing.
Do not commit or push unless explicitly asked.

## UI testing after code changes

After every code update:

1. Update `test/ui-test-plan.md` with new test cases for every new feature. Include normal usage, boundary conditions, edge cases, malformed commands, invalid inputs, and other likely failure modes relevant to the change.
2. Interleave positive and negative test cases where practical. Negative cases must be placed between positive cases when they exercise the same state, so the suite can detect invalid input accidentally changing or corrupting internal state.
3. Invoke the project-specific `test-ui` skill by running `skills/test-ui/scripts/run_ui_tests.py` from the repository root.

Report the test result after the update. If the test plan does not need changes, state that it was reviewed and left unchanged.
