# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Normal
* IDE and level of expertise: IntelliJ normal

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

## Git

* Strictly follow the SE-EDU Git Conventions as defined in `.agents/skills/seedu-git-standard/SKILL.md` and https://se-education.org/guides/conventions/git.html for all proposed and created commits in this project.
* Format commit messages with an imperative subject line ($\le 50$ chars, capitalized, no ending period) and a detailed body wrapped at 72 chars explaining WHAT changed and WHY.
* Use lightweight tags unless the user requests an annotated tag.
* Do not commit or push unless explicitly asked.


## Testing

* Focus JUnit tests on the top ~50% highest-value methods (prioritizing complex parsing, validation, core task domain logic, and persistence).
* Update and run JUnit tests after each code change to ensure ongoing compliance with the ~50% test coverage target.

## Coding standard:

* Strictly adhere to the SE-EDU Java Coding Standard (Basic + Intermediate) as defined in `.agents/skills/seedu-java-coding-standard/SKILL.md` and https://se-education.org/guides/conventions/java/intermediate.html for all code in this project.
* Follow all naming conventions (PascalCase for classes, camelCase for methods/variables, SCREAMING_SNAKE_CASE for constants, no single-letter variable names or abbreviations like `desc` or `arg`).
* Enforce layout rules: 4 spaces per indentation level, 8-space continuation indent for wrapped lines, 110-character soft limit / 120-character hard limit, K&R braces, no wildcard imports.
* Provide Javadoc comments for all classes, public/protected members, and non-trivial private methods.
* Strictly adhere to the SE-EDU CSS Coding Standard as defined in `.agents/skills/seedu-css-standard/SKILL.md` and https://se-education.org/guides/conventions/css.html for all stylesheets in this project.



