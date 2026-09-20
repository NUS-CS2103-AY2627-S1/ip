# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Low level
* IDE and level of expertise: Substantial 

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

## Java coding standard

Follow the project-specific `$seedu-java-coding-standard` skill for all Java code changes.
Keep `src/main/java` as the source root, put every production class in a package under it, use 4-space indentation, avoid wildcard imports, and keep lines within 120 characters.

## Git

Follow the project-specific `$seedu-git-standard` skill for commit messages and branch names.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, use imperative mood, capitalize the first word, do not end the subject with a period, and keep the subject within 72 characters.
For non-trivial commits, include a blank line followed by a body that explains what changed and why.
After every code change, tell the user the corresponding suggested Git commit message.
Do not commit or push unless explicitly asked.

## UI testing

After each code update, update `test/ui-test-plan.md` if the change affects commands, output text, formatting, or console behavior.
After each code update, invoke the project-specific `$test-ui` skill to run the console UI test plan.

## JUnit testing

Focus JUnit tests on the top ~50% highest-value methods, prioritizing complex, core, or critical business logic over simple getters/setters.
After each code update, update the relevant JUnit tests so the project continues to comply with this 50% high-value coverage target.
Use Gradle/JUnit conventions for test paths and names, e.g. `src/test/java/larper/task/TaskListTest.java` for `src/main/java/larper/task/TaskList.java`.

## Comments

Write comments in English, use American spelling, and avoid local slang because the code is for an international audience.
Write descriptive Javadocs for all public classes and public methods, except getters/setters, test classes/methods, and overridden methods where the parent Javadoc applies exactly.
Keep the first Javadoc sentence short and behavior-focused, e.g. `Returns ...`, `Adds ...`, `Creates ...`, or `Shows ...`.
