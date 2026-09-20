# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intellij

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

## Java coding standard

All Java code in this project must follow the SE-EDU basic and intermediate
Java coding standard. Use the reusable project skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md` for every Java change; it
is based on the authoritative
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

In particular, keep classes in lower-case packages, use the prescribed naming
and visibility conventions, use four-space indentation and K&R braces, keep
lines within 120 characters, use explicit imports, and provide descriptive
Javadoc for public classes and methods. Preserve the existing `duchess` package
structure when adding Java files.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
For every task that changes files, provide a suggested commit message for the user
to apply. Leave changes uncommitted by default; only commit when the user
explicitly asks for that task, and never push changes.

## JUnit coverage target:

Maintain JUnit coverage for roughly the top 50% of methods by value, prioritizing
complex, core, or critical business logic. After every application-code change,
review and update the relevant JUnit tests so the tests continue to reflect the
changed behavior and this coverage target remains satisfied. Run the Gradle
JUnit test task with Java 25 after updating the tests.
## Git coding standard

All future commits and branches in this project must follow the SE-EDU Git
conventions. Use the reusable project skill at
`.codex/skills/seedu-git-standard/SKILL.md` before preparing a commit or branch.

Commit subjects must be imperative, start with a capital letter, omit a final
period, and stay within the 72-character hard limit. Add a separated, 72-column
wrapped body for non-trivial commits explaining what changed and why. Use
meaningful kebab-case branch names, or the issue-number format when applicable.

## After code updates

After every update to application code:

1. Review `test/ui-test-plan.md` and add or update test cases when the code
   changes user-visible behavior or introduces a relevant scenario. If the
   existing cases still cover the behavior, leave the plan unchanged.
2. Invoke the project-specific `$test-ui` skill from the repository root:
   `python3 .codex/skills/test-ui/scripts/run_ui_tests.py`.
3. Treat a failed UI test as a failed change. Stop, report the actual and
   expected output, and do not present the code update as verified until the
   failure is resolved.
