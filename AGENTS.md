# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: >10000 Loc
* IDE and level of expertise: I use them regularly in my coding (more than 1000 Loc)

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

For every Java code creation, modification, or review in this project, invoke and follow the project-specific `seedu-java-coding-standard` skill. Apply its rules to the changed code before completing the task.

## Console UI testing

After every code update, review `test/ui-test-plan.md` and update it whenever the change adds, removes, or alters observable console behaviour. Then invoke the project-specific `test-ui` skill to run the UI test plan. The skill must run before reporting the code update as complete; if a test fails, report the failure rather than proceeding with further test cases.

## JUnit testing

After every code update, review and update the JUnit tests in `src/test/java` as needed. Maintain tests for approximately the highest-value 50% of candidate methods, prioritizing complex logic, core application behaviour, state-changing operations, and error handling over trivial getters or display-only formatting. Run the JUnit test suite before reporting the update as complete.

## Git

Before preparing or creating any commit, invoke and follow the project-specific `seedu-git-standard` skill.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
