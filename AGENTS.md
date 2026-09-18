# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium
* IDE and level of expertise: Medium

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

All Java code in this repository must follow the SE-EDU Java coding standard
(basic + intermediate levels), captured in the `seedu-java-coding-standard`
skill at `.claude/skills/seedu-java-coding-standard/SKILL.md`.

* Invoke that skill before writing, editing, reviewing, or generating any Java
  source in this project, and follow it without exception.
* This applies to new classes, edits to existing ones, tests, and Javadoc alike.
* When an edit would touch code that already breaks the standard, bring the
  lines you touch into compliance; leave unrelated code alone unless asked.

## Git

All commits and branches in this repository must follow the SE-EDU Git
conventions, captured in the `seedu-git-standard` skill at
`.claude/skills/seedu-git-standard/SKILL.md`.

* Invoke that skill before writing, proposing, amending, or reviewing any
  commit message, and before creating or naming any branch, and follow it
  without exception.
* This governs how a commit is written, not whether to make one: do not commit
  or push unless explicitly asked.
* Subject lines are imperative, capitalised, free of a trailing period, and at
  most 72 characters. Non-trivial commits carry a body, wrapped at 72
  characters, explaining what changed and why rather than how.
* Branch names are kebab-case; a branch for an issue leads with the issue
  number, e.g. `1234-ui-freeze-error`.

Use lightweight tags unless the user requests an annotated tag. The SE-EDU
conventions say nothing about tags, so this repository's preference stands.
