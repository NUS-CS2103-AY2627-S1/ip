# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java source or test change in this project, use the `seedu-java-coding-standard` skill at
`/Users/lwz/.codex/skills/seedu-java-coding-standard`. Its SE-EDU basic + intermediate rules are mandatory for
all Java code in this repository, including code reviews and style-only maintenance.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing

Maintain JUnit coverage for approximately the top 50% highest-value methods, prioritizing complex, core, and critical business logic. Update or add the relevant JUnit tests after every code change so the test suite continues to meet this target.

## Git

For every future commit, use the `seedu-git-standard` skill at
`/Users/lwz/.codex/skills/seedu-git-standard`. Its SE-EDU rules for commit messages and branch names are mandatory.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
