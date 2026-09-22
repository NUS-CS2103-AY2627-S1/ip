# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner-Intermediate
* IDE and level of expertise: Beginner-Intermediate

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

## UI test validation

After every code update, review `test/ui-test-plan.md` and update it when the change adds, changes, or removes console UI behaviour. Then invoke the project-specific `$test-ui` skill to run the recorded UI test cases. Report the resulting console-session record; if a case fails, stop testing immediately and report its expected and actual outputs.

## JUnit test coverage

Maintain JUnit tests for at least the top 50% of methods by testing value, prioritising complex, core, and critical business logic over trivial accessors. Update the relevant JUnit tests after each code change so that this target continues to be met.

## Coding standard

Invoke the project-specific `$seedu-java-coding-standard` skill whenever creating, changing, or reviewing Java code. Follow it for all Java code in this project.

## Git

Invoke the project-specific `$seedu-git-standard` skill whenever proposing, reviewing, or creating commit messages or branch names. Follow it for all future Git commits in this project.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
