# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: done small personal projects before
* IDE and level of expertise: basic vs code and intelliJ for hackathons and projects

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

Before writing, modifying, or reviewing Java code in this project, read and follow
`skills/seedu-java-coding-standard/SKILL.md`. Apply its SE-EDU intermediate Java
coding-standard rules to all production code and tests changed in the task.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## After code updates

After every code update, complete these steps before reporting the work as finished:

1. Update the JUnit tests after each code change. Maintain tests for at least the top 50% highest-value methods, prioritizing complex, core, and critical business logic over simple accessors.
2. Review `test/ui-test-plan.md` and update it when the changed behavior needs a new test case, changed input, or changed expected output. If the update does not affect the tested UI behavior, leave the plan unchanged but confirm that it was reviewed.
3. Invoke the project-local `test-ui` skill by following `skills/test-ui/SKILL.md` and running its test runner from the project root:

   ```sh
   python3 skills/test-ui/scripts/run_ui_tests.py
   ```

The runner must print the console input/output transcript. If a test fails, stop immediately, report the actual and expected output, and do not continue with later test cases.
