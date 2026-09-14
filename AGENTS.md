# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 2.5 Years.
* IDE and level of expertise: IntelliJ IDEA, 6 months.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Post-code-update workflow

After every code update:

1. **Update test plan**: If the change affects user-visible behaviour, update `test/ui-test-plan.md` to reflect the new or modified functionality (add, modify, or remove test cases as needed).
2. **Update JUnit tests**: If the change adds, modifies, or removes methods in tested classes, update the corresponding JUnit tests in `src/test/java/` to maintain the 50% test coverage target. Focus on the highest-value methods: complex logic, core business rules, and critical data paths (e.g., parsing, serialization, persistence). Run `./gradlew test` to verify all JUnit tests pass.
3. **Run UI tests**: Invoke the `test-ui` skill to verify all test cases pass. Do not report the change as complete until tests pass.

# Coding standard

Follow the `seedu-java-coding-standard` skill for all Java code in this project. Apply its rules when writing new code and when modifying existing code.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
