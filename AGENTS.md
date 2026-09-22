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

## Java coding standard

Invoke the project-specific `$seedu-java-coding-standard` skill whenever creating, editing, or reviewing Java code.
All Java code in this project must follow the SE-EDU basic and intermediate Java coding standard captured by that
skill. Review affected Java files for compliance after every code change.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods in the
codebase. Prioritize complex methods, core behavior, and critical business logic
over trivial getters, simple wrappers, or implementation details.

After every code change, review and update the JUnit tests as needed to continue
meeting this 50% coverage target. Run the JUnit suite through Gradle using Java
25, and include the test outcome in the handoff.

## Console UI testing

After every code update, review `test/ui-test-plan.md` and update it when the
change affects an existing console interaction or introduces a new one. Then
invoke the `test-ui` skill to run the recorded UI test cases. Include the
test-session outcome in the handoff; if a case fails, report its expected and
actual outputs and stop the UI test session as required by the skill.

## Git

Invoke the project-specific `$seedu-git-standard` skill whenever proposing or creating a commit, commit message, or
branch name. All future commits in this project must follow the SE-EDU Git conventions captured by that skill.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
