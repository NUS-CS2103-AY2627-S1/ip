# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: intermediate learner
* IDE and level of expertise: intermediate learner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard (mandatory)

All Java code in this repository must follow the se-education.org intermediate
Java coding standard, captured in the `seedu-java-coding-standard` skill
(`.claude/skills/seedu-java-coding-standard/SKILL.md`).

* Invoke the `seedu-java-coding-standard` skill **before** writing or editing
  any file under `src/main/java` or `src/test/java`, and before reviewing Java
  code. Do not rely on memory of the rules.
* This applies to every Java change, however small, and to both production and
  test code.
* Points most often missed here: booleans must read as questions (`is`, `has`,
  `was`, `can`, `should`); imports must be explicit, grouped, and never
  wildcards; braces are required on every conditional and loop body; Javadoc
  summaries must be third person (`Returns ...`, not `Return ...`); and every
  `@param`, `@return`, and `@throws` description must end with punctuation.
* If a rule conflicts with existing code, follow the standard for the code you
  touch and flag the wider inconsistency rather than silently spreading it.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing after code changes

After every code update:

1. Update `test/ui-test-plan.md` when the change adds, changes, or removes a
   console UI behavior or requires different test coverage.
2. Invoke the `$test-ui` skill to run the current UI test plan. Include its
   console-session record in the handoff. If the plan cannot be run, state why
   and do not claim that UI testing passed.

## Git (mandatory)

All commits, branches, and tags must follow the se-education.org Git
convention, captured in the `seedu-git-standard` skill
(`.claude/skills/seedu-git-standard/SKILL.md`).

* Invoke the `seedu-git-standard` skill **before** writing any commit message,
  proposing a commit, naming a branch, or creating a tag. Do not rely on
  memory of the rules.
* This applies to every commit, however small.
* Points most often missed: the subject must be imperative, capitalised, free
  of a trailing period, and under 50 characters where possible (72 hard
  limit); the body wraps at 72 characters and follows the five-paragraph
  structure ending with any other relevant info; and the body explains what
  and why, never how.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked. Hand the message text to the
user to commit themselves.
