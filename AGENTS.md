# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [Moderate]
* IDE and level of expertise: nvim, basic knowledge

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

## Java coding standard

For every Java code change or review, invoke and follow the project-specific
`seedu-java-coding-standard` skill at
`.agents/skills/seedu-java-coding-standard/SKILL.md`. All Java code in this
project, including tests, must comply with that skill before the work is
considered complete.

## UI regression testing

After every update to application code:

1. Review `test/ui-test-plan.md` and update its test cases when the changed behavior is not already covered or an existing expected output has intentionally changed.
2. Invoke the project-specific `test-ui` skill to run the complete UI test plan.
3. Stop at the first failing test and report its console input, actual output, and expected output. Do not alter expected output merely to make the implementation pass.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods in the codebase. Prioritize complex methods, core behavior, and critical business logic over trivial getters, setters, and one-line delegations.

After every code change, review and update the JUnit tests as needed to keep the test suite compliant with this 50% method-coverage target. Run the complete JUnit suite with `./gradlew test` and resolve any failures before considering the change complete.

## Git

For every branch name or commit message that you propose, edit, or create,
invoke and follow the project-specific `seedu-git-standard` skill at
`.agents/skills/seedu-git-standard/SKILL.md`. Every future commit made for this
project must comply with that skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
