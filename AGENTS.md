# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Moderately experienced
* IDE and level of expertise: IntelliJ IDE; moderately experienced

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

## UI testing

After every code update:

1. Update `test/ui-test-plan.md` when the change affects existing console behaviour or needs new UI test coverage. Do not make unrelated test-plan edits.
2. Invoke the project-specific `test-ui` skill after updating the code and test plan. If a test case fails, report the failure and its expected and actual outputs.

## Coding standards

All Java code in this project must follow the project-specific `labubu-java-coding-standard` skill in `.codex/skills/labubu-java-coding-standard/SKILL.md`, which is based on the SE-EDU basic and intermediate Java coding standard. Apply it when creating, editing, reviewing, or formatting Java code, and run Checkstyle when possible.

When reviewing or editing code, identify violations of the project's Java coding standards and report the affected file, location, and rule. Do not automatically correct those violations unless the user explicitly requests the correction.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
