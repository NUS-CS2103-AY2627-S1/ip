# Project context

All Java code in this project MUST follow the project-specific `seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard/SKILL.md`, based on the SE-EDU basic and intermediate Java coding standard. Apply it when writing, reviewing, or modifying any Java source file.

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

## Git

Use lightweight tags unless the user requests an annotated tag.
All future commits and branch names MUST follow the project-specific `seedu-git-standard` skill in `.codex/skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git conventions. Apply it whenever proposing, reviewing, or creating a commit or branch name.
When proposing or creating a commit message, include enough detail to explain what changed and why.
Do not commit or push unless explicitly asked.

## UI regression testing after code changes

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds, removes, or changes observable command-line behavior.
2. Invoke the project-specific `test-ui` skill and run the resulting UI test plan. Use the project's current Java build/run command, for example:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py \
     --command 'java -cp <compiled-classes> Monkey' \
     --plan test/ui-test-plan.md
   ```

   Report the console input/output session and stop at the first failed test. If the change has no UI impact, still invoke `test-ui`; document why the existing plan remains sufficient.

## JUnit test coverage target

Maintain JUnit coverage for at least the top 50% of methods that are highest-value to the project, prioritizing complex, core, state-mutating, or otherwise critical business logic. After every code change, review the affected methods and update or add JUnit tests as needed to keep this target satisfied. Tests should cover the reasonable success, boundary, and failure cases for the selected methods.
