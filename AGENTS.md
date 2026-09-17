# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: basic
* IDE and level of expertise: basic

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

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Testing

JUnit 5 tests live in `src/test/java`, mirroring the package of the class under test
(e.g. `harvey.task.TaskList` is tested by `src/test/java/harvey/task/TaskListTest.java`).
Run them with `./gradlew test`; `./gradlew build` runs them too.

Name test methods `featureUnderTest_testScenario_expectedBehavior()`, e.g.
`get_taskNumberJustPastEnd_exceptionThrown()`.

**Coverage target: nearly all code that can be tested automatically.** That means every
class outside the GUI, including error paths, the `Harvey` class that ties the parts
together, and the text interface's reading and printing (by swapping `System.in` and
`System.out` for in-memory streams). Leave out only:

* the JavaFX GUI (`DialogBox`, `MainWindow`, `Main`, `Launcher`), which is checked by hand
  using `docs/ManualTesting.md`;
* code that touches the real `./data/harvey.txt` (`Harvey()` and `Harvey.main`);
* branches that cannot be reached, such as `default:` cases for every enum constant already
  handled, and the failure side of `assert` statements.

Tests must pass whatever the computer's language setting, and a test that cannot run on
some OS (e.g. one needing POSIX file permissions) should be skipped there with an
assumption rather than fail.

**Update the JUnit tests as part of any code change**, in the same commit, so the project
stays at that target. A change that adds or alters non-trivial behaviour should add or
alter the tests that cover it; a change that removes behaviour should remove its tests.

## Coding and commit standards

This project follows the SE-EDU standards. Two project skills hold the rules in full,
in `.claude/skills/`:

* **`seedu-java-coding-standard`** — the SE-EDU Java coding standard (basic and
  intermediate). Follow it for **all** Java in this project, main and test alike.
  Load it before writing or editing any Java file.
* **`seedu-git-standard`** — the SE-EDU Git commit message convention. Follow it for
  **every** commit message proposed or created here. Load it before writing one.

The points most often got wrong in this codebase:

* 4-space indent, no tabs; soft line limit 110 characters, hard limit 120.
* Javadoc on every non-private class and method. Blank line before the tag section, and
  a full stop at the end of every `@param`, `@return` and `@throws` description.
* Comments and Javadoc use American spelling (`behavior`, `serialize`). Text shown to the
  user is not a comment — do not change its wording for spelling alone, because that
  changes program output and breaks the expected-output checks.
* Commit subjects: imperative, capitalized, no trailing full stop, 50 characters soft and
  72 hard. Body wrapped at 72, explaining what and why rather than how.
