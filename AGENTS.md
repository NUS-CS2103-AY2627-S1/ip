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

## Java coding standard

For every Java code creation, modification, or review in this repository, agents must load and follow the project skill at `.agents/skills/seedu-java-coding-standard/SKILL.md`. All Java code must comply with the SE-EDU basic and intermediate Java coding standard described by that skill.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing

Maintain JUnit tests for at least the top 50% highest-value methods, prioritizing complex, core, and critical business logic over trivial methods.
After each code change, update or add the relevant JUnit tests as needed to continue meeting this target, and run the test suite to verify the change.

## Git

For every future commit-message proposal or commit creation in this repository, agents must load and follow the project skill at `.agents/skills/seedu-git-standard/SKILL.md`. Agents must also follow its branch-naming rules when proposing or creating branches.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
