# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ IDEA, intermediate

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

## Java coding standard

For every task that creates, modifies, or reviews Java code, use the repository skill
`$seedu-java-coding-standard` at
`.agents/skills/seedu-java-coding-standard/SKILL.md`. All production and test Java
code must follow the SE-EDU basic and intermediate Java coding standard summarized
by that skill. Follow the Google Java Style Guide for topics the SE-EDU standard
does not cover.

## Git

Use lightweight tags unless the user requests an annotated tag.
For every task that proposes, reviews, creates, amends, squashes, or merges a
commit, use the repository skill `$seedu-git-standard` at
`.agents/skills/seedu-git-standard/SKILL.md`. All commit messages, including merge
commit messages, must follow the SE-EDU Git conventions enforced by that skill.
Use the same skill when naming branches.
Do not commit or push unless explicitly asked.
