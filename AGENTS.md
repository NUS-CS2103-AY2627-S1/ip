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
  * Add explanatory Javadoc comments to every declared class, enum, constructor, and method. Include useful
    @param, @return, and @throws tags whenever they apply. Document nontrivial fields when their purpose or
    behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

Follow the project-specific `seedu-java-coding-standard` skill for every Java code or test change. Before
handing over Java changes, audit the changed files for its naming, layout, imports, comments, and line-length
rules, then fix any violations in scope.

## Git

Follow the project-specific `seedu-git-standard` skill for every future
branch name and commit. Before committing, inspect the staged diff and apply
the skill's commit-scope and message-format rules.
Use lightweight tags unless the user requests an annotated tag.
Every commit must have a full message consisting of a convention-compliant
subject, a blank line, and a body wrapped at 72 characters. The body must
explain what changed and why, following the situation, motivation, action,
and rationale structure from the SE-EDU Git conventions. Leave low-level
implementation details to the diff.
Do not commit or push unless explicitly asked.

## UI testing after code updates

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds or alters user-visible behavior.
2. Invoke the project-specific `test-ui` skill to run the UI test plan and inspect the console transcript.

## JUnit testing after code updates

After every code update:

1. Review and update the JUnit tests in `src/test/java` as needed.
2. Maintain tests for approximately the top 50% highest-value methods, prioritizing complex, core, and
   critical business logic. Test all reasonable scenarios for each selected method.
3. Run `./gradlew test` and inspect the result before handing over the change.
