# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

- Prior knowledge: Basic Java and OOP concepts.
- Level of programming experience: Comfortable with core Java and OOP (from CS2040S Data Structures & Algorithms and CS2030S Programming Methodology II). New to the software-engineering layer: build tools (Gradle), multi-file/package project structure, JUnit testing, and JavaFX GUIs.
- IDE and level of expertise: IntelliJ IDEA on Windows. Comfortable writing and running Java classes; beginner with the IDE's features and the Git/SE workflow.

# Guidance for interacting with users

- Explain the rationale for significant actions: what you did and why.
- Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:
  - When suggesting a Git command, briefly explain what it does.
  - Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  - Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  - When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

All Java code in this project MUST follow the SE-EDU intermediate Java coding standard
(https://se-education.org/guides/conventions/java/intermediate.html): naming, 4-space
layout, explicit imports (no wildcards), braces on all bodies, and Javadoc on public
classes/methods. Apply it before writing, editing, or reviewing any Java code. This is
not optional.

## Git

All commit messages in this project MUST follow the SE-EDU Git conventions
(https://se-education.org/guides/conventions/git.html): an imperative, capitalized
subject line of at most 50 characters (hard limit 72) with no trailing period, a blank
line, then a body wrapped at 72 characters explaining WHAT and WHY (not HOW). This is
not optional.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
