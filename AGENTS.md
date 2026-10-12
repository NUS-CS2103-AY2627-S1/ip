# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner - Intermediate
* IDE and level of expertise: VSC, 2 Years

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

## Coding Standard:

All Java code in this project must strictly follow the **SE-EDU Java Coding Standard (Intermediate)** as defined in the `seedu-java-coding-standard` skill (`.agents/skills/seedu-java-coding-standard/SKILL.md`). Ensure naming conventions, layout, 4-space indentation, line wrapping at 120 characters, K&R braces, import hygiene (no wildcards), and Javadoc standards are followed at all times.

## Git

All Git commit messages and branch operations must strictly follow the **SE-EDU Git Conventions** as defined in the `seedu-git-standard` skill (`.agents/skills/seedu-git-standard/SKILL.md`).
- Subject line must be imperative, capitalized, no trailing period, and under 50 characters (hard limit: 72 chars).
- Commit body must explain WHAT and WHY (not HOW), wrapped at 72 characters, with paragraphs separated by blank lines.
- Use lightweight tags unless the user requests an annotated tag.
- Do not commit or push unless explicitly asked.

## Testing and Test Coverage:

- **Target Coverage**: Maintain a JUnit test coverage target of ~50%, focusing on the highest-value methods (prioritizing complex, core, or critical business logic such as date-time parsing, task serialization/deserialization, and entity validation).
- **Test Maintenance**: JUnit tests must be updated or added after each code change to comply with the 50% coverage target and prevent regressions.

## Post-code update workflow:

After each code update:
1. Update or add JUnit tests in `src/test/java/` to maintain the ~50% high-value method coverage target.
2. Execute `./gradlew test` to verify all JUnit tests pass.
3. Update `test/ui-test-plan.md` (if needed, e.g. when inputs, commands, or outputs are added/modified).
4. Invoke the `test-ui` skill (by executing `python3 .agents/skills/test-ui/scripts/run-ui-tests.py`) to verify that all UI tests pass.

