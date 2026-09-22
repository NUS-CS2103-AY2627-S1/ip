# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Basic

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Code update verification

After every code update, inspect `test/ui-test-plan.md` and update it when the changed behavior, inputs, or expected console output is no longer covered accurately. Then invoke the project-specific `$test-ui` skill using the resulting test plan.

The `$test-ui` run is required even when the test plan does not need changes. It must compile or prepare the project with Java 25, execute the listed cases in order, compare actual output with expected output, and stop at the first failure. Include the console input/output session record in the response; for a failure, report the actual and expected output and do not continue with later cases.

# Project-specific requirements

## Java coding standard

All Java code in this project must follow the project-specific `$seedu-java-coding-standard` skill at `skills/seedu-java-coding-standard/SKILL.md`, based on the SE-EDU intermediate Java coding standard. Apply it whenever creating, modifying, or reviewing Java code. In particular, enforce lowercase packages, PascalCase type names, camelCase methods and variables, explicit imports, four-space indentation, K&R braces, a 120-character line limit, private fields, and descriptive Javadocs for public classes and methods.

## Java version: Java 25

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All future commits and branch names in this project must follow the project-specific `$seedu-git-standard` skill at `skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git conventions. Commit subjects must be imperative, capitalized, and free of a trailing period, with a 72-character hard limit. Non-trivial commits must include a body wrapped at 72 characters explaining what changed and why. Use meaningful kebab-case branch names. Do not commit or push unless explicitly asked.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
