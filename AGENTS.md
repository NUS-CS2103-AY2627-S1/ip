# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

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

All Java code in this repository **must** follow the SE-EDU Java coding standard (basic and
intermediate levels), captured as the project skill `seedu-java-coding-standard`
(`.claude/skills/seedu-java-coding-standard/SKILL.md`).

* Invoke that skill before writing, editing, or reviewing any `.java` file in this project, and
  whenever asked to check or fix style or coding-standard violations.
* This applies to test code as well as production code, and to code written from scratch as well
  as code being modified.
* Where the standard is silent, fall back to the
  [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
* Before reporting Java work as finished, run the compliance checks at the end of that skill, plus
  `./gradlew test javadoc`.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
