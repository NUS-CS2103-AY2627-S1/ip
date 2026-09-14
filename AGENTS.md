# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium
* IDE and level of expertise: Medium

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

For every Java creation, edit, refactor, or review, invoke and follow the project-specific `$seedu-java-coding-standard` skill at `.agents/skills/seedu-java-coding-standard/SKILL.md`. All production and test Java code must comply with that skill before it is handed back to the user.

## UI testing

After every code update, before handing the work back to the user:

1. Review `test/ui-test-plan.md` and update its test aims, inputs, or expected outputs when the code change affects UI behavior or requires additional coverage. Leave it unchanged only when the existing cases remain accurate and sufficient.
   For command parsing or error-handling changes, add incorrect and edge-case inputs, interleave them with valid commands, and verify afterward that rejected inputs did not change internal state.
2. Invoke the `$test-ui` skill to run the recorded UI test plan. A separate compilation or manual run does not replace this step.
3. If a UI test fails, stop the test session immediately and report the failing case together with its actual and expected outputs. Do not report the code update as complete unless the tests pass or the user explicitly accepts the failure.
4. Include the generated console input/output session record in the final handoff.

## JUnit testing

After every code change, review the affected classes and update their JUnit tests as needed. Maintain tests for approximately the 50% highest-value methods, prioritizing complex, core, and critical business logic rather than trivial accessors. Run `./gradlew test` before handing the work back to the user.

## Git

Use lightweight tags unless the user requests an annotated tag.
Before proposing, preparing, reviewing, or creating any commit, invoke and follow the project-specific `$seedu-git-standard` skill at `.agents/skills/seedu-git-standard/SKILL.md`. Every commit must comply with that skill.
Do not commit or push unless explicitly asked.
