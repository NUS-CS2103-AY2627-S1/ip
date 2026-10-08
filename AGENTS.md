# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Not much, Got a B in CS2030 and mostly learning python to do Neetcode 150 questions
* IDE and level of expertise: Also quite low, mainly used it in previous mods as well as Orbital project, ManaBhasha

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

For all Java and JUnit code in this project, always follow the project skill `seedu-java-coding-standard`.
That skill is the default coding-standard authority for this repository and is mandatory for all code changes under `src/main/java` and `src/test/java`.

## Testing workflow

After each code update:

* Update `test/ui-test-plan.md` if the change affects console behavior or otherwise changes the relevant UI test coverage.
* Update or add JUnit tests as needed to keep coverage focused on roughly the top 50% highest-value methods, prioritizing complex, core, or critical business logic.
* Treat JUnit tests as part of the required follow-up for each code change whenever that change affects behavior, logic, or important control flow covered by the 50% target.
* Prefer multiple realistic test cases over placeholders, including edge cases and incorrect inputs. Interleave positive and negative cases when practical so errors can be checked for unintended state changes.
* Invoke the `test-ui` skill to run the UI test plan and review the resulting transcript or failure report.

## Git

For all future commits and branch-name suggestions in this project, always follow the project skill `seedu-git-standard`.
That skill is the default Git-convention authority for this repository and is mandatory whenever proposing or creating commit messages, or suggesting branch names.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
