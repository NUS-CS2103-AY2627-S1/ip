# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ Beginner

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

All Java code in this project (new or edited) must follow the SE-EDU intermediate Java coding
standard — see the `seedu-java-coding-standard` skill for the full checklist (naming, layout,
statements, comments/Javadoc). Apply it when writing code, and check against it when reviewing or
editing existing code; fix violations you touch or notice in the area you're working in.

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

All commit messages (proposed or created) and branch names must follow the SE-EDU Git
conventions — see the `seedu-git-standard` skill for the full checklist (subject line, body,
branch naming).

Whenever asked to commit, the commit message must follow standard Git/GitHub conventions:

* **Subject line:** imperative mood (e.g. "Add", not "Added"/"Adds"), capitalized first letter,
  no trailing period, at most 50 characters (72 is a hard limit).
* **Body:** separated from the subject by one blank line, wrapped at 72 characters per line,
  explaining *what* changed and *why* (not *how* — the diff already shows that).
* Do not fabricate content: base the message only on the actual changes being committed.

## Testing after code changes

After any code change that could affect the program's console behavior (new/changed commands, output formatting, task handling, etc.):

1. Update `test/ui-test-plan.md` if the change adds a new command, changes an existing command's behavior, or changes console output — add or update test cases so the plan still reflects actual, correct behavior. Use the `test-ui` skill's `--record` mode to generate exact expected-output text rather than hand-typing it.
2. Invoke the `test-ui` skill to run the full test plan and confirm the change didn't break anything.

If `test-ui` reports a failure, treat it as a bug to investigate and fix (or, if the new behavior is intentional, update the affected test case's expected output) before considering the change done.

## JUnit test coverage target

Maintain JUnit test coverage across the top ~50% highest-value methods in the codebase — prioritize complex, core, or critical business logic (parsing, validation, date/time handling, data persistence, task-list operations) over trivial getters/setters and thin orchestration code (`Ui`, `Command.execute()` implementations, `Chud.main`), which are console-facing and already covered end-to-end by the `test-ui` suite instead.

After any code change to a method in that top-value tier (new method, changed behavior, new edge case):

1. Add or update the corresponding JUnit test(s) so the suite still reflects actual, correct behavior. Follow the existing file layout (`src/test/java/<package>/<Class>Test.java`, mirroring `src/main/java/<package>/<Class>.java`) and the `featureUnderTest_scenario_expectedBehavior()` naming convention used throughout the suite.
2. Run `./gradlew test` to confirm the full suite still passes.

If a test fails, treat it as a bug to investigate and fix (or, if the new behavior is intentional, update the affected test's expectations) before considering the change done. A JUnit test failure caused by a genuine defect in production code (not a stale expectation) should be fixed in the production code, not worked around in the test.
