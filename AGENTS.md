# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium Low
* IDE and level of expertise: Medium

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java production and test code must follow the SE-EDU basic and intermediate
Java conventions. Use the project-specific `seedu-java-coding-standard` skill
when reviewing or changing Java code, including checks for naming, layout,
imports, control-flow braces, line length, and JavaDoc. Correct violations in
the touched code before handing the change back.

## UI testing after code changes

After every code update, review `test/ui-test-plan.md` and update it when the
change adds, removes, or alters user-visible behavior. Then invoke the
project-specific `test-ui` skill to run the documented UI test plan before
handing the change back to the user. If a UI test fails, stop and report the
actual and expected output; do not silently change the expected output to make
the test pass.

## JUnit coverage after code changes

Maintain JUnit tests for the highest-value approximately 50% of methods, with
priority given to complex, core, and critical business logic. Update or add the
relevant JUnit tests after every code change so the project continues to meet
this coverage target, and run the Gradle test task before handing changes back.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Git commit messages

All future commits must follow the SE-EDU Git conventions. Use the project-
specific `seedu-git-standard` skill when proposing or writing commit messages:
use a capitalized imperative subject (preferably 50 characters, no more than
72, without a final period), and for non-trivial changes add a blank-line-
separated body wrapped at 72 characters that explains what changed and why.
