# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: intermediate 
* IDE and level of expertise: intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## UI regression workflow

After each code update, review `test/ui-test-plan.md` and update it if the change affects the console interaction being tested.
After updating the code (and test plan, if needed), invoke the `test-ui` skill to run the UI test cases and surface any transcript mismatches immediately.

## JUnit coverage workflow

Maintain JUnit coverage for approximately the top 50% of methods, prioritizing complex, core, or critical business logic over trivial accessors.
After each code change, update or add the relevant JUnit tests so the suite continues to meet this coverage target, and run `./gradlew test` to verify the tests.

## Project-specific standards

For every Java source or test change, follow the `seedu-java-coding-standard`
skill in `.codex/skills/seedu-java-coding-standard/SKILL.md`. Review naming,
layout, imports, braces, line length, encapsulation, and required Javadocs before
finishing the change.

For every future commit or branch, follow the `seedu-git-standard` skill in
`.codex/skills/seedu-git-standard/SKILL.md`. Use meaningful kebab-case branch
names and imperative commit subjects; include a concise, wrapped body explaining
what changed and why for non-trivial commits.
