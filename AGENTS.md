# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 2nd year computer science student
* IDE and level of expertise: intelliJ but previously more comfortable with VScode

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

## Coding standard (mandatory)

All Java code in this project MUST follow the SE-EDU Java coding standard
(intermediate level), captured in the `seedu-java-coding-standard` skill.

Invoke that skill before writing or editing any `.java` file, and before adding
or changing any Javadoc comment. Apply it as you write rather than as a cleanup
pass afterwards — code is expected to conform on the first attempt. This
applies to code you generate, code you refactor, and code you review.

Key points, with the full rules in the skill: 4-space indent, lines under 110
characters, K&R braces, explicit imports, braces on every loop and conditional
body, and Javadoc on every public class and method written in the third person
("Returns …", not "Return …").

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

All commit messages and branch names MUST follow the SE-EDU Git conventions,
captured in the `seedu-git-standard` skill. Invoke that skill before proposing
or creating any commit message, and before naming a branch.

In short: imperative, capitalised subject line of at most 72 characters with no
trailing full stop; a blank line before the body; the body wrapped at 72
characters and explaining what and why rather than how; branch names in kebab
case.
