# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Moderate
* IDE and level of expertise: Moderate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks.
* **macOS**: Use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.
* **Windows**: Java 25 is installed at `C:\Program Files\Java\jdk-25`. When running Gradle or Java commands via PowerShell, ensure `JAVA_HOME` is set:
  ```powershell
  $env:JAVA_HOME = 'C:\Program Files\Java\jdk-25'; .\gradlew.bat <task>
  ```

## Git

* Strictly follow the **SE-EDU Git Conventions** as defined in [SE-EDU Git Conventions](https://se-education.org/guides/conventions/git.html) and the project skill `seedu-git-standard`.
* Use lightweight tags unless the user requests an annotated tag.
* Commit conventions:
  * **Subject line**: Imperative mood (e.g., `Add ...`, `Refactor ...`, `Extract ...`), concise summary (50 characters or less, hard limit 72), capitalize first letter, no trailing period.
  * **Body**: Separate from subject with a blank line for non-trivial commits. Explain the context/problem, use the "Let's" syntax to outline changes, and provide rationale for the change.
* Do not commit or push unless explicitly asked.

## Coding standard:

* Strictly follow the **SE-EDU Java Coding Standard (Basic + Intermediate rules)** as defined in [SE-EDU Java Conventions](https://se-education.org/guides/conventions/java/intermediate.html) and the project skill `seedu-java-coding-standard`.
* For any topics not covered by the SE-EDU standard, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
* Adhere to all naming (PascalCase classes, camelCase methods/variables, SCREAMING_SNAKE_CASE constants), layout (4 spaces indent, 8 spaces wrapped indent, 120 char max line length, mandatory braces for all control blocks), statements (no wildcard imports, interface types where applicable), and documentation standards (descriptive Javadoc for public/protected members with third-person declarative summaries).

## Testing and test coverage:

* **Target**: Focus JUnit tests on the top ~50% highest-value methods (prioritizing complex, core, or critical business logic, such as input parsing, persistent storage, command execution, and data models).
* **Maintenance**: JUnit tests must be updated or added after each code change to continuously comply with this 50% coverage target.
* Ensure all tests pass (`./gradlew test`) before considering a coding task complete.


