# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Average
* IDE and level of expertise: Average

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding standard

**All Java code in this project follows the [se-edu Java conventions (intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)**, without exception. That covers every `.java` file in the repository — the tests under `src/test/java` as much as the code under `src/main/java`. Code that breaks the standard is not finished, however well it works.

**Invoke the `seedu-java-coding-standard` skill before writing or editing any `.java` file**, so the code comes out conforming rather than being corrected afterwards, and again after editing one, before reporting that change as done. If the skill is not available — most often because it was added or edited during the current session and has not been picked up yet — read `.claude/skills/seedu-java-coding-standard/SKILL.md` and follow its steps directly instead. Never skip the check because the skill did not load.

The skill keeps the rules written out in `.claude/skills/seedu-java-coding-standard/rules.md` so they can be applied while typing, and treats the page at the URL above as the authority whenever a rule is contested or that file is silent.

## Checking style

This project follows three style guides:

* [se-edu Java conventions (intermediate)](https://se-education.org/guides/conventions/java/intermediate.html), for every `.java` file, tests included. This is the coding standard above, and the `seedu-java-coding-standard` skill is what checks it.
* [se-edu Markdown conventions](https://se-education.org/guides/conventions/markdown.html), for every `.md` file.
* [Google developer documentation style guide](https://developers.google.com/style), for all prose a reader sees: Markdown files, Javadoc, comments, and the messages the program prints.

After writing or editing any `.java` or `.md` file, and before reporting that change as done, **invoke the `style-check` skill**, which covers all three guides and calls the `seedu-java-coding-standard` skill for the Java one. If it is not available — most often because it was added or edited during the current session and has not been picked up yet — read `.claude/skills/style-check/SKILL.md` and follow its steps directly instead. Never skip the check because the skill did not load.

The skill fetches the Markdown and Google guides from their URLs on every run rather than working from an embedded copy, so it follows them as they stand rather than as they stood when it was written. If a guide cannot be fetched, it reports that the check did not run. Treat that as an unfinished check, not as a pass. The Java guide is the one exception: it is written out in the coding standard skill so it can be applied while typing, and that skill fetches the page whenever a rule is contested.

Some of the Java rules are enforced by the build rather than by a skill, so that they hold whether or not anyone remembers to check. Checkstyle covers most of them, and the `checkJavadocTagPunctuation` task covers the one rule no Checkstyle module can express: that a `@param`, `@return`, or `@throws` description ends in punctuation, counting a description that wraps onto the lines below it. Both run as part of `./gradlew check`. A skill reports; the build refuses.

Run the style check **before** the tests below. A style fix can change what the program prints, and the tests have to run against the code as it will be left, not as it was midway through.

## Unit tests

The JUnit tests live under `src/test/java`, in a package and a file that mirror the class under test: `alfred.Parser`, in `src/main/java/alfred/Parser.java`, is tested by `alfred.ParserTest`, in `src/test/java/alfred/ParserTest.java`. Name each test method `featureUnderTest_testScenario_expectedBehavior`, as in `parse_emptyLine_exceptionThrown`. Run the tests with `./gradlew test`.

**Aim to cover the top 50% of the methods in `src/main/java`, ranked by value.** Rank each method by what breaks if it is wrong and by how much a test can see of it, then test the more valuable half. Complex, core, and critical business logic comes first: index and offset arithmetic, data that has to survive a round trip to disk, and anything whose wording the user reads and acts on. The half to leave out is the methods that only print, only wire the program together, or only return a field. Say which methods you counted as the valuable half and why, since the target is a judgment rather than a measurement, and this project has no coverage tool to settle it.

After every change to the code under `src/main/java`, and before reporting that change as done:

1. **Add or update the JUnit tests the change affects**, so that the target still holds. A new method that belongs in the valuable half needs tests of its own. A changed method needs its existing tests brought back into line, and a method that has become more important needs the tests it never had.
1. **Run `./gradlew test`** over the whole suite.
1. **Report the result**, including how many tests ran. On failure, show the failing case with its expected and actual values. Do not describe a code change as complete or working while a test is failing.

Run the unit tests **after** the style check and **before** the text UI tests. A unit test is faster and points at one method, so a fault it can catch is cheaper to find there than in a console session.

When a case fails, decide whether the program or the expectation is wrong, and fix that one. Never weaken an assertion, delete a case, or relax a test to make the suite pass: a test edited to agree with a bug is worse than no test at all. Check as well that a new test can fail. An assertion that holds whatever the code does costs the same to run as a real one and protects nothing.

## Testing the text UI

After every change to the code under `src/main/java`, and before reporting that change as done:

1. **Update `test/ui-test-plan.md` if needed.** Add a test case for a new command or behavior, and update the expected output of any existing case whose output the change alters. A change that only reorganizes internals without altering what the program prints needs no update to the plan; in that case say so explicitly, rather than skipping the step silently.
1. **Run the `test-ui` tests** over the whole plan against the rebuilt program. Invoke the `test-ui` skill. If it is not available — most often because it was added or edited during the current session and has not been picked up yet — read `.claude/skills/test-ui/SKILL.md` and follow its steps directly instead; it is a plain Markdown procedure of shell commands, so it runs the same way with or without a skill mechanism. Never skip the tests because the skill did not load.
1. **Report the result.** On success, show the console session. On failure, show the failing case with its expected and actual output. Do not describe a code change as complete or working while a case is failing.

Do these three steps in that order. Running the tests first and then editing the plan to match whatever the program happened to print makes the tests agree with bugs instead of catching them. Similarly, when a case fails, fix the program or correct the expected output deliberately, after deciding which of the two is actually wrong; never edit the plan just to make the test pass.

Expected output belongs in the plan only after it has been produced by an actual run and read line by line to confirm it is correct. See `.claude/skills/test-ui/SKILL.md` for how cases are written, run, and compared.

## Git

**Every commit in this project follows the [se-edu Git conventions](https://se-education.org/guides/conventions/git.html)**, without exception, and so does every branch name. That covers the subject line, the body, and the name of the branch the commit lands on. A message that breaks the conventions is not ready to commit, however accurate it is.

**Invoke the `seedu-git-standard` skill before drafting a commit message**, so the message comes out conforming rather than being corrected afterwards, and again before proposing one for approval or creating any commit. Invoke it when naming or renaming a branch too. If the skill is not available — most often because it was added or edited during the current session and has not been picked up yet — read `.claude/skills/seedu-git-standard/SKILL.md` and follow its steps directly instead. Never skip the check because the skill did not load.

The skill keeps the rules written out in `.claude/skills/seedu-git-standard/rules.md` so they can be applied while drafting, and treats the page at the URL above as the authority whenever a rule is contested or that file is silent. It also covers getting a message approved, and correcting one that has already been committed.

A subject line is worth more care than most checks, because it is the one thing that cannot be fixed later: an unpushed message can be amended, a pushed one cannot be corrected without rewriting published history.

Use lightweight tags unless the user requests an annotated tag. The se-edu conventions say nothing about tags, so this rule is the project's own.
When proposing or creating a commit message, include enough detail to explain the rationale for the change. Weigh this against the size of the change: a small, self-explanatory commit does not need a body.
Do not commit or push unless explicitly asked.
