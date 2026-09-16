# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Have done C++ algorithmatic programming (competitive) and Machine Learning using Python. Limited SWE knowledge and Java knowledge. 
* IDE and level of expertise: IntelliJ - Low. VSCode - High. Vim/Shell - Medium.

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

For all Java code in this project, follow the project-specific `seedu-java-coding-standard`
skill in `skills/seedu-java-coding-standard`. This skill is based on the SE-EDU
Java coding standard (basic + intermediate) and falls back to the Google Java
Style Guide for topics not covered there.

## Testing

Focus JUnit tests on the top ~50% highest-value methods, prioritizing complex, core, or critical business logic over trivial getters/setters or simple display-only methods.
After each code change, update or add JUnit tests as needed so the test suite continues to comply with this coverage target.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
For all future commits in this project, follow the project-specific `seedu-git-standard`
skill in `skills/seedu-git-standard`.
Do not commit or push unless explicitly asked.
