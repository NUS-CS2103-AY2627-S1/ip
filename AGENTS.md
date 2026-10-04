# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory comments to classes, methods, and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java Coding Standard:

All Java code in this project MUST strictly follow the **SE-EDU Java Coding Standard (Basic + Intermediate)** defined in the `seedu-java-coding-standard` skill (`.agents/skills/seedu-java-coding-standard/SKILL.md`) and referenced from https://se-education.org/guides/conventions/java/intermediate.html:
* Package names in all lowercase; classes in PascalCase; methods and variables in camelCase; constants in SCREAMING_SNAKE_CASE.
* Test methods must use the three-part format: `featureUnderTest_testScenario_expectedBehavior()`.
* 4-space indentation; 8-space continuation indentation; line length <= 120 chars (soft limit <= 110 chars).
* K&R / Egyptian bracket style for all blocks; curly brackets mandatory for all conditionals and loops.
* Explicit imports only (no wildcard imports like `import java.util.*;`).
* Array specifiers attached to type (`int[] a`, not `int a[]`).
* Booleans named with prefixes (`is`, `has`, `was`, `can`, `should`).
* Javadoc headers for all classes, public/protected methods, and constructors, starting with 3rd-person singular verbs and ending parameter descriptions with periods (not required for private methods).

## Git Conventions:

All Git operations, commit message proposals, branch creations, and tags MUST strictly follow the **SE-EDU Git Conventions** defined in the `seedu-git-standard` skill (`.agents/skills/seedu-git-standard/SKILL.md`) and referenced from https://se-education.org/guides/conventions/git.html:
* **Subject line**: Imperative mood (e.g. `Add Parser class`), capitalized first letter, no trailing period, $\le 50$ chars target (hard limit: 72 chars).
* **Commit body**: Separated by a blank line, wrapped at 72 chars, explaining WHAT and WHY (not HOW) using the structure `{current situation} {why it needs to change} {what is being done about it} {why it is done that way}`.
* **Branch names**: Meaningful keywords in kebab-case (`kebab-case` or `<issueNumber>-<keywords>`).
* **Tags**: Use lightweight tags unless annotated tags are explicitly requested.
* **Commit Execution**: Do not commit or push unless explicitly asked by the user.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.
