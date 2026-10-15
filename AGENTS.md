# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Mandatory project coding standard

For every Java source or test change in this repository, read and apply the project-specific
`.agents/skills/seedu-java-coding-standard/SKILL.md`. It is based on the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
and is mandatory for code creation, editing, review, and refactoring. The skill's requirements for
naming, layout, whitespace, imports, braces, variable scope, encapsulation, and Javadocs must be
checked before completing a Java change. Use the Google Java Style Guide only for topics not
covered by the project skill.

# Mandatory project Git standard

For every future authorized commit or branch creation in this repository, read and apply the
project-specific `.agents/skills/seedu-git-standard/SKILL.md`. It is based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Commit subjects
must be imperative, capitalized, free of a trailing period, and no longer than 72 characters;
non-trivial commits must include a focused body explaining what changed and why, wrapped at 72
characters. Branch names must use meaningful kebab-case keywords. Existing project instructions
and explicit user authorization still control whether a commit or branch operation may be made.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Above average level of 2nd year computer science student
* IDE and level of expertise: Basic IntelliJ expertise

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

## Testing

After every code update under `src/main/java`:

1. Review and update the JUnit tests under `src/test/java` for the changed behavior. Maintain approximately 50% coverage of the project's highest-value methods, prioritizing complex, core, and critical business logic over trivial getters, constructors, and thin UI adapters. Add or adjust tests for relevant normal, boundary, and error cases.
2. Update `test/ui-test-plan.md` if the change affects command behavior or output (new commands, changed messages, newly-handled edge cases) — add or edit test cases per the format documented at the top of that file.
3. Invoke the `test-ui` skill to run the test plan against the updated code, and report the result (or the actual-vs-expected failure) back to the user.
