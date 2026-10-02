# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: High Beginner
* IDE and level of expertise: High Beginner

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

## Test coverage

Set a minimum test coverage target of 50% for the repository. After every code change, update or add JUnit tests as needed so the codebase meets or exceeds this coverage target; JUnit tests must be kept in sync with code changes to comply with this requirement. Continuous integration may enforce the 50% threshold.

## Coding standard

All Java code in this repository MUST follow the SE-EDU intermediate Java coding conventions. The project ships a local skill to document and (in future) validate this requirement: .copilot/skills/seedu-java-coding-standard/SKILL.md

Key mandates:
- Public classes and nontrivial public methods must include Javadoc comments.
- Follow naming conventions, ordering, spacing, and other rules from the reference: https://se-education.org/guides/conventions/java/intermediate.html
- Agents and contributors should reference the skill when reviewing or generating code for this repository.

## Git conventions (MANDATORY for all future commits)

All commits intended for this repository MUST follow the SE-EDU Git conventions: .copilot/skills/seedu-git-standard/SKILL.md

Key mandates:
- Commit subject should be imperative, concise (<=50 chars) and summarise what changed.
- Use an optional body to explain why the change was made and any important context; wrap at 72 characters per line.
- Include trailers for metadata (e.g., Co-authored-by) only when required by policy.
- Prefer lightweight tags for releases; do not push to shared remotes without explicit permission.

Agents acting on behalf of contributors must format commit messages according to these rules and should not push changes without an explicit user instruction.

