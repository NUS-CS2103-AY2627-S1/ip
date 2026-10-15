# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

## Mandatory Java coding standard

For every Java source and test change in this repository, follow the project-specific skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`. It is based on the SE-EDU basic and
intermediate Java coding standard:
https://se-education.org/guides/conventions/java/intermediate.html

Read and apply that skill when creating, editing, reviewing, or refactoring Java code. This
includes naming, layout, imports, visibility, variable scope, braces, line length, comments,
and Javadoc. Preserve behavior and public APIs when making style-only fixes.

## Mandatory Git standard

For every future commit or commit-message proposal in this repository, follow the
project-specific skill at `.codex/skills/seedu-git-standard/SKILL.md`. It is based on the
SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

Read and apply that skill before preparing, reviewing, or proposing a commit. Commit subjects
must be imperative, capitalized, concise, and free of a trailing period; non-trivial commits
must include a focused body explaining what changed and why, wrapped at 72 characters. Use
meaningful kebab-case branch names. Do not commit or push unless explicitly asked.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: moderate
* IDE and level of expertise: moderate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Testing:

Maintain JUnit tests for approximately 50% of the codebase's highest-value methods,
prioritizing complex, core, and critical business logic. After every code change,
update or add the relevant JUnit tests so that the tests remain aligned with the
implementation and continue to meet this coverage target.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
 project requirements as following website:https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html
