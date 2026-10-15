# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Low
* IDE and level of expertise: IntelliJ 2026.2.1 and low

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

## Testing

Maintain JUnit tests for approximately the top 50% of highest-value methods, prioritizing complex, core, and critical business logic. Update the relevant JUnit tests after every code change so this coverage target continues to be met.

## Coding standards

All production and test Java code in this project must follow the project-local `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. Apply it to every new or modified Java file.

Every future Git commit must follow the project-local `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`. This does not override the requirement to obtain explicit authorization before committing or pushing.
## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
