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

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding Standard

All Java source and test code in this project MUST strictly follow the **SE-EDU Java Coding Standard (Basic + Intermediate)** as specified in the project skill `seedu-java-coding-standard` (referencing https://se-education.org/guides/conventions/java/intermediate.html).

Key mandates:
* **Naming**: PascalCase for classes/enums, camelCase for methods/variables, SCREAMING_SNAKE_CASE for constants, lowercase for packages, and boolean names prefixed with `is`/`has`/`was`/`can`/`should`.
* **Layout**: 4-space indentation (no tabs), line lengths <= 120 chars (soft limit 110 chars), 8-space continuation indentation, K&R braces, spaces around binary/ternary operators and after keywords.
* **Statements**: One statement per line, variables declared in the smallest scope and initialized immediately, mandatory braces for all control flow statements (`if`/`else`/`for`/`while`), `default` case in switch statements, no wildcard imports.
* **Class Organization**: Strict member ordering (Javadoc -> Class -> static fields -> instance fields -> constructors -> methods), modifiers ordered `<access> static abstract final synchronized <unusual>`, no non-constant `public` fields.
* **Documentation**: Meaningful Javadoc header comments for all public classes, enums, interfaces, and non-trivial methods/constructors.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

