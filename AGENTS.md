# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Comfortable writing Java and using Git day-to-day. New to software engineering practice specifically. Design patterns, testing discipline, coding standards, and documentation conventions are the parts I'm learning. Explain SE concepts and the reasoning behind them; no need to explain Java syntax.
* IDE and level of expertise: VS Code, working mostly through the integrated
  terminal rather than IDE refactoring tools.

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

## Coding standards

Java code must follow the SE-EDU **basic and intermediate** rules:
https://se-education.org/guides/conventions/java/intermediate.html
Captured in full in the `seedu-java-coding-standard` skill. Load it when writing,
editing or reviewing Java in this repository.

Commit messages and branch names must follow the SE-EDU git conventions:
https://se-education.org/guides/conventions/git.html
Captured in full in the `seedu-git-standard` skill. Load it when drafting a commit
message or naming a branch.

## Testing

Prioritize JUnit coverage of high-value logic — parsing, state mutation, persistence,
command dispatch — over trivial getters, println wrappers, and one-line delegations.
When a change adds or alters that kind of logic, add or update its tests in the same
commit rather than leaving them stale.
