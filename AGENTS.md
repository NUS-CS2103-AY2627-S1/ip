# Default user context

* Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.
* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Testing

Maintain JUnit coverage for approximately the top 50% highest-value methods, prioritizing
complex, core, and critical business logic. Update the relevant JUnit tests after every code
change so the project continues to meet this target.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
For every future commit, follow `.agents/skills/seedu-git-standard/SKILL.md`.
Do not commit or push unless explicitly asked.

## Java coding standard

For all Java code created, edited, or reviewed in this project, follow
`.agents/skills/seedu-java-coding-standard/SKILL.md`.

## Customization

The sample output is illustrative only. Output formatting and command/display formats may differ from the sample. The chatbot's personality may also be customized to make it unique, as long as the required behavior and functionality are preserved.

# Project documentation

Current commands, datetime formats, display conventions, important files, and behavior details are documented in docs/PROJECT_BEHAVIOR.md. Keep that document updated whenever these details change.

# Source organization

Place new Java source files in the existing `miku.*` package structure. Group files by responsibility (for example, `miku.command`, `miku.task`, `miku.parser`, `miku.storage`, or `miku.ui`) instead of adding files directly under `src/main/java`.

# Miku presentation style

Keep the project cheerful and idol-like, using symbols such as ★, ☆, ♪, ♫, and ✨ in user-facing messages and suitable documentation. Use symbols meaningfully and preserve readability.

# Micro-commit policy

Use small, focused commits. Keep implementation, tests, and directly related documentation together; do not mix unrelated refactoring or formatting cleanup into feature commits. Use lightweight tags only for milestones, and do not commit or push unless explicitly requested.
