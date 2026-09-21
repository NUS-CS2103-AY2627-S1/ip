# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: terminal and intermediate

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

All Java code in this project follows the SE-EDU Java coding standard
(intermediate level). The rules are captured in the project skill
`seedu-java-coding-standard`; load it before writing or editing any `.java`
file, and apply it to new code as well as to code being changed.

Most of the standard is enforced by Checkstyle: `./gradlew build` runs
`checkstyleMain` and `checkstyleTest` against `config/checkstyle/checkstyle.xml`,
so a violation fails the build. Run `./gradlew checkstyleMain checkstyleTest`
to check without building everything; reports land in
`build/reports/checkstyle/`.

The rules Checkstyle cannot check, and so the ones most often missed here:
comments use American spelling, and a Javadoc summary should say something the
method name does not already say.

## Git

All commits follow the SE-EDU Git conventions, captured in the project skill
`seedu-git-standard`. Load it before writing or proposing a commit message.

In short: an imperative, capitalized subject line of at most 50 characters
with no trailing period, a blank line, then a body wrapped at 72 characters
that explains what and why rather than how.

## Testing

JUnit 5 tests live in `src/test/java`, mirroring the package of the class
under test (`goat.task.TaskList` is tested by `src/test/java/goat/task/TaskListTest.java`).
Run them with `./gradlew test`; `./gradlew build` runs them too.

Name test methods `featureUnderTest_testScenario_expectedBehavior()`,
e.g. `delete_outOfRange_exceptionThrown()`.

**Coverage target: keep line coverage above 95%**, measured by
`./gradlew jacocoTestReport` (report in `build/reports/jacoco/test/html/`).
It currently sits at 97.4%.

The GUI package is excluded from the measurement: a JavaFX control cannot be
built without a toolkit, and counting those classes would make the figure
meaningless. It is covered by `docs/manual-testing.md` instead. The logic
behind the GUI is not excluded -- `Goat.getResponse` and the three flags beside
it are the whole interface between the window and the program, and are tested.

Unreachable defensive code is also allowed to stay uncovered: the `assert
false` in an impossible switch branch, and the catch blocks that re-wrap a
checked exception a lambda cannot propagate. Contorting a test to reach them
would make the test lie about what the code does.

Keep the tests in step with the code: whenever a method in that top half
changes, gains a branch, or is added, update or add its tests in the same
change rather than leaving it for later.

## Git: project-specific

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
