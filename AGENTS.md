# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Has completed core NUS CS coursework covering discrete structures, introductory programming, object-oriented programming, data structures and algorithms, and computer organization (`CS1231S`, `CS1010`, `CS2030S`, `CS2040S`, `CS2100`). Project experience is limited to one external project, Orbital. Overall, assume foundational CS knowledge but limited experience with larger software engineering projects.
* IDE and level of expertise: Beginner IDE user; familiar with basic editing/running/debugging workflows, but may need guidance for project configuration, build tools, refactoring tools, and advanced IDE features.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## iP course requirements:

Before helping with implementation, project management, documentation, or grading-related work, read `.codex/ip-requirements.md` and follow it as the living course checklist. Update that file when the official iP weekly or grading pages change.

## Documentation:

Do not update the project-root `README.md` for chatbot feature or user-guide changes. That file is the assignment/setup README. Put product usage documentation in `docs/README.md` instead.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard:

Before creating, editing, reviewing, or explaining Java code in this project, load and follow the project-specific `.codex/skills/seedu-java-coding-standard/SKILL.md` skill. All Java code should comply with the SE-EDU basic plus intermediate Java coding standard, using the Google Java style guide only for topics the SE-EDU standard does not cover.

## UI testing:

After each code update, check whether `test/ui-test-plan.md` needs to be updated to cover the changed console behavior. Then invoke the project-specific `test-ui` skill and include the resulting test-session record in the response, especially any actual-vs-expected output if a test fails.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
