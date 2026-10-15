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
  * Follow the `seedu-java-coding-standard` skill for naming, layout, statements, and Javadoc. Do not invent a different house style.
  * Follow the `seedu-git-standard` skill for commit messages and branch names. Do not invent a different Git style.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java in this project MUST follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). Read `.cursor/skills/seedu-java-coding-standard/SKILL.md` before writing, editing, or reviewing Java, and apply it. For topics that page does not cover, use the Google Java Style Guide as that skill specifies.

## After each code update

After changing application code (or chatbot output), do both of the following before considering the work done:

1. Update `test/ui-test-plan.md` if needed — add, remove, or adjust test cases so each case still records its **aim**, **inputs**, and **expected output** for the current behavior.
2. Invoke the `test-ui` skill (read `.cursor/skills/test-ui/SKILL.md` and follow it). That skill runs the program against the plan, shows the console input/output record, and stops immediately on the first failure with actual vs expected output.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All commits and new branch names MUST follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Read `.cursor/skills/seedu-git-standard/SKILL.md` before proposing or creating a commit, and apply it.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
