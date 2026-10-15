# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every creation, modification, refactoring, or review of Java code in this repository, invoke and follow the project-specific `seedu-java-coding-standard` skill at `.agents/skills/seedu-java-coding-standard/SKILL.md`. All production and test Java code must comply with that skill before the work is reported complete.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI regression testing

After every update to executable application code, and before reporting the change as complete:

1. Review `test/ui-test-plan.md` against the changed behavior. Update the plan when the change adds or alters a console interaction, command path, or expected output; otherwise leave it unchanged.
2. Invoke the project-specific `test-ui` skill and run the complete UI test plan. Merely reading the skill instructions does not satisfy this requirement.
3. If a test fails, stop the test session immediately and report the actual and expected outputs. Do not report the code update as successfully completed while the UI tests are failing.

Documentation-only, agent-instruction, and other non-executable changes do not require a UI test run.

## Git

For every proposed, reviewed, or created commit and every branch name, invoke and follow the project-specific `seedu-git-standard` skill at `.agents/skills/seedu-git-standard/SKILL.md`. Before executing any commit, verify the complete commit message against that skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
