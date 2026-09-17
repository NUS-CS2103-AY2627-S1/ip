# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Average
* IDE and level of expertise: Average

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Mandatory UI testing after code updates

After every code update, inspect `test/ui-test-plan.md` and update it when the change adds,
removes, or alters user-visible behavior or otherwise requires new UI coverage. Then invoke
the project-specific `test-ui` skill by reading and following
`.codex/skills/test-ui/SKILL.md`, and run all applicable test cases from the plan. This is
mandatory even when the code update appears small. The skill must record the console input
and output; if a test fails, stop immediately and report the expected and actual output.

## Mandatory Git standard

All future commits in this project MUST follow the project-specific
`seedu-git-standard` skill, based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Before creating or reviewing a commit, read and apply
`.codex/skills/seedu-git-standard/SKILL.md`. This requirement applies to commit subjects,
commit bodies, and branch names. Do not commit or push unless the user explicitly authorises it.

## Mandatory Java coding standard

All Java production code and Java tests in this project MUST follow the project-specific
`seedu-java-coding-standard` skill, based on the
[SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
Before creating, editing, or reviewing Java code, read and apply
`.codex/skills/seedu-java-coding-standard/SKILL.md`. This requirement applies to every
Java code change in the repository, including delegated work and tests. For topics not
covered by the SE-EDU standard, follow the Google Java Style Guide as directed there.

## Java coding standard:

Before creating, editing, or reviewing Java code in this project, read and follow
`.codex/skills/seedu-java-coding-standard/SKILL.md`. This requirement applies to all
production Java code and Java tests.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
