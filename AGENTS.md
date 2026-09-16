# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 1
* IDE and level of expertise: 1

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Git standard

All future commits and branch names in this project MUST follow the project
skill `.codex/skills/seedu-git-standard/SKILL.md`, which is based on the
SE-EDU Git conventions. This includes imperative, capitalized, period-free
commit subjects; subject length limits; 72-character-wrapped WHAT/WHY bodies
for non-trivial commits; meaningful kebab-case branch names; and reviewing the
staged diff before committing. Do not commit or push unless explicitly asked.

## Java coding standard

All Java code in this project MUST follow the project skill
`.codex/skills/seedu-java-coding-standard/SKILL.md`, which is based on the
SE-EDU Java coding standard (basic + intermediate rules). This includes new
code, edits to existing code, tests, package declarations, naming, layout,
imports, control flow, variable visibility, and required Javadoc comments.
Review the skill after every Java change and correct relevant violations before
reporting the work complete.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit coverage:

Maintain JUnit tests for approximately 50% of the codebase's highest-value methods,
prioritizing complex, core, or critical business logic. After every code change,
review and update the relevant JUnit tests so that they continue to meet this
coverage target. Run the JUnit suite before reporting the implementation complete;
if a test fails, report the failure rather than claiming the change is verified.

## UI testing

After every code update, review `test/ui-test-plan.md` and update it when the
changed behavior needs new or revised UI coverage. Then invoke the project
`test-ui` skill to run the plan before reporting the implementation complete.
If a test fails, report the failure and its expected versus actual output; do
not claim that the update is verified.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
