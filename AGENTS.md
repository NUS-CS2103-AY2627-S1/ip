# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Novice
* IDE and level of expertise: Novice

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

For every Java code creation, edit, refactor, or review in this project, you MUST load and follow the project-specific `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. All Java production and test code must comply with the SE-EDU basic and intermediate Java coding standard described by that skill.

## Testing

Maintain JUnit tests for approximately the top 50% highest-value methods. Prioritize methods that contain complex logic, implement core application behavior, handle important data, or present a high risk if they fail; exhaustive tests for trivial methods are not required merely to reach the target.

After every code change, review and update the JUnit tests as needed so the changed behavior continues to comply with this coverage target. Run the relevant tests using Java 25 to verify the changes.

## Git

For every proposed, created, or amended commit in this project, you MUST load and follow the project-specific `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`. Use the same skill whenever naming a branch.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
