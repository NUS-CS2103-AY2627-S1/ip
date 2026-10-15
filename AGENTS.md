# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: About one year of programming experience, familiar with Python, JavaScript, Java, and basic C.
* IDE and level of expertise: Most familiar with VS Code and have used it for projects, familiar with basic IntelliJ project navigation.

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

For every task that creates, modifies, or reviews Java code, read and follow the
project-specific skill in
`.agents/skills/seedu-java-coding-standard/SKILL.md`. Apply it to both production and
test code.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Console UI tests

After each code update that can affect application behavior:

* Update `test/ui-test-plan.md` when the expected behavior or output changes.
* Invoke the project-specific `test-ui` skill in `.agents/skills/test-ui/SKILL.md`.
* Stop at the first failed test and report the commands, expected output, and actual output before making further changes.
