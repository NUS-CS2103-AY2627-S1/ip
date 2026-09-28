# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Minimal
* IDE and level of expertise: Minimal

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this repository, including tests, MUST follow the project-local
`seedu-java-coding-standard` skill, based on the SE-EDUCATION basic and
intermediate Java coding standard:
https://se-education.org/guides/conventions/java/intermediate.html

Apply it when creating, modifying, or reviewing Java code. For topics not covered
there, use the Google Java Style Guide. Before completing code changes, inspect
touched files for naming, layout, imports, visibility, braces, initialization,
and Javadocs, then run the relevant tests.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
For every future commit, follow the project skill
`.codex/skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git
conventions. This mandates imperative, capitalized, period-free commit
subjects (preferably 50 characters, hard limit 72), 72-character-wrapped
bodies for non-trivial commits, WHAT/WHY explanations, and meaningful
kebab-case branch names.
Do not commit or push unless explicitly asked.
