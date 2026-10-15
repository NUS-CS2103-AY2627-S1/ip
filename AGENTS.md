# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate

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

## Java coding standard

For every Java code creation, edit, refactoring, or review in this project, invoke and follow the project-specific `$seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard`. Treat its rules as mandatory for both production and test code.

## Post-change UI testing

After every source-code update:

1. Review `test/ui-test-plan.md` and update it when the change adds, removes, or alters user-visible behavior. Keep expected outputs based on the intended behavior, not output copied from a test run.
2. Invoke the `$test-ui` skill and run the test cases in `test/ui-test-plan.md`. Follow the skill's requirement to stop at the first failure and report the console transcript.

Do not describe a code update as complete until these steps have been performed. If the UI test plan does not need updating, state that it was reviewed and why no change was required.

## Git

Use lightweight tags unless the user requests an annotated tag.
Before proposing or creating any commit message, invoke and follow the project-specific `$seedu-git-standard` skill in `.codex/skills/seedu-git-standard`. Treat its commit-message rules as mandatory for every commit in this project.
Do not commit or push unless explicitly asked.
