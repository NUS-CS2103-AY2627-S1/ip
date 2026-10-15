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

## CS2103T coding standards

All Java code in this repository, under `src/main/java` and `src/test/java` alike, must follow
the SE-EDU Java coding standard at
<https://se-education.org/guides/conventions/java/intermediate.html>. This is mandatory, not
advisory: write new code to the standard, and bring any code you touch into line with it.

The full rules, and the commands that check code against them mechanically, live in the
`seedu-java-coding-standard` skill. In Claude Code, use the skill in
`.claude/skills/seedu-java-coding-standard/`. In Codex, invoke it as
`$seedu-java-coding-standard`. Read that skill before writing or reviewing Java code here.

Where this file and the skill appear to differ, the skill is correct: it holds the full
standard, while this file only points to it.

## JUnit test coverage

Aim to cover the top half of the codebase by value with JUnit tests: roughly the 50% of methods
carrying the most complex, core, or otherwise critical logic. Parsing, task-list operations, and
reading and writing the data file are in that half. Trivial accessors, one-line overrides, and
methods that only print to the console are not, and are left to the UI test plan instead.

After every code update:

1. Add or update the JUnit tests so the target still holds. A new method in that top half needs
   tests of its own; a method whose behaviour changed needs its existing tests changed to assert
   the new behaviour, rather than deleted.
2. Cover the reasonable cases for each method under test, not just one: the ordinary result, the
   boundaries, and each way the method is meant to fail.
3. Run the tests from the repository root:

   ```bash
   ./gradlew test
   ```

   If a test fails, stop and report the failure rather than adjusting the test to match the new
   behaviour without saying so.

Follow the Gradle and JUnit conventions for placement and naming: a test class lives under
`src/test/java` in the same package as the class it tests and is named after it, such as
`happybot.Parser` being tested by `happybot.ParserTest` in
`src/test/java/happybot/ParserTest.java`. Name a test method for what it checks, using
`featureUnderTest_testScenario_expectedBehavior()` when a plain name would not be clear enough.

## UI test verification

After every code update:

1. Review `src/test/ui-test-plan.md` and update it when the change adds or alters console behaviour, commands, or expected output.
2. Run the UI test plan with the `test-ui` skill. In Codex, invoke it as `$test-ui`. In Claude Code, use
   the `test-ui` skill in `.claude/skills/`. Any agent that finds neither should run the shared runner
   directly from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py src/test/ui-test-plan.md
   ```

   All three routes run the same script. Do not substitute an ad-hoc test script for it. If the build or
   a test case fails, stop the test session immediately and report the failure.

## Git

Every commit and branch in this repository must follow the SE-EDU Git conventions at
<https://se-education.org/guides/conventions/git.html>. This is mandatory, not advisory: write
every future commit message and branch name to the standard.

The full rules, and the commands that check history against them, live in the
`seedu-git-standard` skill. In Claude Code, use the skill in `.claude/skills/seedu-git-standard/`.
In Codex, invoke it as `$seedu-git-standard`. Read that skill before writing or proposing a
commit message or creating a branch.

Where this file and the skill appear to differ, the skill is correct: it holds the full
standard, while this file only points to it.

These project rules sit alongside the standard, which does not cover them:

* Use lightweight tags unless the user requests an annotated tag.
* Give the body enough detail to explain the rationale for the change.
* Do not commit or push unless explicitly asked.
