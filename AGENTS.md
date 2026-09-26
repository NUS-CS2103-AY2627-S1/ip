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

## Java coding standard

All Java code in this project must follow the repository skill at
`.agents/skills/seedu-java-coding-standard/SKILL.md`, based on the SE-EDU Java basic and intermediate
conventions. Read and apply that skill whenever Java code is created, edited, reviewed, or refactored.

## Post-code-change testing

After every code update:

1. Review `test/ui-test-plan.md` against the changed behavior.
2. Update the test plan when the change adds or modifies user-visible behavior, inputs, or expected outputs. If the existing plan still covers the behavior, leave it unchanged.
3. Invoke the project-specific `test-ui` skill at `.agents/skills/test-ui/SKILL.md` after the plan review or update. The skill must compile the project, run the applicable UI test cases, compare actual output with expected output, and show the console transcript.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All commits and proposed commit messages must follow the repository skill at
`.agents/skills/seedu-git-standard/SKILL.md`. Read and apply that skill before proposing or creating a
commit, branch, or tag.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
