# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 2000-3000 LoC
* IDE and level of expertise: IntelliJ, rarely used

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard:

Use the project-specific `seedu-java-coding-standard` skill before creating,
reviewing, refactoring, or updating Java code in this repository. All production
and test Java code must follow the SE-EDU Java coding standard basic +
intermediate rules summarized in
`.codex/skills/seedu-java-coding-standard/references/rules.md`.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use the project-specific `seedu-git-standard` skill before proposing, creating,
or editing commit messages or branch names in this repository. All future commit
messages and branch names must follow the SE-EDU Git conventions summarized in
`.codex/skills/seedu-git-standard/references/rules.md`.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Code-update UI testing

After every code change, review and update the JUnit test suite so it continues
to cover the top ~50% highest-value methods. Prioritize complex, core, or
critical business logic over trivial getters, setters, or thin wrappers. Add or
adjust tests when changed behavior affects those high-value methods, and avoid
adding low-value tests merely to increase the test count.

After every code update, review `test/ui-test-plan.md` and update it when the change introduces or alters a relevant console UI behavior, command, input, or expected output. Keep expected outputs aligned with the intended behavior; do not edit them merely to hide a regression.

After that review, invoke the project-specific `test-ui` skill to run the recorded UI test cases. Use the skill even when the test plan does not need changes, and report its console-session transcript and any failure. If the plan has no applicable test cases, record that limitation in the response rather than silently skipping the skill invocation.
