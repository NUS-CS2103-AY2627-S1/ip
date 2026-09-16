# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Around 1 year of experience
* IDE and level of expertise: Around 1 year of experience

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

Apply the project-specific `seedu-java-coding-standard` skill to every Java code change in
`src/main/java` and `src/test/java`. All production code and tests must follow the SE-EDU
basic and intermediate Java coding conventions documented in
`.codex/skills/seedu-java-coding-standard/SKILL.md`, using Google Java Style for topics that
the SE-EDU standard does not cover. Review changed Java files for naming, package/import
order, four-space indentation, 120-character line length, K&R braces, explicit braces on
control-flow bodies, variable scope, and descriptive Javadocs before completing the change.
## JUnit coverage target

Maintain JUnit tests for at least the highest-value 50% of methods, prioritizing
complex, core, or critical business logic. After every code change, update the
relevant JUnit tests so they remain aligned with the changed behavior and the
test suite continues to satisfy this coverage target.

## Post-code-update verification

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change affects user-visible behavior, commands, inputs, or expected outputs. Keep each affected test case's aim, inputs, human-readable expected output, and `test-ui` JSON data consistent.
2. Invoke the project-specific `test-ui` skill, even when the test plan does not need a change. Run it from the repository root with `python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md`.

If a UI test fails, stop the verification session immediately and report the failing command plus the actual and expected output before making further changes.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Apply the project-specific `seedu-git-standard` skill whenever a commit is requested,
proposed, reviewed, or prepared. All future commits must follow the SE-EDU Git conventions
documented in `.codex/skills/seedu-git-standard/SKILL.md`: use an imperative, capitalized
subject without a period, keep it preferably within 50 characters and never over 72
characters, and include a blank-line-separated body for non-trivial commits. Wrap body lines
at 72 characters and explain what changed and why. Use meaningful kebab-case branch names.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
