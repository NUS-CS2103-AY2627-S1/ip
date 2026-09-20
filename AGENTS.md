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

## Skill usage

Do not use or invoke any skill whose name starts with `superpowers:` for work in this repository. This prohibition applies to every chat and task in the project, including situations where a `superpowers:` skill would otherwise be automatically selected. Use the applicable project instructions and ordinary tools instead.

## Java coding standard

For every task that creates, edits, refactors, or reviews Java code, invoke the project-specific `$seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. All Java code in this project must follow that skill's SE-EDU Java coding standard. For topics not covered by the SE-EDU standard, follow the Google Java Style Guide as directed by the skill.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Post-update UI testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the changed behavior or test coverage requires it. Every test case must state its aim, inputs, and exact expected output.
2. Invoke the project-specific `$test-ui` skill at `.codex/skills/test-ui/SKILL.md` and run the full UI test plan.
3. Stop at the first failure and report the recorded console input, actual output, and expected output.

## JUnit testing and coverage

The current JUnit suite has **82.3% line coverage (525 of 638 executable lines)**, measured on 2026-09-15 with JaCoCo by running `./gradlew clean test jacocoTestReport` on Java 25. The HTML report is generated at `build/reports/jacoco/test/html/index.html`.

After every code change, review the affected behavior and update or add JUnit tests in `src/test/java` whenever relevant. Run the full JUnit suite and regenerate the coverage report before considering the change complete. When production code or tests change, remeasure coverage and keep the baseline in this section current.

## Git

Before proposing, creating, amending, or squashing any commit, invoke and follow
the project-specific `$seedu-git-standard` skill at
`.codex/skills/seedu-git-standard/SKILL.md`. Its commit-message and branch-name
rules are mandatory for all future commits in this repository.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
