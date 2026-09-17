# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner

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

All Java code in this project must follow the project-specific `$seedu-java-coding-standard` skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`. Invoke and follow that skill whenever creating, changing, or
reviewing Java code.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods, prioritizing complex, core, or critical business logic over trivial accessors and simple output methods.

After every code change, review and update the JUnit tests so that changed behavior is covered and the 50% target continues to be met. Run the complete Gradle test suite with Java 25 before reporting the change as complete.

## Console UI testing

After every code update that can affect the console UI:

1. Review and update `test/ui-test-plan.md` when the commands, inputs, expected output, or setup requirements have changed.
2. Invoke the project-specific `$test-ui` skill and follow its test-session procedure before reporting the change as complete.

The test plan is the source of truth for console UI cases. If it needs new or corrected expected output, update it before starting the test session.

## Git

All proposed or created commits, commit messages, and branch names in this project must follow the project-specific
`$seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`. Invoke and follow that skill before
proposing or creating a commit or branch.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
