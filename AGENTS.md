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

## Java coding standard

For every Java code change or review in this repository, load and follow the project-specific `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. All production and test code must comply with that skill.

## Testing

Maintain at least 50% code coverage with JUnit tests. After each code change, update or add the relevant JUnit tests as needed to keep the test suite aligned with the changed behavior and compliant with the 50% coverage target.

## Git

For every proposed, reviewed, or created commit and every new branch name, load and follow the project-specific `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
