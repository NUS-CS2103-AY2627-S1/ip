# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: CS2030S, CS2040S (Intermediate Java)
* IDE and level of expertise: VS Code, IntelliJ IDEA (Beginner)

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard:

All Java code in this repository must follow the project-specific
`.codex/skills/seedu-java-coding-standard/SKILL.md`, based on the SE-EDU basic
and intermediate Java coding standard. Apply it to production code and tests,
including naming, imports, formatting, braces, line length, and public Javadoc.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing

After every code update, review `test/ui-test-plan.md` and update it whenever
the user-facing console behavior, inputs, or expected output has changed. Then
invoke the project-local `test-ui` skill by running:

```sh
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Do not continue to later UI test cases after a failure. Report the runner's
actual and expected output, and only update an expected output when the change
is intentional and matches the requested behavior.

## Git

All future commits and branch names must follow
`.codex/skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git
conventions. Commit subjects must be imperative, capitalized, without a final
period, and preferably 50 characters or fewer (maximum 72). Non-trivial
commits must include a blank-line-separated body wrapped at 72 characters that
explains what changed and why. Branch names must use meaningful kebab-case.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
