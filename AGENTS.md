# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: low - intermediate
* IDE and level of expertise: NVim, 1 year of experience

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

Follow the SE-EDU Java coding standard (basic + intermediate) for all Java code in this project, including tests and any code reviewed or suggested.
The rules are captured in the `seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard/`; invoke it before writing or reviewing Java code.

## Git

Follow the SE-EDU Git conventions for every commit message and branch name, as captured in the `seedu-git-standard` skill in `.codex/skills/seedu-git-standard/`.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Keep commit subjects within 50 characters when practical and never exceed 72 characters.
Wrap commit message body lines at 72 characters.
Do not commit or push unless explicitly asked.

## JUnit testing

* Write JUnit 5 tests in `src/test/java/`, mirroring the package structure of `src/main/java/`.
* Test coverage target: the top ~50% of highest-value methods, prioritizing complex, core, or critical business logic (e.g. parsing, task operations, and storage round-tripping) over trivial getters and wiring.
* Update the JUnit tests after each code change so the coverage target continues to hold.
* Run the full suite with `./gradlew test`; all tests must pass before work is considered complete.

## UI regression testing

After each code update:

* Update `test/ui-test-plan.md` if commands, expected output, or relevant test coverage changed.
* Invoke the project-specific `test-ui` skill in `.codex/skills/test-ui/`.
* Stop at the first failed UI test and report the command together with the expected and actual output.
