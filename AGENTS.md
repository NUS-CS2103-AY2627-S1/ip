# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
  * Level of programming experience: Low
* IDE and level of expertise: Low

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

## Code-update verification

After every code update, review `test/ui-test-plan.md` and add or revise test cases when the change affects the console UI, its inputs, or its output. Then invoke the project-local `test-ui` skill (`.codex/skills/test-ui/SKILL.md`) and run all concrete cases in the plan. Follow that skill's fail-fast behavior: capture and show the console input/output, and stop at the first mismatch while reporting the expected and actual output. If the plan contains no concrete cases, note that explicitly rather than inventing expected output.

## Submission guidance

When reporting completed implementation work, always include concise submission instructions. Tailor them to the current increment's branch, merge, tag, commit, and push requirements, and remind the user that no commit or push is performed unless explicitly requested.
