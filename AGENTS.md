# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this project must follow the project-specific `seedu-java-coding-standard` skill, based on the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Apply it to new and
modified production and test code, including naming, layout, braces, imports, line length, and Javadoc requirements.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI test maintenance

After every update to Java source code or other code that can affect the chatbot's behaviour:

1. Review and update `test/ui-test-plan.md` when its test cases, commands, or expected console output are affected.
2. Invoke the project-local `$test-ui` skill to run the documented UI tests.

If the test environment is unavailable, report the reason and do not claim that the UI tests passed.

## JUnit coverage target

Maintain JUnit tests for approximately the highest-value 50% of methods, prioritizing complex, core, and critical business logic. After every code change, review and update the relevant JUnit tests to keep them compliant with this coverage target, and run the Gradle test task when the environment permits.

## Git

All future commits and branch names must follow the project-specific `seedu-git-standard` skill, based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Before committing, verify the subject,
body, and branch name against that skill. Do not commit or push unless explicitly asked.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
