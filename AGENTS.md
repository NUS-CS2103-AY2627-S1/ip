# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: basic
* IDE and level of expertise: basic

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible. Try to minimise inline comments.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
You MUST suggest a commit message every time after you make any changes to the codebase.
When proposing or creating a commit message:
* Comply with the Conventional Commits format, keeping descriptions short and simple.
* Capitalize the initial letter of the subject/description following the colon (e.g., `feat: Add search feature`, `fix: Correct date parsing logic`).
Do not commit or push unless explicitly asked.

### Branching and Merging Workflow
* **Feature Branches**: Implement each increment/task on a dedicated branch starting with the prefix `branch-` (e.g. `branch-Level-7`, `branch-Level-8`, `branch-A-MoreOOP`, `branch-A-Packages`, `branch-A-JUnit`, `branch-Level-9`).
* **Merging into `master`**:
  1. Ensure the working tree is clean and all tests pass on the feature branch (`./gradlew test`).
  2. Switch to `master`: `git checkout master`.
  3. Ensure `master` is synchronized with remote: `git pull origin master`.
  4. Perform an explicit non-fast-forward merge: `git merge --no-ff <branch-name> -m "chore: Merge <branch-name> into master"`.
  5. Tag the merge commit with the increment tag (e.g. `git tag <tag-name>`).
  6. Verify tests and build pass on `master` before pushing (`./gradlew test && ./gradlew build`).

## Code Style

Ensure that all code complies with the [SE-EDU Java Coding Standard](https://se-education.org/guides/conventions/java/index.html).

## Testing and Test Coverage

* Maintain a test coverage target of ~50% focused on the highest-value methods (prioritizing complex, core, or critical business logic).
* JUnit tests MUST be updated/added after each code change to comply with this coverage target.
* Follow Gradle and JUnit naming and placement conventions (e.g. `featureUnderTest_testScenario_expectedBehavior()`).
