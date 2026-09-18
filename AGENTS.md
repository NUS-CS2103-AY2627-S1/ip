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

**All Java in this repository must follow the SE-EDU Java coding standard (intermediate level): <https://se-education.org/guides/conventions/java/intermediate.html>.** The rules are written out in the project skill `.claude/skills/seedu-java-coding-standard/SKILL.md`; invoke that skill before writing, editing, or reviewing any Java here, and check the finished code against its self-check list.

This is not advisory. Any new or edited Java — main sources and tests alike — must comply: 4-space indent, lines within 120 chars, K&R braces on every conditional and loop body, explicit imports in the order `static` / `java` / `javax` / `org` / `com` / project, verb method names, boolean names that read as booleans, and a Javadoc whose first sentence begins with a verb on every class and public method.

## Testing

JUnit 5 tests live under `src/test/java/`, mirroring the main source package structure. A test class is named `<ClassName>Test` (e.g. `myriad.parser.Parser` is tested by `src/test/java/myriad/parser/ParserTest.java`). Run the suite with `./gradlew test`; the HTML report lands at `build/reports/tests/test/index.html`.

Name test methods `featureUnderTest_testScenario_expectedBehavior()`, e.g. `resolveIndex_emptyList_exceptionThrown()`.

**Coverage target: the top ~50% highest-value methods** — those carrying complex, core, or business-critical logic. Console formatting (`Ui`) and thin I/O wrappers are deliberately out of scope; their behaviour is covered by the scripted transcript checker in `claude-test/`.

**JUnit tests must be updated in the same change as any code change, so the suite stays at that target.** Adding a branch means adding a case for it; changing an error message, a date format, or the save format means updating the assertions that pin it down. A change that leaves the suite passing only because nothing tests the new code has not met the target.

Where a test documents a known defect rather than intended behaviour, assert what the code currently does and mark it with a `// Known limitation:` comment explaining the cause — so the test is not later "fixed" by loosening it.

Note that `junit-jupiter-params` is not on the classpath, so `@ParameterizedTest` is unavailable; use a plain `@Test` with a loop over an array of cases instead.

## Git

**All commits and branch names in this repository must follow the SE-EDU Git conventions: <https://se-education.org/guides/conventions/git.html>.** The rules are written out in the project skill `.claude/skills/seedu-git-standard/SKILL.md`; invoke that skill before proposing or writing any commit message or branch name here.

This is not advisory. Every commit message must have an imperative, capitalised subject of at most 50 characters (72 hard limit) with no trailing period, and — for anything non-trivial — a body separated by a blank line, wrapped at 72 characters, explaining what and why rather than how. Branch names are kebab-case keywords, prefixed with the issue number when the branch addresses an issue (`1234-ui-freeze-error`).

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
