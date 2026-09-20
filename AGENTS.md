# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.
Do not edit any file in this project unless explicitly told to by the user. 

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Completed basic programming modules in Java and JavaScript. Courses include: CS2030S Programming Methodology II, CS1101S Programming Methodology I, CS2040S: Data Structures and Algorithms
* IDE and level of expertise: No familiar with IDE's, big SE projects, iterative approach to programming.

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

## Testing

Maintain a JUnit test coverage target of at least 50%, prioritizing complex, core, and critical business logic. After every production code change, review and update the relevant JUnit tests as needed to continue meeting this target, then run the test suite to verify that it passes.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
