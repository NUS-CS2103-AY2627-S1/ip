# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: one year of programming experience
* IDE and level of expertise: new to IDE's 

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Mandatory project standards

* Before implementing any code, configuration, or project-structure change,
  read and follow `.agents/skills/plan-before-implementing/SKILL.md`. Present
  its task-specific plan before editing and obtain confirmation when required.
* For every Java code change or review, read and follow
  `.agents/skills/seedu-java-coding-standard/SKILL.md`. Apply it to all Java
  code in scope and verify the result before handing work back to the user.
* For every code change or review, read and follow
  `.agents/skills/seedu-code-quality-standard/SKILL.md`. Apply its readability,
  naming, simplicity, safety, duplication, and commenting guidelines to all
  code in scope and verify the result before handing work back to the user.
* After every code change, read and follow
  `.agents/skills/run-gradle-check/SKILL.md`. Run the complete Gradle check and
  report any verification failure before handing work back to the user.
* Before proposing or creating a Git commit or branch, read and follow
  `.agents/skills/seedu-git-standard/SKILL.md`. This requirement does not grant
  permission to commit, push, or create a branch.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
