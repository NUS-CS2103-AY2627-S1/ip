# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: IntelliJ IDEA, beginner

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

## Required Java coding standard

For every Java code creation, modification, or review, invoke the project-specific
`seedu-java-coding-standard` skill and follow it for all Java code in scope. Do not report a Java
change as complete until the changed code has been checked against that skill.

## Required UI regression workflow

After completing any code update, before reporting the work as complete:

1. Review `test/ui-test-plan.md` against the code change. Update the plan when the change affects commands, console output, build or launch steps, setup, or other behavior covered by UI acceptance tests. Leave it unchanged when the change does not affect the plan, and state that decision.
2. Invoke the project-specific `test-ui` skill and run the applicable cases in the plan.
3. If a case fails, do not report the code update as complete. Resolve the implementation or expected-output mismatch, then rerun the applicable cases.
4. Report which cases ran and their pass or fail status. Never claim that an unexecuted case passed.

## Git

Before proposing, reviewing, or creating any commit message or branch name, invoke the
project-specific `seedu-git-standard` skill and follow it. Every commit created for this project
must comply with that skill, including its subject and any required body.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
