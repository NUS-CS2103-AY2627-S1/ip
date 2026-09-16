# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 1 year of coding experience. Have worked with java and python. Have also built a full stack web app with javascript and taskscript.
* IDE and level of expertise: Usually use vscode, not too familiar with the intellij im using now.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Java coding standard requirement

All Java code in this repository must follow the SE-EDU Java coding standard for intermediate projects:

- https://se-education.org/guides/conventions/java/intermediate.html
- https://se-education.org/guides/conventions/java/index.html

This includes naming, indentation, brace style, method casing, Javadoc rules, and readable layout. Follow these rules for every code change in this project.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All Git work in this repository must follow the SE-EDU Git conventions:

- https://se-education.org/guides/conventions/git.html

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## JUnit coverage target

Aim to maintain JUnit coverage of roughly 50% across the highest-value methods in the codebase. Prioritize complex, core, and critical business logic when deciding what to test, rather than low-value getters or trivial formatting wrappers.

After every code change, review the existing JUnit tests in `src/test/java` and update them as needed to keep coverage aligned with the current implementation. If a code change alters behavior, adds a branch, or modifies a core method, add or revise JUnit tests before considering the change complete. Do not leave the project below the target coverage expectation for the affected area.

## UI testing after code updates

After every code update, review `test/ui-test-plan.md` and update it when the change adds or alters user-visible behavior or test coverage. Keep existing test cases unless they are intentionally obsolete; never change expected output merely to conceal a regression.

After reviewing the plan, invoke the project-specific `test-ui` skill and run its test runner before reporting the code update as complete:

```text
Use $test-ui to run the project's UI test plan.
```

If the UI test runner fails, stop and report the failing case with its actual and expected output. Do not continue with unrelated code changes or claim the update is complete until the failure is resolved or the user gives further direction.
