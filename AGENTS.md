# Project context

## Mandatory project standards

* Follow [`project-skills/seedu-java-coding-standard/SKILL.md`](project-skills/seedu-java-coding-standard/SKILL.md) for all Java code, including tests.
* Follow [`project-skills/seedu-git-standard/SKILL.md`](project-skills/seedu-git-standard/SKILL.md) for all future commit messages. Do not commit or push unless explicitly asked.

## Mandatory project standards

* Follow [`.agents/skills/seedu-java-coding-standard/SKILL.md`](.agents/skills/seedu-java-coding-standard/SKILL.md) for all Java code, including tests.
* Follow [`.agents/skills/seedu-git-standard/SKILL.md`](.agents/skills/seedu-git-standard/SKILL.md) for all future commit messages. Do not commit or push unless explicitly asked.

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate level. did orbital
* IDE and level of expertise: intermediate. used IntelliJ, cursor and VS code before, ok with the basic features of them

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

## UI testing after code changes

After every code update, review `test/ui-test-plan.md` and update it when the
change adds, removes, or alters observable console behaviour. Then invoke the
project's `test-ui` skill and run its test plan. Report the test result, including
the first expected-versus-actual mismatch if the test session fails.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Testing requirements
* When adding or modifying features, add test cases to `test/ui-test-plan.md` that interleave valid and invalid inputs, to verify that invalid inputs do not corrupt internal state (e.g., the task list).
* Before considering a task complete, run the `test-ui` skill (or manually verify) that all test cases pass.
* Periodically verify test effectiveness: introduce a deliberate bug, confirm the test plan catches it, then revert the bug.
* Maintain JUnit tests for approximately the highest-value 50% of methods, prioritizing core business logic, state transitions, parsing, and persistence.
* Update the JUnit tests after every code change so the project continues to meet the 50% high-value method coverage target.
