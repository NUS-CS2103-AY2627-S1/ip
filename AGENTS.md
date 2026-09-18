# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner with mediocre knowledge in Python and Java
* IDE and level of expertise: IntelliJ, beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard:

All Java code in this project must follow the project-specific
`seedu-java-coding-standard` skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`. Read and apply that skill
before creating, editing, reviewing, or refactoring Java code. It is based on
the SE-EDU basic and intermediate Java coding conventions and includes the
project's required naming, layout, import, encapsulation, and Javadoc rules.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git standard:

All future commits and branches in this project must follow the project-specific
`seedu-git-standard` skill at
`.codex/skills/seedu-git-standard/SKILL.md`. Read and apply it before creating,
amending, reviewing, or proposing commits, and before creating or renaming
branches. In particular, commit subjects must be concise, capitalized,
imperative, and free of a trailing period; non-trivial commits must explain
what changed and why.

## UI testing after code changes:

After every code update, review `test/ui-test-plan.md` and add or update test cases when the change affects command-line behavior. Then invoke the `test-ui` skill to run the plan and report any failure; use Java 25 for Java-based test commands.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
