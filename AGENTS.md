# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 2/10
* IDE and level of expertise: 2/10

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

Invoke and follow the project-specific `$seedu-java-coding-standard` skill for every change or review involving Java code. All production and test Java code must comply with the SE-EDU basic and intermediate Java coding standard captured by that skill.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value candidate methods. Prioritize methods containing complex logic, core business behavior, important state changes, persistence, parsing, validation, or error handling. Avoid adding low-value tests solely to increase the number of tested methods.

After every code change:

1. Review the affected methods and update or add JUnit tests as needed to continue meeting the 50% target.
2. Run the JUnit test suite using `./gradlew test` with Java 25. If a test fails, stop and report the failure before continuing.

## UI testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds or modifies user-visible console behavior, commands, or expected output.
2. Invoke the project-specific `$test-ui` skill and run its UI test plan. If a test fails, stop and report the expected and actual output as directed by the skill.

## Git

Invoke and follow the project-specific `$seedu-git-standard` skill whenever proposing or creating commit messages or branch names. All future commits must comply with the SE-EDU Git conventions captured by that skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
