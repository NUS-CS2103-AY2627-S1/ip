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

## Console UI testing

After every change to application code under `src/main/java`:

* Review `test/ui-test-plan.md` and update it when the change adds, removes, or modifies commands, accepted inputs, or observable console output. Derive suitable test cases and expected outputs from the intended requirements; do not copy actual output into the plan merely to make a failing test pass.
* Invoke the project-specific `test-ui` skill and run the complete UI test plan, even when the test plan itself did not need an update.
* Include normal cases and relevant edge or error cases in proportion to the behavior changed. Preserve unrelated existing test cases.
* Stop at the first test failure, as required by the skill, and resolve or report the mismatch before treating the code update as complete.
* Report whether the test plan changed and provide the resulting console test transcript or failure record to the user.

## JUnit testing

After every change to application code under `src/main/java`:

* Maintain JUnit tests for approximately the top 50% highest-value methods. Prioritize complex, core, and critical business logic over trivial getters, constructors, and simple wrappers.
* Add or update test classes under `src/test/java` using the same package structure as the production classes they test.
* Cover normal behavior, important boundary cases, and relevant invalid inputs in proportion to the changed behavior.
* Run the complete Gradle JUnit test suite and resolve or report failures before treating the code update as complete.
* Derive expected results from the intended requirements; do not weaken assertions merely to make a failing test pass.

## Java coding standard

For every Java code change or review:

* Invoke the project-specific `seedu-java-coding-standard` skill.
* Follow the SE-EDU basic and intermediate Java coding standard for all Java code in this project.
* Use the Google Java Style Guide only for topics not covered by the SE-EDU standard.
* Resolve relevant violations in changed code before treating the work as complete.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Invoke the project-specific `seedu-git-standard` skill whenever proposing, creating, amending, or reviewing a commit or merge message.
Follow the skill's subject and body requirements for all future commits and merges.
Do not commit or push unless explicitly asked.
