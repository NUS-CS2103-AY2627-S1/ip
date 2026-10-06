# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basic
* IDE and level of expertise: Medium

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
* When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

## Java coding standard

All Java source and test code in this project must follow the project-specific
skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`, which is based on
the SE-EDU basic and intermediate Java coding standard. Apply it to existing
code touched by a change as well as to new code, including package naming,
layout, naming, imports, braces, and required Javadoc comments.

## Git standard

All future commits in this project must follow the project-specific skill at
`.codex/skills/seedu-git-standard/SKILL.md`, which is based on the SE-EDU Git
conventions. Commit subjects must be imperative, capitalized, concise, and
free of a trailing period; non-trivial commits must include a 72-character
wrapped body explaining what changed and why. Use meaningful kebab-case branch
names, preserving any required repository or host prefix.

# Project-specific requirements

## Testing:

* Maintain JUnit test coverage for approximately the highest-value 50% of methods, prioritizing complex, core, and business-critical logic.
* After every code change, add or update the relevant JUnit tests so that the test suite remains compliant with this coverage target.
* Run the relevant Gradle tests after making code or test changes.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
