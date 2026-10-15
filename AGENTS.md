# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: moderate programming experience, low software engineering experience
* IDE and level of expertise: IntelliJ IDEA, low expertise

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
* When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

## Java coding standard

All Java production and test code in this repository must follow the
project-specific `$seedu-java-coding-standard` skill, based on the
[SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the skill when reviewing or changing Java code, including its naming,
layout, import, encapsulation, and Javadoc requirements. Do not leave Java
style violations in code changed during a task.

Use the project-specific `$seedu-code-quality` skill after every production or
test-code modification to review readability, maintainability, and design
quality against the project's SE-EDU code-quality guidelines. The Java
standard skill must also run the project's Checkstyle tasks after Java code
changes.

# Project-specific requirements

## Code update verification

After every code update:

1. Review and update the relevant JUnit tests for the changed code. Name each
   JUnit test method using the convention
   `unitBeingTested_descriptionOfTestInputs_expectedOutcome`, with three
   descriptive underscore-separated parts. The test suite should target
   approximately the highest-value 50% of methods, giving priority to complex,
   core, and business-critical logic. Do not leave JUnit tests stale after a
   code change.
2. Run the JUnit suite with Java 25 using `./gradlew test` and report failures.
3. Review `test/ui-test-plan.md` against the changed behavior.
4. Update the test plan when the change adds, removes, or changes a user-visible
   command, input rule, or console output. If no update is needed, state that
   explicitly.
5. Invoke the project-specific `$test-ui` skill to run the documented UI tests.
6. Report the console input and output record and stop at the first failed UI
   test case.
7. Invoke `$seedu-java-coding-standard` to review the changed Java code,
   Javadocs, and Checkstyle results.
8. Invoke `$seedu-code-quality` to identify the highest-priority remaining
   readability or maintainability issue. Do not apply additional refactorings
   from that review without the user's approval.

For changes to JavaFX GUI code, also review `test/gui-test-plan.md` and invoke
the project-specific `$test-gui` skill after the JUnit suite. GUI tests should
exercise observable UI behavior with TestFX and should not duplicate the full
CLI command matrix.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Before every future commit in this repository, invoke the project-specific
`$seedu-git-standard` skill and follow its checks for commit subjects, bodies,
and branch names. This requirement is based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
