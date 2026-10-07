# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate 
* IDE and level of expertise: IntelliJ, intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Code quality

For every future source code change in this project, use the project-specific `seedu-code-quality`
skill at `skills/seedu-code-quality/SKILL.md`. For Java changes, also apply the
`seedu-java-coding-standard` skill at `skills/seedu-java-coding-standard/SKILL.md`.

## Git standard:

For every future commit or branch operation in this project, use the project-specific
`seedu-git-standard` skill at `skills/seedu-git-standard/SKILL.md`. Commit messages and branch
names must follow its SE-EDU conventions; do not commit or push without explicit user approval.

## Java version

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing after code changes

After every code update, review `test/ui-test-plan.md` and update it whenever the
changed behavior requires new or revised UI coverage. Then invoke the `test-ui`
skill by running `python3 skills/test-ui/scripts/run_ui_tests.py`. Do not report
the code update as complete until the UI tests pass, or clearly state the failing
case and why it is unresolved.

Run the tests in a temporary working directory when they create or read runtime
data, so they do not modify a user's `data/drax.txt` save file.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
