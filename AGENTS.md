# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Several years in web development, algorithms and data analysis. Proficient in python, java, javascript.
* IDE and level of expertise: Used several IDE is before including Python IDE and Dev-CPP

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## JUnit test coverage

Target JUnit coverage for the top approximately 50% of methods, prioritizing
complex, core, and business-critical logic over trivial accessors or boilerplate.
After every code change, review the relevant JUnit tests and add or update them
to cover the changed behavior so that the target remains satisfied. Run the
Gradle test task after updating the tests.

## Console UI testing

After each code update:

1. Update test/ui-test-plan.md when the change affects an existing console behavior or adds a new one.
2. Invoke the project-specific $test-ui skill and run the plan. The skill must show each console input/output transcript and stop at the first failure.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use sdk use java 25.0.3.fx-zulu to switch to Java 25 if needed.

## Git

Use the $seedu-git-standard skill for all git operations. Enforce the SE-EDU conventions:

* **Commit subject**: 50 chars max (72 hard), imperative mood, capitalized, no trailing period, optional scope prefix.
* **Commit body**: Blank line after subject, 72-char wrap, explain WHAT and WHY, not HOW.
* **Branch names**: Kebab-case, e.g. refactor-ui-tests or 1234-ui-freeze-error.
* **Tags**: Lightweight unless annotated tags are explicitly requested.

When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Java coding standard

Use the $seedu-java-coding-standard skill for all Java code in this project. Enforce the SE-EDU conventions:

* **Naming**: Classes PascalCase, methods/fields camelCase, constants UPPER_SNAKE_CASE
* **Formatting**: 4-space indentation, 120-char line width, braces on same line
* **Documentation**: Javadoc for all public members, inline comments for WHY
* **Code organization**: Proper visibility, avoid magic numbers, fail fast with validation

All Java code must follow these conventions.
