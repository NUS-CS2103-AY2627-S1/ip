# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Some experience with building web-apps (with AI help).
* IDE and level of expertise: IntelliJ Idea. Knowledge of basic features.

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

All Java code in this project — everything under `src/main/java` and `src/test/java` — **must** follow the se-education.org Java coding standard at the **intermediate** level (which subsumes the basic level):

* Basic: https://se-education.org/guides/conventions/java/basic.html
* Intermediate: https://se-education.org/guides/conventions/java/intermediate.html

The full rule set, as a working checklist, lives in the **`seedu-java-coding-standard`** skill (`.claude/skills/seedu-java-coding-standard/SKILL.md`). Consult it whenever you write, modify, or review Java code here, and keep it in sync if the upstream standard changes.

This is mandatory, not advisory: any code you add or change must comply before the task is treated as done. When you edit a file, also fix any pre-existing violations you can see in it (opportunistic cleanup — no obligation to reformat untouched files). If following the standard would conflict with a specific user instruction, follow the user and say so.

## Testing

Steph has two test layers: the console `test/ui-test-plan.md` (run by the `test-ui` skill) and the JUnit tests under `src/test/java` (run by `./gradlew test`).

After every code update (any change under `src/main/java`), before treating the task as done:

1. Check the changed code against the **`seedu-java-coding-standard`** skill's checklist (see the Coding standard section above) and fix any violations, including line length (`awk 'length > 120' ...`), braces on every control-flow body, explicit imports, and a Javadoc header on every class.
2. Update `test/ui-test-plan.md` if the change affects console output or adds/changes a command — add new test cases for new behavior, and update expected output for existing cases whose output legitimately changed. Generate expected-output blocks from a real run (the `test-ui` skill's `record` mode) rather than hand-typing them.
3. Invoke the `test-ui` skill to run the test plan and confirm it passes.
4. Update the JUnit tests to keep the coverage target below satisfied: add or adjust cases for any high-value logic the change adds or reshapes, add a test class for any new class that carries such logic, and remove tests for deleted code. Run `./gradlew test` and confirm it passes.

If either layer fails, treat that as a signal to investigate before proceeding — either the change introduced a regression, or the expected values are stale and need a deliberate update. Don't edit a test to match broken output just to make it pass.

### JUnit coverage target

Keep focused JUnit tests on roughly the **top 50% highest-value methods** — those with real branching, those core to how the program behaves, and those that would be costly if they broke. At present that means `Parser`, `DateTimes`, `Storage`, and the deliberate `TaskList` contracts (defensive-copy constructor, unmodifiable `asList()` view). Thin one-line delegates, trivial getters, and the `System.in` / `System.out` code in `Ui` and `Steph` are left to the `test-ui` plan and do not need JUnit tests.

This target is a standing requirement, not a one-time task: every change under `src/main/java` must leave the JUnit suite back in compliance with it. If a change adds high-value logic without matching tests, or leaves existing tests asserting the old behavior, the task is not done. Include the test changes in the same commit as the code change.

Follow standard Gradle/JUnit layout and naming: test for `steph.Foo` goes in `src/test/java/steph/FooTest.java`, in package `steph`. When a test method name would get unwieldy, use `featureUnderTest_scenario_expectedBehavior()` (e.g. `parseTaskIndex_zero_exceptionThrown()`).

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
