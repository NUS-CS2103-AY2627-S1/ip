# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium 
* IDE and level of expertise: IntelliJ IDEA, Low

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

Use the project-local `seedu-java-coding-standard` skill for every Java code creation, modification, and review in this repository. All Java code must follow the SE-EDU intermediate Java coding standard; use Google Java Style only for topics the SE-EDU guide does not cover.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing after code changes:

After each code update, check whether `test/ui-test-plan.md` needs to be updated to cover the changed console UI behavior. If updates are needed, record each test case with its aim, inputs, and expected output.

After updating code and the test plan, invoke the project-specific `test-ui` skill to run the console UI tests. If a test fails, stop further testing and report the failing command, expected output, actual output, and transcript.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.