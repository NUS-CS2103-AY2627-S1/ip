# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium (done CS1101S + CS2030S + CS2040S in NUS)
* IDE and level of expertise: intelliJ

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
Do not commit or push unless explicitly asked.

All commit messages and branch names — including ones only being proposed for the user's review, not yet committed — must follow the SE-EDU git conventions (https://se-education.org/guides/conventions/git.html), captured in full in the `seedu-git-standard` skill (`.claude/skills/seedu-git-standard/SKILL.md`). Load that skill whenever drafting a commit message or branch name. Key points worth internalizing: imperative-mood subject line, capitalized, no trailing period, aim for 50 chars/never exceed 72; a body (blank line separated, wrapped at 72 chars) explaining *what* and *why* is required for any non-trivial commit; branch names are kebab-case.

## Testing

Target JUnit coverage of roughly the top 50% highest-value methods in the codebase — prioritize complex, core, or critical business logic (parsing, state mutation, persistence, command dispatch) over trivial getters/setters, pure `println` wrappers, or one-line delegations. This is a coverage target, not a percentage to hit exhaustively; use judgment on which methods are "high-value" as the codebase grows.

After any code change (new feature, bug fix, refactor), review whether it adds, changes, or removes high-value logic, and update JUnit tests accordingly so the codebase keeps meeting this target — new complex logic should get new tests, and changed behavior should get its existing tests updated rather than left stale or silently broken.

## Coding standard

All Java code in this project — new code being written and existing code being edited — must follow the SE-EDU intermediate Java coding standard (https://se-education.org/guides/conventions/java/intermediate.html), captured in full in the `seedu-java-coding-standard` skill (`.claude/skills/seedu-java-coding-standard/SKILL.md`). Load that skill whenever writing or reviewing Java source in this repository. Key points worth internalizing rather than just looking up each time: K&R brace style (`} else {`, `} catch (...) {` — never on separate lines), no public fields on classes with real behavior, boolean-returning methods named `isX`/`hasX` rather than `getX`, no wildcard imports, and header comments on public classes/methods (except getters/setters, overridden methods, and test methods/classes — their names already say enough).
