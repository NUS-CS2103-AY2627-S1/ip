# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner to Intermediate, around 1 year of experience
* IDE and level of expertise: Beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java production and test code in this project MUST follow the project-specific
`seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`,
which is based on the SE-EDU basic + intermediate Java coding standard. Apply it to
new code and when modifying existing code; preserve behavior while correcting style.

## Testing workflow

After every code update:

1. Update or add JUnit tests so that tests cover at least the top 50% highest-value methods, prioritizing complex, core, or critical business logic. JUnit tests must be updated after each code change to maintain compliance with this target.
2. Review `test/ui-test-plan.md` and update it when the changed behavior requires new or revised UI test cases.
3. Invoke the project-specific `test-ui` skill using `.codex/skills/test-ui/SKILL.md` and run its documented UI tests.
4. Report the test result, including the console input/output session or the expected and actual output when a test fails.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All future commits and branches MUST follow the project-specific
`seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`,
which is based on the SE-EDU Git conventions. Commit subjects must be imperative,
capitalized, concise, and without a final period; non-trivial commits must include
a 72-character-wrapped body explaining what changed and why. Branches must use
meaningful kebab-case names.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
