# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: A little
* IDE and level of expertise: A little

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java code creation, edit, review, or refactoring task, invoke and follow the project-specific `seedu-java-coding-standard` skill in `.codex/skills/seedu-java-coding-standard/SKILL.md`. All production and test Java code must comply with that skill.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Before proposing or creating any commit message, invoke and follow the project-specific `seedu-git-standard` skill in `.codex/skills/seedu-git-standard/SKILL.md`. Every future commit in this project must comply with that skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Testing after code updates

Maintain JUnit coverage for roughly the top 50% of highest-value methods. Prioritize core business logic, complex branching, parsing, persistence, and other behavior where a regression would affect users or their data. Update the relevant JUnit tests after every code change so this target continues to be met.

After each code update, check whether `test/ui-test-plan.md` needs to be updated to cover the changed behavior. If the code change affects console input/output, commands, error messages, or chatbot behavior, update the test plan before testing.

After each code update, invoke the project-specific `test-ui` skill and run the UI tests using:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Report the console input and output transcript from the test run. If a UI test fails, stop the test session immediately and report the failed test case, actual output, and expected output.
