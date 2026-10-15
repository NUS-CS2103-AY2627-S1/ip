# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: able to understand basic java
* IDE and level of expertise: IntelliJ IDEA, new to this IDE, but able to use VS code

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this repository (both `src/main` and `src/test`) MUST follow the `seedu-java-coding-standard` skill (`.claude/skills/seedu-java-coding-standard/SKILL.md`), which condenses the se-education basic + intermediate Java conventions.

* Consult that skill before writing or editing any `.java` file.
* Re-check the changed code against the skill's self-review checklist before proposing a commit.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All commits and branches in this repository MUST follow the `seedu-git-standard` skill (`.claude/skills/seedu-git-standard/SKILL.md`), which condenses the se-education Git conventions (commit-message form, body content, branch naming).

* Consult that skill before writing any commit message or creating a branch, and check the message against its pre-commit checklist.
* Use lightweight tags unless the user requests an annotated tag.
* When proposing or creating a commit message, include enough detail to explain the rationale for the change.
* Do not commit or push unless explicitly asked.

## Testing

* Tests use JUnit 5 (Jupiter) and live under `src/test/java`, mirroring the package of the class under test (e.g. `pan.Parser` -> `src/test/java/pan/ParserTest.java`).
* Run them with `./gradlew test` (they also run as part of `./gradlew build`). The HTML report is written to `build/reports/tests/test/index.html`.
* Coverage target: JUnit tests should cover roughly the top 50% highest-value methods -- the complex, core, or business-critical logic such as parsing, validation, and calculations. Trivial getters/setters and one-line wrappers over library calls do not need tests.
* Keep tests in sync with the code: whenever a method in that top-50% tier is added, changed, or removed, add or update its JUnit tests in the same change so the coverage target continues to be met.
