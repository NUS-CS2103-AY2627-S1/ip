# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner to Intermediate level
* IDE and level of expertise: IntelliJ IDEA, Beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every task that creates, modifies, reviews, or refactors Java code, load and follow the project-specific `$seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. All Java code in this repository must comply with that skill.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods. Prioritize methods that contain complex, core, or critical business logic rather than adding low-value tests solely to increase the tested method count.

After each code change, review and update the relevant JUnit tests as needed to continue meeting this target. Run the full Gradle test suite before reporting the code change as complete.


## Console UI testing

After each code update, review `test/ui-test-plan.md` and update it whenever the console UI's commands, output, error handling, or state behavior changes. Then invoke the `$test-ui` skill to run the plan. Do this before reporting the code update as complete; if a UI test fails, stop and report the failure rather than continuing with later cases.

## Git

For every task that proposes, creates, or amends a commit or names a branch,
load and follow the project-specific `$seedu-git-standard` skill at
`.codex/skills/seedu-git-standard/SKILL.md`. Every future commit in this
repository must comply with that skill.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
