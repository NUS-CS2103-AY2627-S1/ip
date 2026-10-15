# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: beginner
* IDE and level of expertise: beginner

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

## Post-code-update UI testing:

After every update to executable code:

1. Review `test/ui-test-plan.md` and update it when the change adds or alters user-visible behavior, or when existing coverage is no longer sufficient. Each added or changed test case must include its aim, inputs, and expected output.
2. Invoke the project-specific `$test-ui` skill from the repository root, even when the test plan did not need changes.
3. Include the skill's console input/output transcript in the handoff. If a test case fails, stop immediately and report its actual and expected output before making further code changes.

## JUnit Testing:

Maintain a test coverage target of ~50%, focusing primarily on the highest-value methods (prioritizing complex, core, or critical business logic over simple getters/setters).
After every code change, ensure that JUnit tests are updated or added to comply with this 50% target.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

All Git operations (commits, branch names) in this project must strictly follow the `seedu-git-standard` skill. Review its instructions before proposing or creating any commits.

## Coding Standard

All Java code written in this project must strictly follow the `seedu-java-coding-standard` skill. Review its instructions before generating or modifying Java files.
