# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner

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

All Java code in this project — under `src/main/java` and `src/test/java` alike — **must follow the SE-EDU Java coding standard (intermediate level)**. It is recorded in the `seedu-java-coding-standard` skill (`.claude/skills/seedu-java-coding-standard/SKILL.md`), which is the authority; the standard itself is at <https://se-education.org/guides/conventions/java/intermediate.html>.

**Invoke the `seedu-java-coding-standard` skill before writing or editing any `.java` file**, and apply the standard while writing rather than as a clean-up pass afterwards. The same applies when reviewing Java code for style, or when asked whether some code conforms.

The rules most often broken, as a reminder: 4 spaces and never tabs; lines under 110 characters and never over 120; K&R braces; braces on every conditional and loop body however short; explicit imports and never a fully qualified name written inline; methods named as verbs; booleans named `isX`/`hasX`/`wasX`; collections named in the plural; a Javadoc header on every public class and method, its first sentence a summary beginning `Returns ...`, `Adds ...` and so on.

Checkstyle enforces the mechanical part of the standard, using the SE-EDU AddressBook Level 3 rules in `config/checkstyle/`. Both of these must pass with no violations:

```
./gradlew checkstyleMain checkstyleTest
```

`./gradlew javadoc` must likewise report **zero warnings**. Treat a new violation or warning as a defect to fix, not as noise, and do not loosen a Checkstyle rule to make code pass.

Where the skill records a project-specific decision — such as `Dates.today()` keeping its noun name, or a `{@link}` being avoided because it would create a package dependency — follow it rather than reopening the question.

## Git

**All commits and branches must follow the SE-EDU Git conventions**, recorded in the `seedu-git-standard` skill (`.claude/skills/seedu-git-standard/SKILL.md`), which is the authority; the conventions themselves are at <https://se-education.org/guides/conventions/git.html>.

**Invoke the `seedu-git-standard` skill before drafting any commit message or naming any branch.**

In short: the subject line is in the imperative mood, capitalised, has no full stop, and is at most 50 characters (72 hard). A non-trivial commit has a body, separated from the subject by a blank line and wrapped at 72 characters, explaining WHAT and WHY rather than HOW, and avoiding the words "currently" and "originally". Branches are kebab case, except increment branches, which are `branch-<TagName>` because the course instructions and the progress dashboard both expect that form.

Use lightweight tags unless the user requests an annotated tag.
Do not commit, tag or push unless explicitly asked; prepare the change and hand over the commands.
Remember that `git push` does not carry tags, and that the branch needs pushing as well as the tag.

## Testing after code changes

The project has two test suites, which cover opposite ends of the program and do not replace each other.

* **JUnit unit tests** live in `src/test/java`, mirroring the packages under `src/main/java`. Run them with `./gradlew test` (on Windows, `gradlew.bat test`). They check that one method handles every input it can be handed.
* **Text-based UI tests** live in `test/ui-test-plan.md`, run by the `test-ui` skill (`.claude/skills/test-ui/scripts/run-ui-tests.py`). They start the whole program, type commands at it, and compare the entire console output. They check that the assembled program behaves correctly for a user.

### JUnit coverage target

**Aim to have JUnit tests for nearly all code that can be tested automatically.** Where effort has to be spent first, spend it by how much logic a method holds multiplied by how badly a silent failure in it would hurt: complex, core or critical business logic comes before anything else.

Every package under `src/main/java` is covered, `elsa.gui` aside. Each class is tested at its mirrored path, including `Ui`, whose wording is what the user actually reads, and every `Command` subclass, each assembled with its own task list, user interface and store rather than reached only through `Elsa.getResponse`.

Deliberately outside it:

* `elsa.gui.Main`, `elsa.gui.Launcher`, `elsa.gui.DialogBox` and `elsa.gui.MainWindow`, which build and show a window and would need a running JavaFX toolkit. The course says to test the GUI by hand instead, so changes there are checked by running the program and looking.
* `Elsa.main` and `Launcher.main`, which only hand off to something else.
* `Elsa.run`, the terminal session loop, which all of the text UI cases drive from end to end. That suite is a better test of it than anything written with redirected streams.
* Plain getters and methods that pass straight through to a field or a collection, where a test could only restate the field.

**The language the computer is set to is checked by hand, not by JUnit.** `Dates` builds its formatters once, when the class is first loaded, so a test that changes the default locale afterwards is testing a formatter that was already built and passes whether or not `Locale.ENGLISH` is named. To check it for real, run the jar in another language and confirm a date still reads `Oct 15 2019`:

```
java -Duser.language=fr -Duser.country=FR -jar elsa.jar
```

**The JUnit tests must be updated after each code change so that this target continues to hold.** Concretely, in the same commit as the change:

* a new method that can be tested automatically needs tests before the commit, not after;
* a changed method needs its existing tests updated to match the new behaviour, derived from the requirements rather than from what the code now returns;
* a method that moves or is renamed takes its test class with it, so the mirrored path and the `ClassNameTest` name stay correct;
* a deleted method has its tests deleted with it.

Follow the Gradle and JUnit conventions: `src/test/java/<same package path>/<ClassName>Test.java`, and test methods named `featureUnderTest_testScenario_expectedBehavior`.

Prefer tests that would actually fail if the method were wrong. A quick way to confirm one would: change the method under test so that it is wrong, check that the expected tests fail and that they name the right thing, then put the method back.

### What to do after each change under `src/main/java`

Before proposing a commit:

1. **Run Checkstyle** with `./gradlew checkstyleMain checkstyleTest` and fix every violation it reports.
2. **Update the JUnit tests** as described above, then run `./gradlew test` and report how many tests passed.
3. **Update `test/ui-test-plan.md` if needed.** Add a test case for each new command or behaviour, and update the expected output of existing cases when the change alters what the program prints. Every case's expected output contains the banner, greeting, and borders, so a change to any of those means updating every case. Derive expected output from the increment's requirements, not from what the program currently prints — expected output copied from actual output records present behaviour as correct and cannot detect an existing bug.
4. **Invoke the `test-ui` skill** to run the UI suite.
5. **Show the test session transcript** — the commands typed and the console output — and report how many cases passed. Show it rather than only summarising it.
6. **If anything fails, stop there.** Report the expected and actual results, and say whether the code or the test is at fault. Do not edit either one purely to make a suite pass.

Changes that do not touch `src/main/java` — documentation, skills, or a test plan itself — do not need a test run.
