# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Has around half a year of experience in Java.
* IDE and level of expertise: Has around half a year of experience in using IntelliJ.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

## JavaDoc coverage

All production classes and public methods must have JavaDoc header comments. Non-trivial private methods must also have header comments. Methods whose names start with `get` or `set` are getters or setters and do not require JavaDoc headers. Comments may also be omitted for overridden methods when the parent method's JavaDoc applies exactly as written. Use the project's established JavaDoc format, including a concise third-person summary and relevant `@param`, `@return`, and `@throws` tags.

After every code change, review the affected classes and methods and update their JavaDoc headers so this coverage target remains satisfied. JavaDoc-only changes do not require updates to the console UI test plan, but code changes must still follow the usual UI and JUnit verification requirements below.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Console UI tests

After every code update, review `test/ui-test-plan.md` and update it whenever the change adds, removes, or alters observable console behaviour. Then invoke the project-specific `test-ui` skill to run the relevant UI tests. Include the recorded console input/output and the pass/fail result in the handoff; if a test fails, stop the UI test session and report the expected and actual outputs.

## JUnit test coverage

Maintain JUnit tests for at least the top 50% of the codebase's highest-value methods, prioritizing complex, core, or business-critical logic. After every code change, review the relevant JUnit tests and update or add tests so that they continue to cover the changed behavior and comply with this 50% target. Run the Gradle JUnit test suite when handing off code changes and report the result or any environment limitation.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
