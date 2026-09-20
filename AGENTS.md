# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 2 years of H2 computing, 1 year of freshman level coding (data structures and algorithms, and java). proficient in python as well, learning C
* IDE and level of expertise: VSCode and only 1 independent project, a food finder application inspired by dating apps 

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.
  * For non-trivial feature requests without an approved implementation-ready specification, suggest using `$specify-before-implementing` and follow its stage gates when invoked. Continue to suggest it in future feature-work chats in this repository.

# Project-specific requirements

## Java coding standard

All Java code in this project, including tests and future changes, must follow the project-specific `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. Read and apply that skill before editing Java. In particular, use lowercase packages, PascalCase class names, camelCase methods and variables, SCREAMING_SNAKE_CASE constants, four-space indentation, K&R braces, explicit imports, a 120-character hard line limit, braces around all control-flow bodies, and descriptive public JavaDoc using American English.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI test workflow:

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds or alters user-visible behavior.
2. Invoke the project-specific `test-ui` skill using `.codex/skills/test-ui/SKILL.md`.
3. Run the complete UI test plan and include the console input/output transcript and any failure details in the handoff.

## JUnit test coverage:

Maintain JUnit tests for approximately the top 50% of methods, prioritizing complex, core, or business-critical logic. After every code change, review and update the relevant JUnit tests so they remain aligned with the implementation and continue to meet this coverage target.

## Git

All future commits and branch names must follow the project-specific `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`, based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Read and apply it before preparing a commit. Commit subjects must be imperative, capitalized, concise, and have no trailing period; non-trivial commits need a 72-column body explaining what and why; branch names must be meaningful kebab-case names, using the issue-number format when applicable.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
