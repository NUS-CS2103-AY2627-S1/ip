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

## Java coding standard (mandatory)

All Java code in this repository MUST follow the SE-EDU Java coding standard
(basic + intermediate rules): <https://se-education.org/guides/conventions/java/intermediate.html>

The standard is packaged as the project skill **`seedu-java-coding-standard`**
(`.claude/skills/seedu-java-coding-standard/SKILL.md`).

* Invoke that skill BEFORE writing, editing, generating, or reviewing any `.java` file in this
  repository - including new classes, new methods, refactors, and code review. Do not rely on
  recollection of the rules; read the skill.
* Code that violates the standard is not finished. Before reporting a Java change as done, run
  the checks in the skill's "Verifying compliance" section and fix everything they report.
* If a rule appears to conflict with a request, follow the standard and say so; only depart from
  it when the user explicitly asks.
* For anything the standard does not cover, follow the Google Java Style Guide.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
