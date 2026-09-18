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

* The chatbot's personality and response wording are determined by the user. Do not treat friendly phrasing differences as requirement mismatches unless the user explicitly specifies the wording.

# Project-specific requirements

## Java coding conventions:

Follow the [SE-EDUCATION intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
for Java naming, layout, statements, imports, comments, and related conventions. Use the Google Java Style Guide for topics not covered by the standard.

The project-specific `$seedu-java-coding-standard` skill in
`.codex/skills/seedu-java-coding-standard` is the working checklist for these
rules. Apply it to all Java source and test code in this repository.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

Follow the [SE-Education Git conventions](https://se-education.org/guides/conventions/git.html)
for all future branch names and commits. Use the project-specific
`$seedu-git-standard` skill in `.codex/skills/seedu-git-standard` when naming a
branch or drafting a commit message.

## Console UI testing

After every Java source-code update:

1. Review `test/ui-test-plan.md` and add or revise test cases when the change affects observable console behavior or introduces a feature that is not yet covered.
2. Invoke the project-local `test-ui` skill to run the complete plan:

   ```text
   Use $test-ui to run the console UI tests in test/ui-test-plan.md.
   ```

Do not consider the code update complete until the skill has been invoked and the test plan passes. If a test fails, stop immediately and report the test case together with its actual and expected output. Preserve the console session transcript in the response.

## JUnit testing

Maintain JUnit tests for at least the highest-value 50% of non-trivial public methods,
prioritising core parsing, task state, persistence, and date/time behavior. Update the
relevant JUnit tests after every Java source-code change so that this coverage target
continues to be met.
