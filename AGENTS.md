# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 1000 lines of code written
* IDE and level of expertise: IntelliJ IDEA, beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

## Code update verification

After every application code update:

* Read `test/ui-test-plan.md` and update it when the change affects the UI behavior, commands, inputs, or expected output. Add or revise test cases before testing when needed.
* Invoke the `test-ui` skill to run the documented command-line UI tests after the code update.
* Do not silently rewrite expected outputs to make a failing test pass. If the test plan is incomplete or contradictory, report that before running the affected test.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Apply the project-local `$seedu-git-standard` skill to every proposed or
created commit message. Subjects should target 50 characters, must not exceed
72 characters, and must use an imperative, capitalized form without a trailing
period. Non-trivial commits must include a 72-character-wrapped body explaining
what changed and why.
Git control rule:

- Never stage, commit, amend, tag, push, reset, checkout, rebase, merge, or otherwise modify Git state automatically.
- Requests to split work into commits are planning instructions only, not authorization.
- You may inspect Git and prepare commit messages or commands.
- Before any Git write operation, stop and ask me to approve the exact files, command, and commit message.
- Leave all changes in the working tree unless I explicitly approve that specific Git operation.

## Project conventions

Apply the project-local `$seedu-java-coding-standard` skill to all Java source
code in this repository. It is based on the SE-EDU basic and intermediate Java
standard and supplements the formatter configuration below.

Apply the following references in this order:

1. These repository instructions.
2. The SE-EDU Java coding standard (all rules):
   https://se-education.org/guides/conventions/java/index.html
3. The SE-EDU Java coding standard (basic + intermediate details):
   https://se-education.org/guides/conventions/java/intermediate.html
4. The SE-EDU Markdown coding standard:
   https://se-education.org/guides/conventions/markdown.html
5. The SE-EDU Git conventions:
   https://se-education.org/guides/conventions/git.html
6. The Google developer documentation style guide:
   https://developers.google.com/style
7. The Google Java Style Guide for Java topics not covered above.

Use the references as project requirements, not as optional suggestions. Prefer
simple designs appropriate for an introductory software engineering project.
Use English and American spelling in source comments and documentation. Add
Javadoc to every class and public method unless one of the documented
exceptions applies. Keep packages, names, imports, braces, indentation,
whitespace, line length, and Markdown structure consistent with the references.
Use imperative, capitalized Git commit subjects without a trailing period.

## Codex workflow

Before changing files, inspect the relevant source, tests, documentation, build
configuration, and Git status. State assumptions when requirements are unclear.
Make the smallest coherent change that satisfies the request, preserve existing
behavior unless a behavior change is requested, and explain significant design
choices briefly for a beginner Java developer.

Use Gradle for builds and validations:

* `gradlew.bat spotlessCheck` checks formatting.
* `gradlew.bat spotlessApply` applies formatting.
* `gradlew.bat test` compiles and runs automated tests.
* `gradlew.bat check` runs the complete validation lifecycle.

Keep Java source under `src/main/java` and tests under `src/test/java`. Use the
Java 25 Gradle toolchain configured by `build.gradle`. Do not add dependencies
without explaining why they are needed.

For any change that affects the command-line interface, commands, inputs, or
expected output, update `test/ui-test-plan.md` before testing. After every
application-code update, run the documented UI tests through the `test-ui`
workflow, compare output exactly, stop at the first failure, and append the
dated session record to the plan. Never rewrite expected output merely to make
a failing test pass.

For a reusable prompt template and examples of effective task requests, see
`docs/CODEX-PROMPT-FRAMEWORK.md`.
