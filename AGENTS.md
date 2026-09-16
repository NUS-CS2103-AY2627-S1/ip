# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Some level of programming
* IDE and level of expertise: Some level of experience 

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

All Java code in this project must follow the SE-EDU intermediate coding standard stored in `.kilo/skills/seedu-java-coding-standard/SKILL.md`. Before generating or editing Java code, load this skill and verify that names, layout, statements, and comments follow its rules. In particular:
- Every public class and public method must have a Javadoc header comment.
- Variables and methods must use camelCase; classes/enums must use PascalCase; constants must use SCREAMING_SNAKE_CASE.
- Imports must be ordered explicitly with no wildcards.
- Collections must use plural names.
- Boolean names must start with `is`, `has`, `was`, `can`, or `should`.
- Remove any AI-generated or editor comments before finalizing code.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
All commit messages must follow the SE-EDU Git conventions. The `seedu-git-standard` skill contains these rules and must be loaded before making any commits.
Do not commit or push unless explicitly asked.
