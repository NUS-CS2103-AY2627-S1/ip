# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ, intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java code creation, edit, refactoring, or review in this repository, invoke and follow the project-specific `seedu-java-coding-standard` skill in `.agents/skills/seedu-java-coding-standard`. Treat all basic and intermediate rules captured by that skill as mandatory for production and test code. Use Google Java Style only for topics the SE-EDU standard does not cover.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods in the codebase. Prioritize methods that contain complex logic, implement core application behavior, enforce important validation rules, or protect critical data and state. Do not add low-value tests merely to increase the coverage count, such as tests for trivial getters with no logic.

After every code update:

1. Review the affected production methods and their existing JUnit tests.
2. Add or update tests as needed to keep the test suite aligned with the 50% high-value method coverage target.
3. Run the JUnit test suite through Gradle using Java 25 and report the result.

## UI regression testing

After every code update:

1. Review `test/ui-test-plan.md` and update its test cases and expected outputs when the intended console behavior has changed or needs additional coverage.
2. Invoke the project-specific `test-ui` skill and run the UI test plan.
3. Include the skill's console input/output record in the completion report. If a test fails, stop the test session immediately and report its actual and expected outputs.

Do not change an expected output merely to make a failing test pass. Update it only when the intended behavior has changed.

## Git

Before proposing, creating, or reviewing any commit or commit message, invoke and follow the project-specific `seedu-git-standard` skill in `.agents/skills/seedu-git-standard`. Treat its subject and body rules as mandatory for every future commit in this repository. Also follow its branch-naming rules when creating a branch unless the user specifies an exact branch name.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
