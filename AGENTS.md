# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 1 year
* IDE and level of expertise: 1 year

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

## Coding standard

All Java code in this project must follow the se-education Java coding standard
(intermediate level): <https://se-education.org/guides/conventions/java/intermediate.html>

This is enforced by the build, not by memory. `config/checkstyle/checkstyle.xml`
is se-education's own Checkstyle configuration for that standard, and
`./gradlew build` fails if any rule is broken. To check on its own:

```
./gradlew checkstyleMain checkstyleTest
```

The rules that come up most often in this codebase:

* 4 spaces for indentation, 8 for a wrapped line. Never tabs.
* Lines at most 120 characters.
* Imports in four groups, separated by one blank line and each group sorted:
  static imports, then `java.*`/`javax.*`, then third-party (`org.*`, `com.*`),
  then `lebronjames.*`. No wildcard imports.
* Within a class: public fields, then private fields, then constructors, then
  methods. A method must not be written between two field declarations.
* Braces on the same line (K&R), and always present, even for a one-line `if`.
* Boolean names read as questions: `isDone`, `hasTaskListChanged`.
* Collections take plural names: `tasks`, not `taskList`.
* Header comments on every public class and method, except getters, setters,
  and overriding methods whose parent Javadoc still applies.

## Git commit messages

Follow <https://se-education.org/guides/conventions/git.html>:

* Subject line in the imperative mood ("Add find command", not "Added" or
  "Adds"), capitalized, no full stop, and kept short.
* Blank line between the subject and the body.
* Body wrapped at about 72 characters, explaining *why* the change was made and
  what problem it solves, not restating the diff.

## Testing

JUnit 5 tests live in `src/test/java`, mirroring the package of the class under
test (e.g. `lebronjames.parser.Parser` is tested by
`src/test/java/lebronjames/parser/ParserTest.java`). Run them with
`./gradlew test`.

### Coverage target

Aim to cover **the top ~50% highest-value methods** with JUnit tests,
prioritising complex, core, or critical logic over trivial getters and
pass-through printing. In practice this means the parsing, task-list, and
save-file logic is tested thoroughly, while the interactive run loop is not.

`./gradlew test` also writes a JaCoCo report to
`build/reports/jacoco/test/html/index.html`, so the figure can be checked rather
than assumed.

**JUnit tests must be updated in the same change as the code they cover**, so
the project stays at or above that target. Concretely, when you change
production code:

* If you add a method that qualifies as high-value, add tests for it.
* If you change the behaviour of a tested method, update its tests to match the
  new behaviour rather than deleting the failing assertions.
* If a test fails, first decide whether the test or the code is wrong. A test
  that documents a bug is worse than no test.
* Name test methods `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `parse_todoWithoutDescription_exceptionThrown()`.

Leave `./gradlew build` passing before committing.
