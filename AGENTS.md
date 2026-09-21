# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [Intermediate]
* IDE and level of expertise: [Intermediate]

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

## UI testing after code updates

After every code update, review `test/ui-test-plan.md` and add or revise test cases when the change affects command-line UI behavior. 
Then invoke the project-specific `test-ui` skill to run the plan. If the plan has no applicable cases, record the reason in the plan 
or report the limitation clearly; do not silently skip the skill invocation.

## JUnit coverage after code updates

Maintain JUnit tests for approximately the highest-value 50% of methods, prioritizing
complex, core, and business-critical logic. After every code change, update or add
JUnit tests when the changed behavior affects those methods, and run the JUnit suite
to verify that the coverage target and existing behavior remain supported.

## SE-EDU Java coding standard

All Java code in this project must follow the project skill
`seedu-java-coding-standard`, based on the SE-EDU basic and intermediate Java coding
conventions. Apply it when creating, reviewing, or refactoring code, including
naming, layout, imports, control-flow braces, line length, encapsulation, and
Javadoc requirements. Review and correct relevant standard violations in files
affected by each code change, and run the project's tests afterward.

## SE-EDU Git standard

All future commits and branch names must follow the project skill
`seedu-git-standard`, based on the SE-EDU Git conventions. Commit subjects must be
meaningful, imperative, capitalized, free of a trailing period, and no longer than
72 characters, with a preference for 50 characters or fewer. Non-trivial commits
must have a blank-line-separated body wrapped at 72 characters that explains what
changed and why. Branch names must be meaningful and use kebab case, with an issue
number prefix when applicable. Review every commit message against these rules
before committing.
