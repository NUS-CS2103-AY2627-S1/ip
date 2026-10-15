# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ IDEA (Intermediate)

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Pre-code skill

Before starting any task involving code, read and apply `.codex/skills/keep-code-simple/SKILL.md`.

## Java coding standard

Before creating, reviewing, or modifying any Java source or test code, read and apply
`.codex/skills/seedu-java-coding-standard/SKILL.md`. All Java code in this project must follow
that skill.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods. Prioritize complex,
core, and critical business logic over trivial constructors, getters, and output-only wrappers.

After every code change, review the affected behavior and update or add JUnit tests as needed to
continue meeting this coverage target. Run the relevant Gradle tests before considering the change
complete.

## Post-update UI testing

After every update to application code:

1. Review `test/ui-test-plan.md` against the changed behavior. Add, remove, or update test cases when the change affects the console UI, supported commands, workflows, or expected output. Leave the plan unchanged when the code update cannot affect those behaviors.
2. Invoke the project-specific `$test-ui` skill and follow it to run the UI test plan. Treat this step as required even when the plan did not need changes.
3. If a UI test fails, stop as required by `$test-ui` and report the failure. Do not claim the code update is complete until the failure is resolved or clearly handed back to the user.

## Git

Before naming or creating a branch, or proposing, reviewing, or creating a commit, read and apply
`.codex/skills/seedu-git-standard/SKILL.md`. All branch names and commits must follow that skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
