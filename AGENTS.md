# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: intermediate level
* IDE and level of expertise: IntelliJ IDEA, intermediate level

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard:

All Java code in this project must follow the project-specific `seedu-java-coding-standard/SKILL.md`, based on the SE-EDU intermediate Java conventions. Apply it to every code change and update the relevant JUnit tests after each change.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing:

Maintain JUnit tests for the highest-value methods covering approximately 50% of the codebase, prioritizing complex, core, and business-critical logic. Update or add the relevant JUnit tests after every code change so that the test suite continues to comply with this 50% coverage target. Run the Gradle test task after modifying tests or production code.

## Git

All branches and commits must follow `seedu-git-standard/SKILL.md`, based on the SE-EDU Git conventions. Use meaningful kebab-case branch names, imperative commit subjects of at most 72 characters, and descriptive wrapped bodies for non-trivial commits. Do not commit or push without explicit user authorization.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
