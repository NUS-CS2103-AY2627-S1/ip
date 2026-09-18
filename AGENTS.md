glogg# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate beginner; comfortable with basic Java syntax, OOP, Functional Programming, and implementing standard data structures, but still developing confidence with larger programs and independent problem-solving.
* IDE and level of expertise: Has some experience using IntelliJ IDEA for CS2040S assignments, mainly editing existing project files and running code. Beginner-level familiarity with IntelliJ IDEA.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Code quality:

All new and modified code in this project, including tests and support code,
MUST follow the project skill
[`seedu-code-quality`](.codex/skills/seedu-code-quality/SKILL.md), based on the
[CS2103/T Code Quality chapter](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html#code-quality).
Load and apply that skill before editing or reviewing code. Treat its
readability, naming, safe-construct, scope, duplication, abstraction-level, and
comment rules as blocking requirements. A passing Checkstyle run is necessary
but not sufficient; review the full affected classes and resolve or explicitly
report every applicable violation before declaring the change complete.

## Java coding standard:

All new and modified Java code in this project, including test code, MUST follow
the project skill [`seedu-java-coding-standard`](.codex/skills/seedu-java-coding-standard/SKILL.md),
which is based on the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Load and apply that skill before editing Java code. Review the resulting diff for
naming, layout, statements, imports, declarations, comments, and Javadoc; use the
existing Checkstyle configuration as the automated conformance check. Do not
weaken the standard or its Checkstyle checks to conceal a violation. For topics
not covered by the skill, follow the Google Java Style Guide.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Post-code-update UI verification:

After every code update:

1. Review `test/ui-test-plan.md` and update its test cases, inputs, or expected outputs when the code changes user-visible behavior.
2. Invoke the project-specific `test-ui` skill and run all applicable UI test cases before declaring the update complete.
3. If a UI test fails, stop and report the actual and expected outputs; do not treat the code update as complete until the failure is resolved or explicitly explained.

## JUnit test coverage target:

The project has reached the stretch goal of using JUnit to test all non-trivial
public methods across all classes, while excluding trivial constructors and
accessors. After every code change, review and update the relevant JUnit tests so
that affected methods remain covered and this level of coverage is maintained.

## Git

All future commits in this project MUST follow the project skill
[`seedu-git-standard`](.codex/skills/seedu-git-standard/SKILL.md), which is based
on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Load and apply that skill before drafting, reviewing, amending, or creating a
commit. Enforce its subject-line, commit-body, and branch-name rules, and inspect
the staged diff to keep unrelated changes out of each commit. Install the local
commit-message hook with `git config core.hooksPath .githooks` and treat hook
failures as blocking. Do not commit or push unless explicitly asked.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
