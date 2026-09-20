# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner 
* IDE and level of expertise: IntelliJ and beginner

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

## Testing
IMPORTANT: CHECK FOR CODE UPDATES THEN FOLLOW THE INSTRUCTIONS BELOW IF THERE ARE ANY CODE UPDATES:
After each code update, review `test/ui-test-plan.md` and update it if the change affects console UI behavior, commands, expected output, or relevant edge cases.
After each code update, update JUnit tests so they continue to cover the top ~50% highest-value methods, prioritizing complex, core, or critical business logic over simple getters and trivial formatting.
After each code update, invoke the project-specific `test-ui` skill to run the UI test plan. If the tests cannot be run because the local environment is missing Java 25 or another required tool, report that clearly.
When adding tests, add more test cases to cover edge cases and incorrect inputs. Interleave positive and negative test cases to detect incorrect inputs affecting the correctness of the internal states.

## Java coding standard

Use the project-specific `seedu-java-coding-standard` skill for all Java source and test code changes.
All Java code in this project must follow the SE-EDU Java coding standard basic and intermediate rules.

## Git

Use the project-specific `seedu-git-standard` skill for all commit messages, merge commit messages, branch names, tags, and push workflows.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
