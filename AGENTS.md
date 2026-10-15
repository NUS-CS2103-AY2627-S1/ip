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

All Java in this repository (both `src/main/java` and `src/test/java`) must follow the **SE-EDU Java coding standard (basic + intermediate level)**: <https://se-education.org/guides/conventions/java/intermediate.html>.

The standard is captured as the `seedu-java-coding-standard` skill (`.claude/skills/seedu-java-coding-standard/`). **Consult that skill before writing or modifying any `.java` file**, and make sure the change complies before finishing: naming, 4-space indentation, line length, brace style, whitespace, import grouping/ordering (`static` → `java.*` → `org.*` → `dennis.*`, blank line between groups), statement layout, and Javadoc formatting. New code must match the existing code's style; fix violations you touch.

The standard is also enforced mechanically by **Checkstyle** (Gradle `checkstyle` plugin), using the SE-EDU config in `config/checkstyle/`. The skill is the guide for *writing* compliant code; Checkstyle is the gate that *verifies* it. Run `./gradlew checkstyleMain checkstyleTest` (also run automatically as part of `./gradlew check`) after touching any `.java` file, and make sure it passes before finishing. Do not silence a violation with a `//CHECKSTYLE.OFF` comment or a `config/checkstyle/suppressions.xml` entry unless the user approves it.

## Testing

The project is built with Gradle. Run the JUnit test suite with `./gradlew test`. `./gradlew check` runs the tests *and* Checkstyle together.

Test files follow the Gradle/JUnit convention: a test for `dennis.foo.Bar` lives at `src/test/java/dennis/foo/BarTest.java`, mirroring the class under `src/main/java`.

**Coverage target:** aim to cover roughly the top 50% highest-value methods, prioritising complex, core, or critical business logic (parsing, domain/task rules, validation). Trivial getters, console output, and glue code do not need tests.

**Keep tests in step with the code:** after every code change, update the JUnit tests in the same change so the suite still builds, still passes, and still meets the 50% target. If you add or change the behaviour of a high-value method, add or adjust its tests; if you remove one, remove its tests.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
