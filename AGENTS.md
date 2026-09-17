# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 6.7 / 10
* IDE and level of expertise: IntelliJ IDEA, 6.7 / 10

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

## Java Coding Standard (MANDATORY)

ALL Java code MUST follow the [[seedu-java-coding-standard]] skill. This is mandatory for all source files in this project.

Key requirements:
- Follow SE-Education.org Java coding conventions exactly
- Use 4-space indentation (no tabs)
- PascalCase for classes, camelCase for methods/variables
- SCREAMING_SNAKE_CASE for constants
- Javadoc for all public classes and methods
- No wildcard imports
- K&R bracket style
- Test methods follow: `featureUnderTest_scenario_expectedBehavior()` format

When making any code changes, verify compliance with this skill's guidelines.

 ## Git Standard (MANDATORY)
 
 ALL Git commits MUST follow the [[seedu-git-standard]] skill. This is mandatory for all commits in this project.
 
 Key requirements:
 - Subject line: max 50 chars (hard limit 72), imperative mood, capitalize first letter, no trailing period
 - Use scope: prefix when applicable (Person class:, Main.java:, bug fix:, chore:)
 - Body: explain WHAT and WHY, not HOW; wrap at 72 chars; use bullet points when helpful
 - Branch names: kebab-case format (e.g., refactor-ui-tests, issueNumber-keywords)
 
When making any commits, verify compliance with this skill's guidelines.
