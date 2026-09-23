# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ IDEA, Beginner

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

All Java code in this project follows the SE-EDU Java coding standard
(intermediate level): https://se-education.org/guides/conventions/java/intermediate.html

Checkstyle enforces it mechanically, using the configuration from AddressBook
Level 3 in `config/checkstyle/`. Run it with:

```
./gradlew checkstyleMain checkstyleTest
```

The build must report no violations before a change is committed. Where
Checkstyle and this document disagree, Checkstyle wins: it is the version that
is actually checked.

Any new or edited code must comply with it. The points this project has had to
watch:

* Import classes explicitly. Never use a wildcard import, and never name a
  class in full at the point of use when it can be imported instead.
* Keep the import order consistent: static imports, then `java.*`, then
  third-party packages, then this project's own packages.
* Comments and names are written in English, using American spelling.
* Abbreviations are not written in uppercase inside a name: `Ui`, not `UI`;
  `exportHtmlSource()`, not `exportHTMLSource()`.
* Boolean names read as a statement that is true or false, e.g. `isDone`,
  `hasKeyword`.
* A name representing a collection is plural, e.g. `tasks`, `matches`.
* Braces are required even around a single statement.
* Lines stay within 120 characters, and a wrapped line is indented 8 spaces.
* Every non-private class and method carries a header comment, including
  overrides where the inherited text does not describe what this particular
  implementation does.

## Git

Commit messages follow the SE-EDU Git convention:
https://se-education.org/guides/conventions/git.html
A subject line in the imperative mood, capitalised, no full stop, ideally
within 50 characters and never past 72; a blank line; then a body wrapped at
72 characters explaining what changed and why, not how.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
