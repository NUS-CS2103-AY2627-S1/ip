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

## Project-local standards

Read the relevant project-local skill before changing Java code, reviewing Java
code, or preparing Git history. The skill files are versioned project
requirements, not optional suggestions.

All Java source and test code in this repository must follow
`.codex/skills/seedu-java-coding-standard/SKILL.md`. In particular, keep every
class in a named lower-case package, use the `bro` package for the current
application, and apply the standard's naming, layout, visibility, and Javadoc
rules to new and modified code.

All future commits and branch names must follow
`.codex/skills/seedu-git-standard/SKILL.md`. Use imperative, capitalized
subjects without final periods, keep subjects within the stated length limits,
write wrapped WHAT/WHY bodies for non-trivial commits, and use meaningful
kebab-case branch names. Do not commit or push unless explicitly asked.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit coverage target:

Aim to cover approximately the highest-value 50% of non-trivial public methods
with JUnit tests, prioritizing core parsing, persistence, task management, and
user-interface behavior. This is a prioritization goal, not a measured line or
branch coverage claim. Update tests when behavior or contracts change; comments
and formatting alone do not require test edits. Run the relevant existing checks
and fix failures caused by the requested change without repeated approval.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
