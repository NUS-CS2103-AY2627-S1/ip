# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Course website

Consult the current AY2026/27 Semester 1 course website only when interpreting,
verifying, or resolving ambiguity about course requirements, standards, or conventions,
or when the user explicitly asks you to refer to it for a specific matter:
https://nus-cs2103-ay2627-s1.github.io/website/index.html

For routine implementation, debugging, and testing, use the repository's local
instructions and skills. Never use previous-semester course websites.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate beginner; completed introductory programming and OOP courses, but still learning software engineering practices.
* IDE and level of expertise: IntelliJ IDEA; only basic familiarity.

# Guidance for interacting with users

* Briefly explain the rationale for significant actions and unfamiliar Git commands.
* Keep code self-explanatory. Add Javadoc to all classes and to nontrivial methods or fields whose purpose is not obvious.
* Prefer the simplest sufficient design; mention more advanced alternatives only when useful.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Course standards and conventions

* Treat only the requirements marked REQUIRED on the current [course standards page](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html) as mandatory unless another project requirement says otherwise.
* Java code must follow the basic and intermediate rules in the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
* Never run the `check-course-conventions` skill automatically. Run it only when the user explicitly invokes `$check-course-conventions`; Java edits, reviews, commits, and pushes do not trigger it.

## UI testing

* For an internal refactor that does not change observable command-line behavior, compile the application and run directly relevant non-UI tests, if any. Do not invoke the `test-ui` skill solely because application code changed.
* When observable command-line behavior changes, review the coverage in `test/ui-test-plan.md`, update `test/ui-test-cases.json` when needed, and invoke the `test-ui` skill for the affected case IDs.
* Run the full UI suite for cross-cutting changes to shared command parsing, dispatch, output framing, or task storage; for changes to the UI-test harness or shared expectations; and before a project milestone.
* Do not change expected output merely to make a failing test pass. Update expectations only when the intended application behavior has changed.

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
Only when the user explicitly asks for a commit-message suggestion or review, or asks to create a commit, use the `craft-commit-message` skill to apply the required [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) and explain the change's rationale.
