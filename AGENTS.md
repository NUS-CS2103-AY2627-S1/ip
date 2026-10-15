# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: About two years in NUS.
* IDE and level of expertise: IntellIJ, not too familiar with it.

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

## Java coding standard:

Follow the SE-EDU Java coding standard for all existing and newly added Java code:

https://se-education.org/guides/conventions/java/intermediate.html

In particular:

* Use four spaces for indentation and K&R-style braces.
* Keep lines at or below 120 characters.
* Use consistent import ordering and explicit imports.
* Use lower-case package names and place every class in a package.
* Use camelCase for variables and methods, and PascalCase for classes.
* Use descriptive JavaDoc comments for all public classes and methods.
* Use braces for all loops and conditional statements, including single-statement bodies.
* Write comments in English using American spelling.

## Git

Use lightweight tags unless the user requests an annotated tag.
Follow the SE-EDU Git commit message standard for all commits.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
