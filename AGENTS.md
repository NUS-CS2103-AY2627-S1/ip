# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: University level with years of python experience and a semester of java
* IDE and level of expertise: Intelij and not that experienced with it

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

Before creating, editing, reviewing, or refactoring Java code, invoke and follow the project-local
`seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. Compliance
with that skill is mandatory for all Java production and test code in this repository.

## UI testing after code changes

After every code update:

1. Update `test/ui-test-plan.md` when the change adds, changes, or fixes user-visible command-line behavior. Each affected test case must state its aim, inputs, and exact expected output.
2. Invoke the `test-ui` skill and run its test plan before reporting the implementation as complete.
3. If a UI test fails, stop and report the recorded console input, actual output, and expected output. Do not continue to later test cases.

## Error handling after feature changes

Whenever adding or changing a command or other user-facing behavior:

1. Identify its invalid inputs and failure modes before considering the implementation complete.
2. Update `docs/errors.md` with each new error condition, its exception type, and its user-facing response.
3. Represent expected user-input failures with a suitable `PandaException` subclass. Reuse an existing subclass when its meaning fits; otherwise create a focused new exception class.
4. Catch expected exceptions at the UI boundary. Do not catch broad exceptions such as `Exception`, because unexpected programming errors should remain visible during development.
5. Add UI test cases for the new error paths to `test/ui-test-plan.md`, then follow the UI testing workflow above.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
