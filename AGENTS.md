# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Mandatory project standards

All Java code in this repository must follow the project-specific
`.codex/skills/seedu-java-coding-standard/SKILL.md`, based on the SE-EDU Java
coding standard (basic + intermediate):
<https://se-education.org/guides/conventions/java/intermediate.html>.
Review changed Java and test files for naming, layout, imports, braces,
variable scope, and descriptive Javadoc before completing the change.

All proposed and created commits must follow
`.codex/skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git
conventions: <https://se-education.org/guides/conventions/git.html>. Use
imperative, capitalized subject lines without trailing periods, keep subjects
within 72 characters (prefer 50), and include a wrapped WHAT/WHY body for
non-trivial commits. Use meaningful kebab-case branch names. Split unrelated
changes into separate commits, and do not commit or push unless the user has
explicitly authorized it.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: low
* IDE and level of expertise: IntelliJ and low

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

## UI testing:

After each code update, review and update `test/ui-test-plan.md` if the user-visible behavior changed, then invoke the project-specific `test-ui` skill. The skill must be run before reporting the update as complete.

## JUnit coverage:

Maintain JUnit tests for approximately the highest-value 50% of methods, prioritizing complex, core, or critical business logic. Update the relevant JUnit tests after every code change so that the test suite continues to meet this coverage target.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
