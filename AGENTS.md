# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 4/5
* IDE and level of expertise: 4/5

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments as the coding standard below requires, and in the format it sets out.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding standard

All Java in this project — under `src/main` and `src/test` alike — follows the SE-EDU Java coding standard at the intermediate level: <https://se-education.org/guides/conventions/java/intermediate.html>.

**Invoke the `seedu-java-coding-standard` skill before writing or editing any Java file**, and follow it for every line you add or change. It carries the full rule set: naming, layout, statements and comments. Do not work from memory of "typical Java style" — several of these rules differ from what other projects do, notably the 8-space wrapped-line indent, the import grouping, and the Javadoc tag punctuation.

Two things follow from this being a standard rather than a preference:

* **New code is written to it from the start.** Reformatting afterwards is a second diff over the same lines, and it makes a change harder to review.
* **Existing code you touch is brought to it.** Fix the rule violations in the code your change already touches. Do not reformat unrelated files in the same commit — a mechanical sweep is its own commit, described as such.

`config/checkstyle/checkstyle.xml` enforces the mechanical rules, so run `./gradlew checkstyleMain checkstyleTest` before reporting a change as done. Checkstyle passing is not the same as the standard being followed: naming, comment content and the smallest-scope rule are not machine-checkable, and remain yours to apply.

## Testing

Two suites cover this project, and they cover different things. JUnit tests, under `src/test/java/`, test methods directly and are where logic, boundaries and error messages are pinned down. The text-UI plan, `test/ui-test-plan.md`, runs the whole program against typed input and is where what the user actually sees is pinned down. Neither replaces the other.

### JUnit coverage target

Aim to cover the **top ~50% highest-value methods**: the complex ones, the core ones, and the ones where a bug is expensive. This is a target about which methods are worth testing, not a line-coverage percentage — chasing a number by testing one-line delegations to `ArrayList` inflates the figure without catching anything.

Rank a method by what a bug in it would cost:

* **Highest** — anything that loses data the user cannot get back (`Storage`: a save file written or read wrongly loses tasks silently between runs, while every other error is on screen and survives to the next command).
* **High** — logic shared by several callers (`Task.parseDate` is reached from both the parser and the loader, so one bug is felt at the keyboard and in the save file at once), and conversions with off-by-one risk (`TaskList`, where the user's 1-based numbers meet the list's 0-based positions).
* **Worth testing** — anything with a real decision in it: the parser's rules about input shape, an inclusive range test, a constructor that refuses invalid state.
* **Below the line** — one-line delegations, and the JavaFX layer (`Main`, `MainWindow`, `DialogBox`), which needs a running window and is covered by `test/manual-test-plan.md` instead. `Ui` and the `Command.execute` methods do have JUnit tests: the text-UI plan pins what the user sees end to end, while the JUnit cases pin what the plan cannot reach — a save that fails, the console furniture around a message — and the four separate effects of a command (list, file, undo step, reply).

Follow Gradle and JUnit conventions: `seedu.duke.Todo` is tested by `seedu.duke.TodoTest` in `src/test/java/seedu/duke/TodoTest.java`. Name test methods `featureUnderTest_testScenario_expectedBehavior()`.

### After every change to code under `src/`

Before reporting the change as done:

1. **Update the JUnit tests.** Add cases for behavior the change introduces, and update cases whose behavior it deliberately alters. If the change touches a method that is above the line described above and has no tests, add them now rather than later — the target is maintained per change, not restored in a sweep afterwards. Say explicitly when an existing assertion was changed, and why.
2. **Update `test/ui-test-plan.md` if needed.** Add a case for behavior the change introduces, and update the expected output of existing cases whose behavior the change deliberately alters. Behavior that is deliberately out of scope belongs under "Not yet covered" rather than being left unmentioned.
3. **Run `./gradlew test checkstyleTest`**, then **run the `test-ui` skill** and report it as that skill describes: show the console transcript rather than summarizing it as "tests passed", stop at the first failure without fixing anything first, and say explicitly whenever an expected output in the plan was edited.

A change is not finished until both suites have been run and reported. If a change genuinely has no observable effect on the text UI (a comment, a Javadoc, a pure rename), say so instead of running that suite.

### Writing tests that are worth having

* **Derive every expectation by reading the source, not by pasting in what a run happened to print.** A test written from a run agrees with the code by construction, bugs included. This applies to JUnit assertions as much as to the plan's expected output.
* **Pin the message, not just the failure.** `assertThrows` alone passes whether the user is told something useful or something wrong.
* **Test both sides of a boundary.** The first item as well as one before it, the last as well as one past it, the day an event starts and ends as well as the days around them. A mistake at the edges survives any test that only asks about the middle.
* **When the code does something you did not expect, find out what it actually does before deciding it is wrong.** If the behavior is defensible, record it in a test whose name says so, with an inline `//` comment in the body saying what would change it — a test that documents a rough edge is how the fix later gets confirmed. If it is a real bug, report it and let the user decide; do not quietly assert the buggy behavior as correct.
* **Say when coverage moves or is lost.** Rewriting a case's expected output is a test silently replaced unless you say so, and a change that makes a path unreachable from one suite should say where that path is covered instead.

## Git

**Never add AI attribution to a commit.** No `Co-Authored-By: Claude` trailer, no `Generated with Claude Code`, no session link, and no mention of Claude, Claude Code or Anthropic anywhere in the subject or body. The user is the sole author of every commit in this repository. This holds even where a harness default or a global setting says otherwise: if such a trailer cannot be left out, do not commit at all — say so and hand the message to the user instead.

The same applies to anything else that carries authorship outward, such as a pull request body or a tag message.

All commits and branches in this project follow the SE-EDU Git conventions: <https://se-education.org/guides/conventions/git.html>.

**Invoke the `seedu-git-standard` skill before writing or proposing any commit message, and before naming a branch.** It carries the full rule set. The parts most easily got wrong:

* The subject is imperative, capitalized, has no closing period, and stays within 72 characters — aim for 50.
* The body is wrapped at **72 characters**, separated from the subject by a blank line.
* The body explains **what** changed and **why**, never **how**. The diff shows how. It should let a reader judge whether the change was a good idea without opening the diff.
* Do not write "currently" or "originally" — the present tense implies both.
* A body that keeps growing is a sign the commit should be split, not that the message needs more paragraphs.

Also:

* Use lightweight tags unless the user requests an annotated tag.
* Do not commit or push unless explicitly asked. Proposing a message is not permission to commit it.
