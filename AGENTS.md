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
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.
  * Do not introduce unrelated refactoring or functionality unless the user requests it.
  * Preserve existing program behavior when performing coding-standard or documentation changes.
# Project-specific requirements
## Java coding standard
Apply the seedu-java-coding-standard skill whenever creating, modifying, reviewing, formatting, or refactoring Java code in this project.

Follow the SE-EDU basic and intermediate Java coding standard:

https://se-education.org/guides/conventions/java/intermediate.html

Use the Google Java Style Guide only for topics not covered by the SE-EDU standard.
After modifying Java code:

1. Review the diff for accidental behavior changes.
2. Run the relevant Gradle tests.
3. Run the Javadoc task when documentation was added or modified.
4. Report any test failures or unresolved standard violations to the user.
## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

On macOS, run the following command when Java 25 needs to be selected:

sdk use java 25.0.3.fx-zulu

Use the Gradle Wrapper supplied by the project:

.\gradlew <task>

On macOS or Linux:

./gradlew <task>
## Git

* Use lightweight tags unless the user requests an annotated tag.
* When proposing or creating a commit message, include enough detail to explain the rationale for the change.
* Do not commit, tag, merge, or push unless the user explicitly asks.
* Keep each commit focused on one logical change.
* Before staging files, inspect git status and avoid including generated build output or unrelated user changes.