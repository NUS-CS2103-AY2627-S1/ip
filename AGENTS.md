# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: ~7 months experience with Java, but decently skilled. 10+ yrs programming experience
* IDE and level of expertise: Familiar with IntelliJ IDEA

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

For every Java code addition, modification, review, or refactoring, invoke and follow the project-specific `seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard`. All Java code in this repository must conform to the SE-EDU basic and intermediate Java coding standard.

## Post-code-update UI verification

After every code update, review `test/ui-test-plan.md` and update it when the change adds, removes, or intentionally changes console UI behaviour. Keep existing cases when their behaviour is still valid; do not change expected output merely to conceal a regression.

Then invoke the `test-ui` skill and run its documented UI test command from the repository root. Do this even when no update to the UI test plan is needed, and report any test failure before considering the code update complete.

## Git

Use lightweight tags unless the user requests an annotated tag.
For every future commit message that you propose, review, or create, invoke and follow the project-specific `seedu-git-standard` skill in `.codex/skills/seedu-git-standard`. Do not alter past commit messages merely to apply that standard.
Do not commit or push unless explicitly asked.
