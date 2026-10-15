# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

All Java code in this project MUST follow `.codex/skills/seedu-java-coding-standard/SKILL.md`, which is based on the SE-EDU basic and intermediate Java coding standard. Apply it to production code and tests when creating, modifying, or reviewing code.

All future commits in this project MUST follow `.codex/skills/seedu-git-standard/SKILL.md`, which is based on the SE-EDU Git conventions. Apply it when naming branches and when preparing, reviewing, or proposing commit messages.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 1 year of Java experience, 1 year of TypeScript/JavaScript experience, 1-2 months of software app dev experience, 3+ years of python experience
* IDE and level of expertise: know about basic features, have not explored any AI features yet

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

## After code changes

After every code update:

1. Review the change against `test/ui-test-plan.md` and update the plan when the intended console behavior, inputs, or outputs changed.
2. Invoke the project-specific `$test-ui` skill to run the UI test plan. The skill must print the console input/output transcript and stop at the first failure.
3. Update `docs/README.md` with any user-visible behavior or usage changes.

## JUnit testing target

Maintain JUnit coverage for approximately the highest-value 50% of methods in each class under test, prioritizing complex, core, or critical business logic. Update the relevant JUnit tests after every code change so the test suite continues to meet this target and covers new or changed behavior.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

# Additional Comments
