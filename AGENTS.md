# Project context

This repository is a starter template for a greenfield Java project used in an introductory
software engineering course in an undergraduate computer science program. Students use it as
the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in
this repository.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: moderate
* IDE and level of expertise: IntelliJ, low

# Guidance for interacting with users

* Briefly explain the rationale for significant actions: what you did and why.
* Interaction examples:
  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when
    their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where
    they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the
    requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding standards

Before making any code change, read and follow both `docs/java-coding-standard.md` and
`docs/git-conventions.md`. The Java standard governs source-code style; the Git standard
governs branch names and commit messages whenever Git history is changed.

## Assertions

When modifying Java code, add or maintain Java `assert` statements for meaningful
programmer-controlled invariants, preconditions, postconditions, or control-flow assumptions.
Do not use assertions for user input, file data, or required work; handle those with validation
and exceptions instead.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, first read and follow
`docs/git-conventions.md`, even if no code change is being made. For every
non-trivial change, provide both a subject and a body. The body must explain
the current situation, why it should change, the imperative change being made,
why that approach was chosen, and any relevant context. Do not provide a
subject-only message unless the change is clearly trivial.
Do not commit or push unless explicitly asked.

## UI testing

After every source-code update, before returning the completed work to the user:

1. Review `test/ui-test-plan.md` against all behavior implemented so far.
2. Add or update cases when behavior, commands, errors, or state transitions change. Include
   both successful and invalid inputs, and verify that rejected inputs do not corrupt task state.
3. Invoke the project `test-ui` skill at `.cursor/skills/test-ui/SKILL.md` and run the entire UI test plan.
4. Do not report the code update as complete unless all UI tests pass. If a test fails, report the
   actual and expected output and fix the problem before rerunning.
5. Report the final test-session result, including the console input and output, to the user.

## JUnit testing

Maintain JUnit coverage for the top 50% highest-value methods, prioritizing complex,
core, and critical business logic. Update the relevant JUnit tests after each code
change so this target continues to be met. Place tests in `src/test/java` using the
same package and a `ClassUnderTestTest` name, and include reasonable successful,
boundary, and invalid-input cases.
