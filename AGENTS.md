# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Year 2 Computer Science student
* IDE and level of expertise: Some experience with IntelliJ and building full-stack applications

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious, in the form the coding standard below prescribes.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.
  * When suggesting code, use the /present-changes-visually skill to compare the code with/without the code suggested by Claude.

# Project-specific requirements

* Do not make any changes to the code until the student has reviewed the code.

## Coding standard

All Java code in this repository — under `src/main/` and `src/test/` alike —
**must** follow the SE-EDU Java coding standard (basic + intermediate),
<https://se-education.org/guides/conventions/java/intermediate.html>.

**Invoke the `seedu-java-coding-standard` skill before writing or editing any
`.java` file**, and again before reporting Java work as finished. The skill holds
the rules in full, plus a self-audit checklist and the greps that catch the
mechanical ones.

The standard is not advisory here. Code that breaks it is unfinished, in the same
way that code with a failing test is unfinished:

* Write new code to the standard the first time, rather than fixing style
  afterwards — a second style pass costs the student a second review.
* When editing existing code, bring the lines you touch into line with the
  standard, but leave untouched code alone. A diff that mixes a behaviour change
  with a whitespace sweep is much harder to review.
* Say which rule a change follows whenever the reason is not obvious, so the
  student learns the rule and not just the edit.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing

Whenever code under `src/main/` has been changed — whether Claude applied the change after the student reviewed it, or the student wrote it themselves — do all of the following before reporting the work as finished:

1. **Update `test/ui-test-plan.md` if the change affects the text UI.** Revise the expected output of every test case the change touches, and add a test case for behaviour that is new. Changes that are invisible from the console (renaming a private field, extracting a helper method) need no update to the plan.
2. **Invoke the `test-ui` skill** to run the plan, and show the student the console session it prints. The transcript is the evidence that the change actually works, so do not replace it with a summary.
3. **Update the JUnit tests so the coverage target below still holds.** Add cases for behaviour that is new, revise the cases the change invalidates, and delete the cases for code that no longer exists. A method that has just become more complex may have crossed into the covered half — say so, and add tests for it.
4. **Run `./gradlew test`** and show the student the result. As with the console transcript, the number of tests that ran and passed is the evidence; report it rather than asserting that the tests pass.

A change is not finished until both kinds of tests pass. When one fails, say whether the code or the test/plan is the thing that is out of date, and correct that one — never edit an expected output merely to make a failing test pass, because that turns a bug into the documented behaviour.

Keep the tests/plan and the code in step in the same commit, so the repository never records a state where they disagree.

### JUnit coverage target

Aim to cover the **top ~50% highest-value methods** with JUnit tests. This is a target about which methods are worth testing, not a line-coverage percentage — do not chase a number from a coverage tool. Rank methods by what breaks when they are wrong, not by how easy they are to test:

* **Complex** — several branches, or a rule that is easy to get subtly wrong: an off-by-one, a separator, an escape.
* **Core** — on the path that every command takes.
* **Critical** — its failure loses the user's data, or goes unnoticed until much later.

The other half is left uncovered on purpose. Two kinds of method belong there:

* Methods whose whole job is console output, and the main conversation loop. `test/ui-test-plan.md` already drives these end to end, and a JUnit copy would double the maintenance for no new information.
* Trivial getters, and constants.

Say which half a method falls in whenever the answer is not obvious.

Prefer a test that would fail if the method were wrong over one that merely runs it. When a test exists to protect a particular decision — a strict date resolver, an escaped separator, a defensive copy — check that it really does fail when that decision is undone, and report that you checked.

### Writing JUnit tests

* **Where:** mirror the package under `src/test/java/`, naming the class after the class it tests. `piplupbot.task.Todo` in `src/main/java/piplupbot/task/Todo.java` is tested by `piplupbot.task.TodoTest` in `src/test/java/piplupbot/task/TodoTest.java`.
* **Naming:** a test method's name says what it checks. Where that gets unwieldy, use `featureUnderTest_testScenario_expectedBehavior()`, e.g. `sortList_emptyList_exceptionThrown()`.
* **Javadoc:** as everywhere else in this project, explain *why* a case is worth having when its name does not already say so — above all for a case guarding a decision someone might otherwise simplify away.
* **What is available:** JUnit 5 with `junit-jupiter-api` only. `@Test`, `assertEquals`, `assertThrows` and `@TempDir` all work; `@ParameterizedTest` needs `junit-jupiter-params` added to `build.gradle` first.
* **Never let a test touch the real save file.** Point `Storage` at a path inside a `@TempDir` folder.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
