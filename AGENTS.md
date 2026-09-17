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

## Java coding standard (MANDATORY)

All Java code in this repository **must** follow the SE-EDU Java coding standard
at **Intermediate** level: <https://se-education.org/guides/conventions/java/intermediate.html>

The full standard is captured as a project skill at
`.claude/skills/seedu-java-coding-standard/SKILL.md`.

* **Read that skill before writing or editing any `.java` file** in this project,
  including tests, and follow it without exception.
* This applies to new code, edits to existing code, and generated snippets shown
  in chat. Never produce Java for this project that violates the standard.
* When you touch a file that already violates the standard, bring the parts you
  touch into compliance and point out any remaining violations you noticed.
* Non-negotiable highlights: `camelCase` methods and variables (no `snake_case`),
  boolean names prefixed with `is`/`has`/`was`/`can`/`should`, 4-space indent,
  K&R braces with a space before `{`, lines <= 120 chars, braces on every
  conditional and loop body, explicit imports with none unused, and Javadoc
  header comments on all public classes and public methods.
* After any rename or refactor for style, rebuild and rerun the tests
  (`./gradlew build`) before reporting the work as done.

## Git (MANDATORY)

All commits and branches in this repository **must** follow the SE-EDU Git
conventions: <https://se-education.org/guides/conventions/git.html>

The full standard is captured as a project skill at
`.claude/skills/seedu-git-standard/SKILL.md`.

* **Read that skill before writing or proposing any commit message**, before
  running `git commit` or `git commit --amend`, and before creating a branch.
* Every future commit message must comply: imperative-mood subject line,
  capitalised, no trailing period, <= 50 chars (hard limit 72); a body for any
  non-trivial commit, separated by a blank line, wrapped at 72 chars, explaining
  WHAT and WHY rather than HOW.
* Branch names use kebab-case; branches tied to an issue use
  `issueNumber-some-keywords-from-issue-title`.
* Use lightweight tags unless the user requests an annotated tag.
* When proposing or creating a commit message, include enough detail to explain
  the rationale for the change.
* Do not commit or push unless explicitly asked.
