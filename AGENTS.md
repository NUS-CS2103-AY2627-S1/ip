# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Low. I am a year 2 cs students. I have done cs1101s, cs2030s, cs2040s only
* IDE and level of expertise: same as above

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

**All Java code in this repository must follow the se-education.org Java coding
standard at the _intermediate_ level**
(<https://se-education.org/guides/conventions/java/intermediate.html>). The full
rule set is recorded in the project skill `seedu-java-coding-standard`
(`.claude/skills/seedu-java-coding-standard/SKILL.md`, mirrored under
`.codex/skills/`). Read that skill before writing or editing any `.java` file,
and check the changed lines against its self-check list before reporting the
edit as done.

This is not optional and not limited to new files: it applies to `src/main/java`
and `src/test/java` alike, to generated snippets shown in chat, and to any
existing code you are already modifying. If a suggested change would break a
rule, choose a different design rather than breaking the rule.

The points most often got wrong here:

* 4-space indent, no tabs, 120-character hard line limit.
* Explicit imports, grouped (static, `java`, `javax`, third party, project) with
  a blank line between groups.
* Every `if`/`for`/`while` body braced, opening brace on the same line.
* No `public` non-constant fields; fields `private final` where possible.
* Javadoc on every public class and non-trivial public method, first sentence a
  third-person verb phrase ("Returns ...", "Adds ..."), tags punctuated.
* Booleans named `isX`/`hasX`/`wasX`, collections plural, acronyms not
  uppercased (`parseHtml`, not `parseHTML`).
* Test methods named `featureUnderTest_testScenario_expectedBehavior()`.

## Testing

Tests are JUnit 5 (Jupiter) and run with `./gradlew test`.

### Layout and naming

* Test sources mirror the main sources: `src/main/java/zhangwei/task/Todo.java` is tested by
  `src/test/java/zhangwei/task/TodoTest.java`, in the same package.
* One test class per production class, named `<ClassUnderTest>Test`.
* Test methods use `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `parse_todoWithoutDescription_exceptionThrown()`.
* Tests that touch the file system use JUnit's `@TempDir`, never the real `data/` folder.

### Coverage target

* Aim to cover roughly the **top 50% highest-value methods** -- the complex, core, or critical
  ones. In this project that means, in priority order: `Parser`, `Storage`, `TaskList`,
  `CommandType`, the `Command` subclasses that change state (`Add`, `Delete`, `Mark`, `Unmark`),
  and the `Task` hierarchy's `toString`/status behaviour.
* A method is high value when it branches a lot, validates input, throws, converts between
  formats, or can lose the user's data. Trivial getters, constructors, and pure console printing
  are below the line and do not need tests of their own.
* For each covered method, include all reasonable cases: the normal case, the boundaries
  (empty list, first and last task number), and every way it can fail.
* The target is about value, not a raw percentage: 50% of the methods, chosen for risk, not
  50% of the lines.

### Keeping tests in step with the code

**JUnit tests must be updated in the same change as the code, so the 50% target still holds
afterwards.** Concretely, whenever you change production code:

* **New method** -- decide whether it falls in the high-value half. If it does, add tests for it
  in the matching `*Test` class in the same commit.
* **Changed behaviour** -- update the affected tests so they assert the new behaviour. Never
  delete a failing test to make the build green; either the test or the code is wrong, and it has
  to be worked out which.
* **New class** -- create its `*Test` class if the class carries any logic.
* **Removed or renamed method** -- remove or rename its tests too.
* Run `./gradlew test` before committing, and report the result. A change is not finished while
  the suite is red.

## Git

**All commits and branches in this repository must follow the se-education.org
Git conventions** (<https://se-education.org/guides/conventions/git.html>). The
full rule set is recorded in the project skill `seedu-git-standard`
(`.claude/skills/seedu-git-standard/SKILL.md`, mirrored under `.codex/skills/`).
Read that skill before drafting any commit message or creating any branch, and
propose only messages that already comply -- never a draft that has to be fixed
afterwards.

In short:

* Subject line: imperative mood, capitalised, no trailing period, 50 characters
  preferred and 72 the hard limit. An optional `<scope>:` prefix is allowed.
* Body: required for any non-trivial commit, separated from the subject by a
  blank line, wrapped at 72 characters, explaining *what* and *why* rather than
  *how*.
* Branch names: meaningful kebab-case keywords, prefixed with the issue number
  when the branch addresses an issue (`1234-ui-freeze-error`).

Also, for this project:

* Use lightweight tags unless the user requests an annotated tag.
* Do not commit or push unless explicitly asked.
