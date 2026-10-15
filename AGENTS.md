# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ, Fairly Comfortable

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

Before every commit or branch creation, use the project-local `$seedu-git-standard` skill. Its rules are mandatory for commit subjects, commit bodies, and branch names: use imperative, capitalized, punctuation-free subjects; concise subject lengths; informative 72-column bodies explaining what and why; and meaningful kebab-case branch names while preserving any higher-priority repository prefix.

## UI testing after code changes

After every code update:

1. Update `test/ui-test-plan.md` whenever the change affects a command, console output, or another behavior covered by the UI tests. Keep each test case's aim, inputs, and expected output accurate.
2. Invoke the `$test-ui` skill and run the UI test plan. Show the resulting console input/output record. If a test fails, stop and report its expected and actual output.

## JUnit test coverage

Maintain JUnit tests for approximately the top 50% of methods by value, prioritizing complex, core, and business-critical logic over trivial accessors.

After every code change, review and update the relevant JUnit tests to keep this coverage target accurate. Run the Gradle JUnit test task and report any failures before handing off the change.

## Java coding standard

For every Java code change, use the project-local `$seedu-java-coding-standard` skill. It is mandatory and is based on the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Follow its naming, package, layout, import, brace, line-length, comment, and JavaDoc rules. Review the touched Java files against the skill before handoff.
