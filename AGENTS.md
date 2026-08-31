# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: VS Code, Intermediate

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

## Automated testing

Two test suites guard this project, and both must pass before a change is considered done.

### JUnit tests

JUnit 5 tests live under `src/test/java`, mirroring the package of the class under test (e.g. `bibi.task.Task` is tested by `src/test/java/bibi/task/TaskTest.java`). Run them with:

```
./gradlew test
```

Coverage target: the top ~50% highest-value methods, prioritising complex, core, or critical logic. Currently that means `Parser`, `Storage`, `TaskList`, `TaskDateTime`, and the display, save, and date-matching behaviour of the task types. Simple getters, one-line delegations, and console output are deliberately left out.

**JUnit tests must be updated after each code change to stay within that target.** When a change adds, removes, or alters behaviour in a covered method, update or add tests in the same commit. When a change introduces a new method that falls in the top ~50% by value, add tests for it.

Name test methods `featureUnderTest_testScenario_expectedBehavior()`, e.g. `parse_emptyInput_exceptionThrown()`.

### UI regression testing

After each code update that changes Bibi's console interaction, update `test/ui-test-plan.md` when needed and invoke the project `test-ui` skill.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
