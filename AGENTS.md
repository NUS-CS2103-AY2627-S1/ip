# Project context

This repository is a starter template for a greenfield Java project used in an introductory software-engineering course. The current application is a command-line Duke-style task manager named `DestroyerOfWorlds`.

Treat the repository as a student's learning project: keep changes small, readable, and aligned with the existing design unless the task explicitly calls for a wider refactor.

# Repository layout

* `src/main/java/` contains production Java sources. Keep it as the Java source root.
* `src/main/java/data/tasks.txt` is the application's persisted task data. Do not overwrite it during diagnostics or tests; use a temporary copy or restore it when a test needs to alter saved data.
* `test/ui-test-plan.md` is the manual interactive UI-test specification. It is currently a template and must be completed with real commands and exact expected output before it can serve as a regression test.
* `out/`, `.idea/`, and `*.iml` are local generated IDE/build artefacts and are ignored by Git.

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

# Development workflow

1. Inspect the relevant classes and existing command/output conventions before editing. Preserve user-visible command syntax and console formatting unless the requested feature changes them.
2. Make the smallest cohesive implementation. Do not introduce frameworks, build tools, or dependencies solely to solve a small task.
3. Keep domain responsibilities clear: commands parse and coordinate work, `Task` subclasses represent task data, `TaskList` manages the collection, and `Util` contains shared formatting/input helpers.
4. Use descriptive names and straightforward control flow. Add Javadoc to new classes and to non-obvious methods or fields; explain intent and constraints rather than repeating the code.
5. Verify every changed behaviour. Prefer focused tests first, then run relevant end-to-end interactive checks. If console output changes, update or add the corresponding exact-output case in `test/ui-test-plan.md`.

# Validation and console UI

* Run application/build tasks with Java 25. Check the active Java version before diagnosing compiler failures caused by a version mismatch.
* This repository currently has no committed build wrapper or automated test suite. Do not claim a Gradle/Maven command exists without first confirming it has been added.
* For interactive checks, start from a known task-data state. Exercise normal input, invalid input, empty-list cases, and persistence when the touched feature affects them.
* Preserve whitespace, prompts, line breaks, and error wording deliberately: they are part of the command-line interface and exact-output tests depend on them.
* Never leave test-created tasks or modified `data/tasks.txt` behind unless changing persisted sample data is the explicit goal.

# Change boundaries

* Preserve unrelated user changes and generated/local files. Avoid formatting or refactoring files outside the requested scope.
* Keep exceptions specific and user-facing error messages helpful; do not use broad exception swallowing to keep the program running.
* Do not commit or push unless explicitly asked. When proposing a commit, explain both the change and its rationale in the message.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Git Conventions

All git operations in this repository must follow the SE-EDU Git conventions at:

https://se-education.org/guides/conventions/git.html

This applies to all future commits, branch names, and repository hygiene. Follow the required subject/body style, use imperative commit subjects, keep messages concise and meaningful, and prefer lightweight tags unless an annotated tag is specifically requested.

https://se-education.org/guides/conventions/java/intermediate.html

This applies to all new code, modified code, and code reviews in this repository. Follow the naming, layout, statement, and comment conventions in that guide without exception unless a project-specific instruction explicitly overrides them.

## JUnit Test Coverage Target

**Test Coverage Objective:** Achieve ~50% coverage by testing the highest-value methods.

**Scope:** Focus JUnit tests on the top ~50% highest-value methods, prioritizing:
- Complex business logic and algorithms
- Core functionality critical to application behavior
- Edge cases and boundary conditions for high-value methods
- Error handling and exception paths

**Maintenance Requirement:** JUnit tests must be updated after each code change to:
- Reflect renamed or refactored methods
- Cover new high-value methods added to the codebase
- Maintain the 50% coverage target across all classes
- Ensure all tests pass and accurately reflect current implementations

