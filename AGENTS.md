# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Decent
* IDE and level of expertise: Decent

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

All Java code in this project — new and edited — must comply with the SE-EDU intermediate Java coding standard. **Invoke the `seedu-java-coding-standard` skill before writing or reviewing Java code**, and check the result against it. Key points: 4-space indent, lines ≤ 120 chars, K&R braces on every block (even one-liners), `switch` cases indented from `switch`, explicit imports in the standard group order, predicate-style boolean names, and Javadoc on every class, every public method, every non-trivial private method, and `protected` fields.

The project also runs Checkstyle (`config/checkstyle/checkstyle.xml`, the AB3 rule set) as the mechanical backstop for everything above that's expressible as a rule. Run `./gradlew checkstyleMain checkstyleTest` after any code change and fix what it reports — it's the tiebreaker for anything the skill's judgment calls leave ambiguous.

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
All commit messages must follow the SE-EDU Git conventions — **invoke the `seedu-git-standard` skill before proposing a commit message or branch name**. In short: imperative-mood subject, capitalised, no trailing full stop, ≤ 50 chars; then a blank line and a body wrapped at 72 chars that explains what changed and why (not how); keep unrelated changes in separate commits (e.g. a code change and an agent-file change are two commits); branch names are kebab-case keywords.

## Testing after code changes

After each update to the program's code, before considering the change done:

1. Update `test/ui-test-plan.md` if the change affects console behavior (new/changed commands, output format, etc.) — add or edit test cases so the plan still reflects expected behavior. No edit is needed for changes that don't affect the UI (e.g. pure refactors).
2. Invoke the `test-ui` skill to run the test plan against the program. If a test case fails, stop and fix the issue (or the test case, if the new behavior is intentional) before proceeding.
3. Update the JUnit tests under `src/test/java` so they still meet the coverage target below, then run `./gradlew test` and make sure it passes.

## JUnit test coverage

- **Target: cover nearly all code that can be tested automatically.** This includes not just the obviously complex classes (command parsing, the data-file format, `TaskList` operations, date/time handling) but also glue/integration code (`Lebron.getResponse`, dispatching every command type end to end), thin I/O wrappers (`Ui`, tested by redirecting `System.in`/`System.out`), and simple value objects (`ParsedCommand`'s static factories). Trivial one-line pass-throughs (e.g. `LebronException`'s constructor, `Lebron.main`) still don't need a dedicated test.
- **The JavaFX GUI (`Main`, `MainWindow`, `DialogBox`) is exempt** — it isn't practically unit-testable without a UI test framework this project doesn't have. Verify GUI changes manually (`./gradlew run`) instead.
- **JUnit tests must be kept up to date after every code change** to keep meeting this target: add tests for any new automatable method, and revise existing tests whose expected values the change alters. A code change is not done until `./gradlew test` passes.
- Test classes mirror the package of the class under test and are named `<ClassName>Test` (Gradle/JUnit convention), e.g. `lebron.parser.Parser` → `src/test/java/lebron/parser/ParserTest.java`.
- Long test method names may use the `featureUnderTest_scenario_expectedBehavior()` convention, e.g. `parse_todoWithoutDescription_throws()`.
