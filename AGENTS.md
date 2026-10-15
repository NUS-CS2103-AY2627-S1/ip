# Project context

## Java coding standard:

All Java code in this project must follow the project-specific `seedu-java-coding-standard`
skill, based on the SE-EDU Java basic and intermediate conventions:
https://se-education.org/guides/conventions/java/intermediate.html

Use the skill for every Java code change, including package/import organization, naming, formatting,
braces, line length, Javadocs, and the prescribed JUnit test naming convention.

## Git standard:

All future commits and branches must follow the project-specific `seedu-git-standard` skill, based
on the SE-EDU Git conventions: https://se-education.org/guides/conventions/git.html

Before committing, use an imperative, capitalized subject without a period, keep it within 72
characters, and include a wrapped explanatory body for non-trivial changes. Use meaningful
kebab-case branch names.

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basic
* IDE and level of expertise: IntelliJ IDEA, basic 

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

## UI testing after code changes:

After every code update, review test/ui-test-plan.md and add or revise test
cases when the changed behavior requires it. Then invoke the project-specific
test-ui skill to run the plan and record the console session. Report any
failure with the expected and actual output before considering the update
complete.

## JUnit test coverage:

Maintain JUnit tests for the highest-value approximately 50% of methods, prioritizing complex,
core, or business-critical logic. Update the relevant JUnit tests after every code change so this
coverage target remains satisfied.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
