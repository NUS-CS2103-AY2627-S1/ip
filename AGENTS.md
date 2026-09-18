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

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

All commit messages (and any new branch names) from now on must follow the SE-EDU Git conventions — use the `seedu-git-standard` skill for the full rules (imperative-mood subject line, 50/72-char limits, what/why-not-how body, kebab-case branch names). Apply it whenever drafting a commit message, whether or not the user mentions "convention" or "format"; it governs how a commit is written, not whether to make one.

## Java coding standard

All Java code in this project (new or edited, main or test) must follow the SE-EDU intermediate Java coding standard — use the `seedu-java-coding-standard` skill for the full rules (naming, layout, import order, brace/whitespace style, Javadoc conventions). Apply it by default while writing or editing any `.java` file, not only when explicitly asked about style, and fix violations you notice in code you're already touching for another reason.

Checkstyle enforces this standard automatically (config in `config/checkstyle/`, matching [se-edu/addressbook-level3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle)). Run `./gradlew checkstyleMain checkstyleTest` after any Java change — it's also wired into `./gradlew build`/`check`, so a build failure there is a real violation to fix, not something to bypass or exclude. Note its `caseIndent`-based switch-statement style (`case` indented 4 spaces from `switch`, its body 4 more) differs from ordinary block indentation — match it in new code.

## Testing

Test coverage target: JUnit tests should cover the top ~50% highest-value methods in the codebase — prioritize complex, core, or critical business logic (e.g. parsing/validation logic, date handling, matching/range logic) over trivial getters/setters, one-line delegates, or println-driven orchestration methods that are better covered by console-level testing.

After any code change (new method, changed logic, changed method signature), update JUnit tests as needed to keep the codebase compliant with that 50% target: add tests for newly-introduced high-value logic, and fix/update existing tests whose expected behavior changed. Don't let test coverage silently drift below target as the codebase grows.

Tests live under `src/test/java`, mirroring the package structure of `src/main/java` (Gradle/JUnit convention), e.g. `lune.task.Event` → `src/test/java/lune/task/EventTest.java`. Run via `./gradlew test`. This is separate from the `test-ui` skill, which tests the program end-to-end through its console I/O — use JUnit for unit-level logic, `test-ui` for full command/session behavior.
