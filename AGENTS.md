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

## Coding standard

All Java code in this project follows the SE-EDU Java coding standard at
https://se-education.org/guides/conventions/java/intermediate.html. The points
that come up most often here:

* 4 spaces for indentation, never tabs; 8 spaces for a wrapped line.
* Lines stay within 110 characters (hard limit 120).
* K&R braces, and braces even around a single-statement `if` body.
* Classes are nouns in PascalCase, methods are verbs in camelCase, constants
  are SCREAMING_SNAKE_CASE, booleans read as `is`/`has`/`was`.
* Single-letter names are only for loop counters.
* Imports are listed explicitly, never with a wildcard, ordered static, then
  `java.*`, then project packages.
* Every non-private class and method carries a Javadoc header comment; getters,
  setters and plain overrides are exempt.
* Fields get the smallest scope that works, and are `final` when they do not
  change.

Checkstyle enforces these rules, configured in `config/checkstyle/`. It runs as
part of `./gradlew build`; run it alone with
`./gradlew checkstyleMain checkstyleTest`. Fix violations rather than
suppressing them, unless the rule genuinely does not fit this project.

## Testing

JUnit 5 tests live under `src/test/java`, mirroring the package of the class
under test (e.g. `nova.Parser` is tested by `nova.ParserTest`). Run them with
`./gradlew test`.

Coverage target: the top ~50% highest-value methods, prioritising complex,
core, or otherwise critical logic. Update the tests alongside any code change
so that the target keeps being met; a change that alters behaviour should
change or add a test in the same commit.

Name test methods `featureUnderTest_testScenario_expectedBehavior()`, e.g.
`parse_todoWithoutDescription_exceptionThrown()`.

## Git

Commit messages follow the SE-EDU Git conventions at
https://se-education.org/guides/conventions/git.html:

* Subject line in the imperative mood, capitalised, no trailing period, aimed
  at 50 characters and never past 72. An optional `Component:` prefix is fine.
* Blank line between subject and body; body wrapped at 72 characters.
* The body explains what changed and why, not how, and describes the situation
  the change is fixing.
* Never add AI attribution, co-author trailers, or tool names to a commit
  message or a pull request description.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
