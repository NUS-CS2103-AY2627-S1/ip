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
  * When making code changes, preserve existing comments made by the user. Modify or remove existing comments only when necessary, such as when the code changes make the comment inaccurate; in that case, update the comment to match the implementation details.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Follow the [course standards and conventions](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html) for every commit, including the linked [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Use capitalized, imperative subjects without a trailing period; aim for at most 50 characters and never exceed 72.
Separate the subject from the body with a blank line, wrap body text at 72 characters, and explain what changed and why.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Java coding standard

For all Java source and JUnit test changes, follow the project-specific `seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard/SKILL.md`, which is based on the SE-EDU Java coding standard (basic + intermediate).
