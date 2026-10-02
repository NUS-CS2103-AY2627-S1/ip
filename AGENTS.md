# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intellij IDEA, Beginner

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

Before modifying Java code, refer to the course's [Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

In particular:

* Use multi-line K&R/Egyptian braces for classes, constructors, methods, loops, and conditionals. Do not collapse a body onto one line.
* Indent `case` and `default` labels one level deeper than their `switch` statement, and indent their statements one further level.
* Keep lines within the 120-character hard limit and use 4-space basic indentation.

## Verification after code changes

After every completed update to application or test code, before reporting the work as complete:

1. Review `test/ui-test-plan.md` against the changed behavior. Update the plan when commands, expected output, setup, or relevant UI-test coverage have changed. If no update is needed, leave the file unchanged and state that it was reviewed.
2. Invoke the project-specific `$test-ui` skill and run every applicable recorded test case. This invocation is mandatory even when `test/ui-test-plan.md` did not need an update.
3. Continue with the remaining independent test cases after a failure. Record the complete latest run, including every failure's actual and expected output, in `test/ui-test-results.md` so the problems remain visible in future sessions.

Do not claim that a code update is verified when any test failed or the UI tests could not run. Report failures, an empty test plan, unavailable JDK 25 runtime, compilation errors, and other blockers explicitly.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
