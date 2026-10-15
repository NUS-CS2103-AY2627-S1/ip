# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner to Intermediate 
* IDE and level of expertise: Beginner to Intermediate 

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

## Coding standard

All Java code in this project (writing new code, editing existing code, or reviewing it) must follow the `seedu-java-coding-standard` skill.

## Testing

Maintain at least 50% JUnit test coverage across the codebase.
Whenever production code changes, update the JUnit tests (adding, adjusting, or removing cases as needed) so coverage stays at or above that target — do not let the test suite go stale relative to the code it covers.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
All commit messages must follow the `seedu-git-standard` skill (subject/body format and branch naming).

## Custom Skills

### `test-ui`
Executes interactive console testing against defined test cases in `test/ui-test-plan.md`.

* **Inputs**: Reads test cases from `test/ui-test-plan.md`.
* **Execution**:
  1. Parse the test cases (aim, inputs, expected output).
  2. Run the application main class (`YapBot`).
  3. Pass the defined inputs to standard input (`stdin`).
  4. Compare the actual standard output (`stdout`) with the expected output.
* **Pass Condition**: If all tests pass, output the full console transcript.
* **Fail Condition**: If any test fails, immediately terminate the session and print:
  * Name/Aim of the failed test case
  * Expected Output
  * Actual Output