# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Slightly below average
* IDE and level of expertise: Average

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Post-update UI testing

After every application code update, review `test/ui-test-plan.md` and update it when the change adds or alters user-visible console behavior, inputs, commands, or expected output. Then invoke the project-specific `$test-ui` skill to run the complete plan. Run every case even when an earlier case fails, and include the full console session record plus all failures in the handoff.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Java Coding Standard

All Java code added, modified, or generated in this repository MUST strictly follow the SE-EDU Java Coding Standard (Intermediate Level):

* **Naming Conventions:**
  * `PascalCase` for Class names (e.g., `TaskList`, `Ui`).
  * `camelCase` for method and variable names (e.g., `readCommand`, `taskNumber`).
  * `ALL_CAPS` with underscores for constants and Enum values (e.g., `MAX_CAPACITY`, `KEYWORD_UNKNOWN`).
  * No abbreviation unless extremely common (e.g., use `message` instead of `msg`).
* **Javadoc & Comments:**
  * Include clear Javadoc headers for all public classes, non-trivial methods, and fields.
  * Start method Javadoc summaries with third-person present tense verbs (e.g., `Reads user input...`, `Displays an error...`).
  * Include `@param` and `@return` tags for all parameter-taking and non-void methods.
* **Layout & Formatting:**
  * Use 4 spaces for indentation (no tabs).
  * Use standard brace placement (`K&R style`: opening brace on the same line, closing brace on its own line aligned with the declaration).
  * Always use braces `{}` for `if`, `else`, `for`, `while`, even if the body is single-line.
* **Switch Statements:**
  * Every `switch` block must cover all cases explicitly or include a `default` case.

## Git Commit Standard

All proposed and generated commit messages MUST follow the SE-EDU Git Standard:

* **Subject Line:**
  * Must start with a short imperative verb (e.g., `Add`, `Fix`, `Update`, `Refactor`, `Remove`).
  * Capitalized first letter, no period at the end.
  * Maximum 50 characters long.
* **Body (when applicable):**
  * Separated from the subject line by a blank line.
  * Explain *what* and *why*, rather than *how*.
  * Wrap lines at 72 characters.
