# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner

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

## Coding and Git conventions

For every Java code change, invoke and follow the project-specific
`$seedu-java-coding-standard` skill. The skill and conventions summarized below apply to all Java
code in this project. Consult the linked guides when a case is not covered here, and use the Google
Java Style Guide for Java topics that the Java guide does not cover.

Before creating or proposing any commit, invoke and follow the project-specific
`$seedu-git-standard` skill. All future commits and commit messages in this project must comply with
that skill. Do not commit or push unless the user explicitly asks.

* Java: <https://se-education.org/guides/conventions/java/intermediate.html>
* Git: <https://se-education.org/guides/conventions/git.html>

### Java summary

* Use lowercase package names, PascalCase noun names for classes and enums, camelCase verb names for methods, camelCase names for variables, and SCREAMING_SNAKE_CASE for constants.
* Use English names and comments. Name booleans to read as boolean values, such as `isVisible`, `hasData`, or `canEvaluate`, and use plural names for collections.
* Indent with 4 spaces, not tabs. Prefer lines shorter than 110 characters and never exceed 120 characters; indent wrapped lines by 8 additional spaces.
* Use K&R braces and braces around every loop and conditional body, including single-statement bodies.
* Put every class in a package. List imports explicitly rather than using wildcard imports, and keep their ordering consistent.
* Declare variables in the smallest practical scope and initialize them where they are declared. Keep fields non-public unless they are constants or belong to a behavior-free data class.
* Write Javadoc for public classes and public methods, except straightforward getters, setters, exact overrides, and test code. Preserve the project's stricter Javadoc requirements elsewhere in this file.

### Git summary

* Write commit subjects in the imperative mood, capitalize the first letter, and do not end them with a period.
* Aim for at most 50 characters in a commit subject and never exceed 72 characters. Add a scope or category prefix when it improves clarity.
* Give non-trivial commits a body separated from the subject by a blank line. Wrap body text at 72 characters and explain what changed and why, rather than narrating implementation details.
* Keep commits focused. If a commit message becomes overly long, split the work into smaller coherent commits.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods in the codebase. Prioritize
methods containing complex logic, core application behavior, validation, state changes, persistence,
and other critical business rules. Do not add tests solely to meet the target for trivial getters,
setters, constructors, or simple delegation methods.

After every code change, reassess the affected methods and update the JUnit tests as needed to keep
the test suite accurate and compliant with this 50% high-value-method coverage target. Run the
applicable Gradle test task and resolve any failures before considering the change complete.

## UI testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds or modifies user-visible console behavior or affects existing test coverage.
2. Invoke the project-specific `$test-ui` skill. Run the applicable UI test cases and follow the skill's stop-on-first-failure and reporting requirements.

Do not skip invoking `$test-ui` merely because the test plan did not require changes.

## Visual change review

After every project file change:

1. Invoke the project-specific `$present-changes-visually` skill.
2. Generate a review of all worktree changes against `HEAD` at `_temp/visual-diff.html`.
3. Provide a clickable absolute path using forward slashes so the user can review the page in the in-app browser.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
