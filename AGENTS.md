# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner; comfortable with basic Java and OOP concepts.
* IDE and level of expertise: IntelliJ IDEA; basic proficiency, with the project already imported and configured.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Product identity

The chatbot is named Beemo. Use `Beemo` consistently in class names, source file names, banners, greetings, documentation, and other user-facing text. Remove references to the original placeholder name when implementing the rename.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Required UI testing after code updates

After every source-code update:

1. Review `test/ui-test-plan.md` and update it when the changed behavior is not covered or when expected console output has changed.
2. Invoke the project-specific `test-ui` skill, even when the test plan does not need an update.
3. Do not report the code update as complete unless the invoked test session passes. If a test fails, follow the skill's fail-fast reporting requirements and do not claim that later test cases passed.

## Required JUnit testing after code updates

Maintain JUnit tests for approximately the top 50% highest-value non-trivial public methods,
prioritizing complex, core, and critical business logic. Review and update the JUnit tests
after each code change so the test suite continues to meet this coverage target. Run the
tests using `./gradlew test`.

## Required Java coding standard

For every Java addition, edit, or review, use the project-specific
`seedu-java-coding-standard` skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`. All Java code must comply with the
SE-EDU basic and intermediate Java coding standard summarized by that skill.

## Git

For every future commit-message or branch-name proposal, and before creating any
commit or branch, use the project-specific `seedu-git-standard` skill at
`.codex/skills/seedu-git-standard/SKILL.md`. Follow the SE-EDU Git conventions
summarized by that skill, except when the course explicitly requires an exact name.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
