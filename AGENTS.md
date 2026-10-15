# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Some programming experience on small projects at a previous internship
* IDE and level of expertise: IntelliJ IDEA, not very familiar with the IDE

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

## Java coding standard:

Follow the project-specific `seedu-java-coding-standard` skill for all Java code in this repository. All new, modified, and refactored code must strictly adhere to the SE-EDU Java Coding Standard (Basic + Intermediate rules) and the Google Java Style Guide.

## UI testing after code updates

After every code update, review `test/ui-test-plan.md` and update it when the change adds, changes, or removes observable console behavior. Each affected test case must continue to specify its aim, inputs, and exact expected output.

Then invoke the project-specific `test-ui` skill to run the UI test plan. Follow its fail-fast behavior: stop at the first failed case, report its actual and expected outputs, and include the console input/output record in the result. Do not update expected output merely to make a test pass; change it only when the user confirms that the behavior is intended.

## Unit testing and test coverage target

Maintain a test coverage target of ~50% focusing on the highest-value methods (prioritizing complex, core, or critical business logic).

After every code change, review and update the JUnit tests to comply with this 50% test coverage target. Ensure new or modified candidate methods have corresponding unit tests following Gradle and JUnit conventions (e.g., test methods named `featureUnderTest_testScenario_expectedBehavior()`), and run `./gradlew test` to verify that all unit tests pass.

## Git

Follow the project-specific `seedu-git-standard` skill for all Git commits, branch naming, and repository workflows in this repository.
All future commit messages must strictly adhere to the SE-EDU Git conventions:
- Subject line: imperative mood, capitalized, no trailing period, $\le 50$ chars (hard limit: 72 chars).
- Body: separated by a blank line, wrapped at 72 chars, explaining WHAT and WHY (not HOW) using the structured flow ({current situation}, {why it needs to change}, {what is being done}, {why done that way}).
Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
