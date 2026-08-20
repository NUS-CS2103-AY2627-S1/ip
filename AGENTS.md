# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Introductory Java course level (currently taking CS2103T).
* IDE and level of expertise: IntelliJ IDEA (beginner).

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

After each code update:

- Update `test/ui-test-plan.md` (and the matching files in `test/cases/`) if the change affects user-visible behavior.
- Invoke the `test-ui` skill (`.codex/skills/test-ui/test-ui.ps1`) and make sure all test cases pass.

## Java coding standard

All code must follow the SE-EDU intermediate-level Java coding standard:
https://se-education.org/guides/conventions/java/intermediate.html

The full rules are captured in the `seedu-java-coding-standard` skill
(`.codex/skills/seedu-java-coding-standard/SKILL.md`).

## Git standard

All commit messages and branch names must follow the SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

The full rules are captured in the `seedu-git-standard` skill
(`.codex/skills/seedu-git-standard/SKILL.md`).

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
