# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Amateur
* IDE and level of expertise: Amateur

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard:

All Java code in this project must follow the project-specific [seedu-java-coding-standard](/Users/tat/NUS/ip/.codex/skills/seedu-java-coding-standard/SKILL.md), based on the SE-EDU basic + intermediate Java coding standard. Apply it to new code and when modifying existing code.

## Git standard:

All future commits and branch names must follow the project-specific [seedu-git-standard](/Users/tat/NUS/ip/.codex/skills/seedu-git-standard/SKILL.md), based on the SE-EDU Git conventions. Do not commit or push unless explicitly asked.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI regression testing

After every code update, review `test/ui-test-plan.md` and update it when the UI behaviour or its test coverage has changed. Then invoke the project-local `$test-ui` skill to run the plan. Record the resulting console session in `test/ui-test-session.md`; if a test fails, stop at that failure and report the expected and actual output before making further changes.

## JUnit test coverage

Maintain JUnit tests for at least the top 50% of the highest-value methods, prioritising complex, core, and critical business logic. After every code change, review the relevant JUnit tests and add or update them as needed to keep this coverage target satisfied. Run the Java 25 test suite after test changes and code changes.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
