# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: low to medium
* IDE and level of expertise: low to medium

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

## Coding standard

Before creating, modifying, or reviewing Java code, read and follow the project-specific `seedu-java-coding-standard` skill at `.agents/skills/seedu-java-coding-standard/SKILL.md`. All production and test Java code in this project must comply with that standard.

## Testing after code updates

After every update to the project code:

1. Maintain JUnit tests for approximately the top 50% highest-value methods in the codebase, prioritizing complex, core, and critical business logic. After each code change, review the affected methods and add or update JUnit tests as needed to continue meeting that target.
2. Run the complete JUnit test suite using Gradle.
3. Review `test/ui-test-plan.md` and update it when the change adds or modifies commands, output, error handling, or other console-visible behavior.
4. Invoke the project-specific `test-ui` skill and run the complete UI test plan, even when no test-plan changes were needed.
5. Follow the skill's fail-fast behavior: stop at the first failure and report the actual and expected output. Do not claim that the code update is complete until the relevant tests pass, unless the user explicitly asks to leave a known failure unresolved.

## Git

Before proposing or creating commits or branches, read and follow the project-specific `seedu-git-standard` skill at `.agents/skills/seedu-git-standard/SKILL.md`.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
