# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: beginner/intermediate
* IDE and level of expertise: good

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Standards

Follow the project skills `seedu-java-coding-standard` and `seedu-git-standard` for all future Java code and Git commits.

## Chatbot personality:

Murphy should have a distinctive, friendly, and witty personality. Future chatbot text should follow these guidelines:

* Use relevant, contextual smart jokes when they fit naturally, especially around commands, programming, and conversation.
* Keep humour age-appropriate, welcoming, and easy to understand.
* Be helpful and clear first; never let a joke obscure an instruction, result, error message, or required output.
* Avoid repetitive catchphrases, sarcasm directed at the user, insensitive humour, and jokes that feel forced or unrelated.
* Preserve the required output format and exact command behaviour even when adding personality.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Post-update checks:

For changes that add, remove, or alter observable command-line behavior:

1. Review and update `test/ui-test-plan.md`. Keep each affected test case's aim, inputs, and expected output current.
2. Invoke the project-local `test-ui` skill using the test cases in `test/ui-test-plan.md`. Stop at the first failed test and report the complete actual and expected outputs.

For any implementation change that should be reviewed visually, invoke the project-local `present-changes-visually` skill. The requested `test-changes-visually` skill refers to this existing skill.

Do not invoke `test-ui` for documentation-only changes, comments, formatting, or refactors whose observable behavior is unchanged unless the user explicitly requests testing. Use judgment for other changes and explain when a test run is unnecessary.

## JUnit coverage target

Use JUnit to cover the highest-value approximately 50% of non-trivial public methods, prioritizing core business logic and complex behavior. Update the relevant JUnit tests after every code change so that this coverage target remains current.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
Do not commit compiled `.class` files or other generated, temporary, IDE-specific, or machine-specific files that should not be revision-controlled. Check `.gitignore` before staging changes.
