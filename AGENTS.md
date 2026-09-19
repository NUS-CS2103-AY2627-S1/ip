# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

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

## Required Java coding standard

For every Java code change or review, invoke the project-specific
`seedu-java-coding-standard` skill and follow its SE-EDU basic and intermediate
rules. Apply the standard to new code and update touched existing code where
necessary.

## Required JUnit coverage workflow

After every Java code change:

1. Review and update the JUnit tests in `src/test/java`.
2. Maintain tests for approximately the top 50% highest-value methods, prioritizing complex, core, and critical business logic.
3. Run `gradlew test` and resolve all failures before handing the change back.

Test classes must mirror the package and name of the production class they test. Test methods may use the `featureUnderTest_testScenario_expectedBehavior` naming convention when descriptive names would otherwise be too long.

## Git

Before proposing or creating any branch, commit, merge commit, or tag, invoke the
project-specific `seedu-git-standard` skill and follow its conventions. Explicit
names or messages supplied by the user take precedence.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Required UI regression workflow

After every code update:

1. Review `test/ui-test-plan.md` and update it when the intended behavior changes or a relevant test case is missing. Derive expected output from the requirement, not from failing program output.
2. Invoke the project-specific `test-ui` skill in `.agents/skills/test-ui/SKILL.md` before handing the change back to the user.

If a UI test fails, stop at the first failure and report its console transcript, actual output, and expected output.

## Required Java coding standard

Before creating, changing, or reviewing Java code, read and follow `.agents/skills/seedu-java-coding-standard/SKILL.md`. Apply it to all Java code in this project.

## Required Git standard

Before proposing or creating a commit message or branch, read and follow `.agents/skills/seedu-git-standard/SKILL.md`. Apply it to all future commits and branch names in this project.
