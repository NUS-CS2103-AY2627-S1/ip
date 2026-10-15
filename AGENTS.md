# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate

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

* Strictly follow the project-specific skill `seedu-git-standard` (based on [SE-EDU Git Conventions](https://se-education.org/guides/conventions/git.html)) for all future commits and branch naming.
* **Commit Subject**:
  * Limit to ~50 characters (hard limit: 72 characters).
  * Use the imperative mood (e.g., `Add README.md`, not `Added ...` or `Adding ...`).
  * Capitalize the first letter.
  * Do not end with a period.
  * Include `<scope>:` or `<category>:` prefix when applicable (e.g., `Parser: Fix deadline delimiter check`).
* **Commit Body**:
  * Mandatory for non-trivial commits.
  * Separate subject from body with one blank line and wrap at 72 characters.
  * Explain WHAT and WHY, not HOW (the diff shows how).
  * Structure the explanation logically: `{current situation} {why it needs to change} {what is being done about it (using Let's)} {why it is done that way}`.
* **Branch Names**: Use meaningful keywords in `kebab-case` (e.g., `refactor-ui-tests`) or `issueNumber-keywords` if related to an issue.
* Use lightweight tags unless the user requests an annotated tag.
* Do not commit or push unless explicitly asked.

## Coding Standards

* Strictly adhere to the project-specific skill `seedu-java-coding-standard` (based on [SE-EDU Java Coding Standard: Basic + Intermediate](https://se-education.org/guides/conventions/java/intermediate.html)) for all code in this project.
* Key conventions to enforce:
  * **Naming**: PascalCase for types/enums, camelCase for methods and variables, SCREAMING_SNAKE_CASE for constants, and three-part `featureUnderTest_testScenario_expectedBehavior()` format for unit test methods.
  * **Booleans**: Variables and getter methods must sound like booleans with prefixes such as `is`, `has` (e.g., `isDone()`, `hasTime()`). Setters must be of the form `void setFound(boolean isFound)` / `void setDone(boolean isDone)`.
  * **Collections**: Plural nouns for variables holding collections or arrays (e.g., `tasks`, `lines`).
  * **Layout**: 4-space indentation, max 120 chars line limit (soft limit 110 chars), K&R Egyptian brackets, all loop and conditional bodies enclosed in `{ }` on new lines.
  * **Switch statements**: Switch cases aligned cleanly, explicit `// Fallthrough` comment for fall-through cases.
  * **Imports**: Explicit imports only (no wildcard `*` imports), consistent ordering.
  * **Types**: Array specifiers attached to type (`String[] args`, not `String args[]`).
  * **Javadocs**: Descriptive header comments for all classes and public methods starting with 3rd-person singular verbs (`Returns ...`, `Adds ...`, `Parses ...`) with trailing punctuation for parameter/tag descriptions.

## Testing and Test Coverage

* **Test coverage target**: Maintain a test coverage target of ~50% across candidate methods, focusing JUnit tests on the top ~50% highest-value methods (prioritizing complex, core, or critical business logic over trivial boilerplate).
* **Test maintenance**: JUnit tests need to be updated after each code change to comply with the 50% test coverage target.
* Follow the standard directory convention with test classes placed under `src/test/java/` matching the package structure of the target classes.
* Ensure all tests pass cleanly via `./gradlew test` before concluding any feature change or refactoring.
