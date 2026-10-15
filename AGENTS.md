# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: around 3 years.
* IDE and level of expertise: VSCode, familiar.

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

## Coding Standard:

Before creating, modifying, refactoring, or reviewing Java code, invoke the project-specific `seedu-java-coding-standard` skill. All Java code in this project must strictly adhere to that skill, which is based on the rules outlined in [https://se-education.org/guides/conventions/java/intermediate.html](https://se-education.org/guides/conventions/java/intermediate.html). Review every affected Java file for compliance before completing the task.

## Code update verification:

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds, removes, or changes user-visible command-line behavior or test coverage.
2. Invoke the project-specific `test-ui` skill and run the listed UI tests. Follow the skill's requirement to stop at the first failure and report the console transcript and any actual-versus-expected mismatch.

## JUnit test coverage:

Maintain JUnit tests for the highest-value methods covering at least the top 50% of the codebase's testing priorities, with preference for complex, core, or business-critical logic. Update or add the relevant JUnit tests after every code change so that the tests remain compliant with this coverage target.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
