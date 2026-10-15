# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: medium
* IDE and level of expertise: low

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Mandatory SE-EDU conventions

All agents working in this repository must treat the latest published versions of the following guides as hard requirements, not optional recommendations:

* [Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
* [Git conventions](https://se-education.org/guides/conventions/git.html)

Before changing Java code or performing a Git operation, review the applicable guide and follow every relevant rule. In particular:

* Apply all basic and intermediate Java rules to every Java source or test file that is added or modified. Use the Google Java Style Guide for topics the SE-EDU Java guide does not cover.
* Run `./gradlew check` after Java changes and fix every reported violation. Passing Checkstyle is necessary but does not replace a manual review for guide rules that Checkstyle cannot enforce.
* Apply the Git guide to every branch name and every commit message. Commit subjects must use imperative mood, start with a capital letter, have no trailing period, aim for 50 characters, and never exceed 72 characters.
* Give every non-trivial commit a body separated from the subject by a blank line. Wrap body text at 72 characters and explain what changed and why, rather than narrating implementation details.
* Before finishing, inspect all modified Java files and any proposed or created Git metadata for compliance. Do not knowingly leave a convention violation in work produced by an agent.

If an explicit course increment or user request requires an exact branch, tag, or other identifier that conflicts with the general Git naming convention, use the required identifier exactly. If compliance is uncertain, pause and ask the user instead of guessing.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
