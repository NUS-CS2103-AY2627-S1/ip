# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ, intermediate

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

Follow these commit-message conventions:

* Write the subject in the imperative mood.
* Capitalise the first word and omit a trailing period.
* Keep the subject within 50 characters where practical and never exceed 72 characters.
* For nontrivial changes, add a body after a blank line that explains what changed and why.
* Wrap commit-message body lines at approximately 72 characters.

## Coding standard

Follow the project Java and Markdown coding standards:

* Use lower-case package names, PascalCase type names, and camelCase method and variable names.
* Name boolean values so they read as propositions, and use plural names for collections.
* Indent Java code with four spaces and use K&R braces.
* Keep Java lines within 120 characters, preferably below 110 characters.
* Use explicit imports and group them consistently as static, Java, third-party, and project imports.
* Put every Java class in a suitable package and keep related classes together.
* Use GitHub-flavoured Markdown with blank lines around headings, lists, and code blocks.
* Do not hard-wrap ordinary Markdown prose unnecessarily.

## UI test workflow

After every code update, review `test/ui-test-plan.md` and update it when the
observable command-line behavior or expected output changes. Then invoke the
project-specific `test-ui` skill to run the plan before considering the update
complete. The skill must stop at the first failed case and report the actual and
expected console output.

## JUnit workflow

Maintain JUnit 5 tests for at least the highest-value non-trivial methods (the
top half is a useful minimum) across the core classes. Update the tests whenever
a code change alters the corresponding behaviour.
