# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner 

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

## Java coding standard

For every task that adds, changes, or reviews Java code, invoke the project-specific
`seedu-java-coding-standard` skill and follow its SE-EDU basic and intermediate Java coding standard reference.
Before handing back a Java change, audit all added and modified Java lines against that standard. Use the Google
Java Style Guide only for topics the SE-EDU standard does not cover.

## Git

Before proposing or creating any future commit or branch, invoke the project-specific `seedu-git-standard` skill
and follow its SE-EDU Git conventions reference. Review the relevant diff before drafting a message, and verify the
subject and any required body against the skill before committing.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## UI testing after code updates

After each code update that can affect chatbot behavior:

1. Update `test/ui-test-plan.md` when the supported behavior or expected output changes.
2. Invoke the project-specific `test-ui` skill and resolve or report the first failure before handing the change back to the user.

## JUnit testing after code updates

Maintain JUnit tests for approximately the top 50% highest-value methods. Prioritize methods that contain
complex logic, implement core behavior, validate important input, or protect critical storage and parsing paths
rather than testing trivial accessors.

After every code change, review the affected classes and update or add JUnit tests as needed to continue meeting
this coverage target. Run the full JUnit suite with `./gradlew test` using Java 25 before handing the change back
to the user.
