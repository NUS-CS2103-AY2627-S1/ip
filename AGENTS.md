# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Y2 CS Student
* IDE and level of expertise: IntelliJ Idea Ultimate Subscription

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

## Coding standard

All Java code in this project must follow the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
at the intermediate level. In particular:

* 4 spaces per indent level, no tabs, and lines no longer than 120 characters.
* Braces around every `if`, `for` and `while` body, opening brace on the same line.
* `PascalCase` for classes, `camelCase` for methods and variables,
  `UPPER_SNAKE_CASE` for constants; boolean names read as a question
  (`isDone`, `hasNextCommand`).
* No wildcard imports. Imports are grouped: static imports, then `java`/`javax`,
  then this project's own packages, with a blank line between groups.
* A Javadoc header comment on every non-private class and method, written in
  the third person ("Returns ...", not "Return ...").
* Implementation comments sit on their own line above the code they explain,
  and say why rather than what.

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

Commit messages must follow the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

* Subject line in the imperative mood, capitalised, no full stop, at most 50
  characters (hard limit 72). An optional `scope:` prefix is allowed, e.g.
  `build.gradle: Point Gradle at this project`.
* A blank line, then a body wrapped at 72 characters for anything non-trivial.
* Structure the body as: the current situation in the present tense, why it
  needs to change, then `Let's ...` describing the change in the imperative
  mood, and why it was done that way.
* Explain what and why, not how; the diff already shows how. Bullet lists are
  welcome where they read better than prose.
