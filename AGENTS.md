# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Good
* IDE and level of expertise: Zed, good level of expertise

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

All Java code in this project (new or edited) must follow the `seedu-java-coding-standard` skill
(`.claude/skills/seedu-java-coding-standard/SKILL.md`), based on the SE-EDU intermediate Java conventions.
Invoke that skill before writing or reviewing any Java code, and check the change against it — not just
the rule most obviously related to the change at hand.

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

All commit messages and branch names in this project must follow the `seedu-git-standard` skill
(`.claude/skills/seedu-git-standard/SKILL.md`), based on the SE-EDU Git conventions. Invoke that skill
before proposing or creating any commit message, and before naming any new branch.

## Testing workflow

After making any code change (e.g. to `Bott.java` or any `Task` subclass):

1. Update `test/ui-test-plan.md` if the change affects Bott's commands or console output — add or revise
   test cases so the plan's expected output reflects the new intended behavior.
2. Invoke the `test-ui` skill to compile the program and check its actual output against the test plan.
3. Update the project's JUnit tests to keep them compliant with the coverage target below — add tests for
   any newly-introduced high-value method, and fix/extend existing tests whose target method's behavior or
   signature changed. Do this whether or not the change already broke an existing test; the target is about
   which methods are covered, not just about keeping existing tests green.

Do this even if the user didn't explicitly ask for testing.

## JUnit test coverage target

Aim to have JUnit tests covering the top ~50% highest-value methods in the codebase, prioritized by
complexity and how core/critical the method's logic is — not by raw line or method count.

* **Prioritize:** parsing/validation logic, formatting logic (e.g. date display, save-file
  serialization), index/boundary-sensitive logic (e.g. 1-based/0-based conversions), and file
  persistence (load/save round-trips, corrupted-input handling, I/O failure handling).
* **Deprioritize:** trivial getters/setters, one-line pass-throughs, enum accessors, and methods whose
  main job is orchestration or interactive console I/O (these are better covered by `test/ui-test-plan.md`
  than by JUnit, since mocking them adds more test complexity than the logic being tested justifies).
* Private helper methods generally don't need a test of their own if their behavior is already exercised
  through a public method's tests (e.g. `Parser.parseDate` via `parseDeadline`/`parseEvent`,
  `Storage.parseSavedTask` via `load`).

Test files live under `src/test/java`, mirroring the package of the class under test (e.g.
`bott.parser.Parser` → `src/test/java/bott/parser/ParserTest.java`). Prefer the
`featureUnderTest_testScenario_expectedBehavior()` naming convention for test methods, e.g.
`parseTaskNumber_zero_exceptionThrown()`.
