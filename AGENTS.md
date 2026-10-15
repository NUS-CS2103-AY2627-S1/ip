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

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Coding standard

All Java code in this repository follows the
[SE-EDU Java coding standard, intermediate level](https://se-education.org/guides/conventions/java/intermediate.html).
Any new or modified code must comply with it. The rules that come up most in this project:

* Keep lines under 110 characters (hard limit 120). Indent wrapped lines by 8 spaces.
* Classes are PascalCase nouns, methods are camelCase verbs, constants are UPPER_SNAKE_CASE,
  and booleans read as questions (`isDone`, `hasTime`).
* An abbreviation used as part of a longer name keeps only its first letter capitalised:
  `EvException`, not `EVException`.
* Every `if`, `for` and `while` body is wrapped in braces, even when it is a single statement.
* Import each class explicitly (no wildcards), `java` imports first, then project imports,
  alphabetically within each group.
* Class variables are never public unless they are constants.
* Test methods are named `featureUnderTest_testScenario_expectedBehavior`.

## Git commit messages

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

* Write the subject in the imperative mood, capitalised, with no full stop, under 50
  characters where possible and never over 72.
* Separate subject and body with a blank line, and wrap the body at 72 characters.
* The body says what the commit is about and why it was done that way. The diff already
  shows how, so do not narrate the changes line by line.
