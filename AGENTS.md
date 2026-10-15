# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate (know fundamental concepts but have not done big projects)
* IDE and level of expertise: Using IntelliJ for this project, not very familiar with it

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

## Testing

Test coverage target: JUnit tests should focus on the top ~50% highest-value methods in the codebase, prioritizing complex, core, or critical business logic over trivial getters/setters, thin delegations, or presentation-only code (e.g. plain `println` calls).

JUnit tests must be kept up to date with that target as the code evolves: after any code change (new method, changed behavior, new class), check whether the change affects a high-value method and update or add JUnit tests accordingly as part of that same change, rather than as a separate follow-up step. Do not let the test suite drift out of sync with the ~50% target.

Follow Gradle and JUnit conventions for test file location and naming (e.g. `seedu.duke.Todo` in `src/main/java/seedu/duke/Todo.java` is tested by `seedu.duke.TodoTest` in `src/test/java/seedu/duke/TodoTest.java`). For long test method names, the convention `featureUnderTest_testScenario_expectedBehavior()` may be used, e.g. `sortList_emptyList_exceptionThrown()`.