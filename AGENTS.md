# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basic to Intermediate, typical year 2 computer science student without programming experience pre-college.
* IDE and level of expertise: Basic familiarity with IDEs like IntelliJ, PyCharm, etc.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## JUnit coverage target

Prioritize JUnit tests for approximately the top 50% of the codebase's
highest-value methods, especially complex, core, or business-critical logic.
When production code changes, update the relevant JUnit tests in the same
change whenever the behavior or coverage is affected. This keeps the tests
aligned with the implementation and maintains the 50% high-value coverage
target.

## After every code update:

1. Review whether the change affects the console UI, and update `test/ui-test-plan.md` with or revise the relevant test case when needed.
2. Invoke the project-specific `test-ui` skill by running:

   ```bash
   python3 .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

3. If a UI test fails, stop and report the failing test, expected output, and actual output before making further changes. After fixing the code, rerun the skill.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard:

All Java source and test code in this project must follow the project-specific `seedu-java-coding-standard` skill, based on the [SE-EDUCATION Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Load and apply that skill for every Java code change.

## Git

All future commits and branch names must follow the project-specific `seedu-git-standard` skill, based on the [SE-EDUCATION Git conventions](https://se-education.org/guides/conventions/git.html). Load and apply that skill before proposing or creating commits.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
