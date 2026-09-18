# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basic
* IDE and level of expertise: Basic

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Coding standard:

All Java code in this repository (every `.java` file under `src/`, production and test) must follow the project's coding standard, which is based on se-education.org's intermediate Java coding conventions (https://se-education.org/guides/conventions/java/intermediate.html).

The standard is captured as the project-local `seedu-java-coding-standard` skill. Invoke it (`$seedu-java-coding-standard`) and apply its rules whenever you write, review, or refactor Java code. This is mandatory, not advisory: new or edited code must conform, and any file you touch must be left at least as compliant as you found it.

## Code update workflow:

After every code update:

1. Check the changed Java against the `## Coding standard` above (use `$seedu-java-coding-standard`) and fix any violations before moving on.
2. Review `test/ui-test-plan.md` and update it when the change adds, removes, or changes observable command-line UI behavior. Keep each affected test case's aim, inputs, and expected output accurate.
3. Invoke the project-local `test-ui` skill (`$test-ui`) using the updated plan. Do not skip this invocation when the plan does not need changes.
4. Update the JUnit tests so the project still meets the test coverage target below (see `## Test coverage`). Add tests for new logic, revise tests whose expected behavior changed, and remove tests for deleted code.
5. Run the full JUnit suite with `./gradlew test` and make sure it passes.

Treat the code update as incomplete until the UI test session has been run and the JUnit suite passes. If a test fails, stop the session, report the actual and expected output, and resolve or explicitly report the failure before considering the update complete.

## Test coverage:

Target: JUnit tests should cover roughly the top 50% highest-value methods, prioritizing complex, core, or business-critical logic (for example command parsing and file persistence) over trivial getters, setters, and console-print helpers.

This is an ongoing target, not a one-time task: every code change must be followed by a matching update to the JUnit tests (step 3 of the code update workflow) so coverage of the high-value methods does not slip below the target. A change is not complete until its tests exist and `./gradlew test` passes.

Follow Gradle and JUnit conventions for test placement and naming: the test for `altair.storage.Storage` lives at `src/test/java/altair/storage/StorageTest.java`. When a descriptive test-method name would be long, use `featureUnderTest_testScenario_expectedBehavior()`, e.g. `load_unknownTypeMarker_throwsAltairException()`.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All commits and branch names must follow the project's Git standard, which is based on se-education.org's Git conventions (https://se-education.org/guides/conventions/git.html). It is captured as the project-local `seedu-git-standard` skill; invoke it (`$seedu-git-standard`) whenever you write a commit message or name a branch. This is mandatory for all future commits.

Key points: imperative-mood subject, capitalized, no trailing period, <= 50 chars (hard limit 72); body (when needed) separated by a blank line and wrapped at 72 chars, explaining what and why; branch names in kebab-case.

Additional project rules:

- Draft the commit message and get the user's explicit approval before committing. Keep messages very short; add a body only when the subject cannot carry the rationale.
- Do not add a `Co-Authored-By` trailer; the user is the sole author.
- Use lightweight tags unless the user requests an annotated tag.
- Do not commit or push unless explicitly asked.
