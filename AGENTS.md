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

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
Strictly stick to agents/GIT_CONVENTIONS.md when handing git.

## Java Coding Standards

Strictly stick to the styling rules described in agents/JAVA_CODING_STANDARD.md when writing Java code.

Additionally, adhere to these rules:

* Never create getter and setter methods for attributes in classes that are not datatypes.
* Do not create extra constructors with less parameters than the main constructor.
  * Prefer passing in appropriate arguments to object initialization.
* Null values
  * Instead of null return values, throw exceptions when parsers cannot parse values into variables.
  * Instead of null checks, Use booleans to check for specific states.
  * If a variable must be set to null in the rare case that the variable will never be used, alert the human programmer.

## Filesystem MCP

Always use the filesystem MCP for manipulating files.
Do not use PowerShell just for accessing files unless strictly necessary, such as if filesystem fails or is not installed.

Available tools include:
- Read/write files
- Create/list/delete directories
- Move files/directories
- Search files
- Get file metadata
- Dynamic directory access control