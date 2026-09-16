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

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

For every Java implementation, test, refactoring, and review task, load and follow the project-specific
`seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. Apply it to all new
or changed Java code. When the user requests a coding-standard audit, apply it across the entire requested scope.
Do not treat formatting as permission to change application behavior.

## JUnit testing

The current target is the A-JUnit stretch goal: test all non-trivial public methods of every application class.
This exceeds the baseline target of testing the highest-value 50% of methods; prioritize complex parsing,
task operations, and persistence when expanding the suite. These are method-selection goals, not measured
line-coverage percentages.

After every code change, add or update JUnit tests to maintain this target and run the suite with Java 25.
Place tests under `src/test/java` in the same package as the class being tested, using `ClassNameTest` and
`methodUnderTest_scenario_expectedBehavior` names. Test normal behavior, boundaries, and relevant failure cases.
Use temporary directories for file tests and restore any global streams or locale settings changed by tests.
See `test/junit-coverage.md` for the current coverage map and Gradle commands.

## Git

Before planning branches or proposing, creating, amending, tagging, merging, or reviewing commits, load and
follow the project-specific `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`.
All future commits must comply with that skill. Git inspection never authorizes mutation; do not stage, commit,
amend, tag, merge, push, or delete branches unless the user explicitly requests the relevant action.
