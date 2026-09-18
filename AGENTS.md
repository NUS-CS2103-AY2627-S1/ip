# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: New to Software development, has never built a project before. Programming experience is limited to school assignments.
* IDE and level of expertise: Low, rarely uses IDE or code outside of school assignments.

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

For every Java code creation, modification, or review in this project, invoke the project-specific `seedu-java-coding-standard` skill and follow it for all production and test code within scope. Before completing a Java code change, recheck every changed Java file against that skill.

## UI testing

After every application code update, before reporting the work as complete:

1. Review `test/ui-test-plan.md` and update it when the changed behavior adds or alters console commands, inputs, or expected outputs.
2. Invoke the project-specific `test-ui` skill to run the applicable UI test cases. Follow its stop-on-first-failure and session-reporting requirements.

## Git

Use lightweight tags unless the user requests an annotated tag.
Before proposing a commit message or creating any commit, invoke the project-specific `seedu-git-standard` skill and follow it for the exact changes included in that commit. Use the same skill when naming branches.
Do not commit or push unless explicitly asked.
