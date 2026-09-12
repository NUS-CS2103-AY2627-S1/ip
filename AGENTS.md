# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: beginner to intermediate
* IDE and level of expertise: IntelliJ IDEA, beginner

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

## Code updates and UI testing

After every update to application or test code:

1. Review `test/ui-test-plan.md` and update it when the code change adds or changes user-visible behavior, commands, or expected console output.
2. Invoke the project-specific `$test-ui` skill before handing the completed change back to the user, even when the existing test plan does not need modification.

If `$test-ui` reports a failure, follow its fail-fast instructions and report the failure rather than claiming that the code update is complete.

## JUnit coverage

Maintain a target of approximately 50% method coverage, prioritizing complex, core,
or business-critical methods. After every application code change, review the
affected methods and add or update the corresponding JUnit tests so that this
coverage target remains satisfied. Run the JUnit suite with the Gradle wrapper
(`./gradlew test` or `gradlew.bat test`) before handing the change back to the user.

## Java coding standard

All Java production and test code must follow the project-specific
`seedu-java-coding-standard` skill, based on the SE-EDU basic and intermediate
Java coding standard. Apply the skill when writing, reviewing, or refactoring
Java, including package naming, imports, naming, layout, braces, encapsulation,
and Javadoc requirements.

## Git

All branch names and commit messages must follow the project-specific
`seedu-git-standard` skill, based on the SE-EDU Git conventions. Before every
commit, apply that skill, inspect the staged diff, and verify the subject and
body meet its length, imperative-mood, formatting, and what/why requirements.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
