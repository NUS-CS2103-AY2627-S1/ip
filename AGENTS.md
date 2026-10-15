# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 7/10
* IDE and level of expertise: 5/10 VS code

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

All code must follow the SE-EDU Java coding standard (intermediate level):
<https://se-education.org/guides/conventions/java/intermediate.html>. The rules
that come up most often here:

* Indent with 4 spaces, never tabs. Wrapped lines indent by 8.
* Keep lines under 110 characters; 120 is the hard limit.
* K&R braces, and braces on every `if`/`for`/`while` body however short.
* Class and enum names are nouns in PascalCase; method names are verbs in
  camelCase; constants are `UPPER_SNAKE_CASE`; collections take plural names.
* Booleans read as booleans: `isDone`, `hasData`.
* Import every class explicitly — no wildcard imports, and no unused ones.
* Header comments on every class and every public method.
* **Comments are written in English using American spelling** (`recognize`, not
  `recognise`).

Test methods are named `featureUnderTest_testScenario_expectedBehavior()`.

Commit messages follow the SE-EDU Git standard:
<https://se-education.org/guides/conventions/git.html>.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
