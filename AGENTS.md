# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

- Prior knowledge: Basic Java and OOP concepts.
- Level of programming experience: Intermediate
- IDE and level of expertise: VSCode, intermediate

## Standards and conventions

Follow the [CS2103/T standards and conventions](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html).

### Java

Follow the [SE-EDU Java coding standard (basic and intermediate)](https://se-education.org/guides/conventions/java/intermediate.html):

- Put every class in a package and use explicit, consistently ordered imports.
- Use English PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase for variables, and SCREAMING_SNAKE_CASE for constants.
- Name boolean variables and methods with boolean prefixes such as `is`, `has`, `can`, or `should`; use plural names for collections.
- Use four spaces for indentation, K&R braces, spaces around operators, and a hard line limit of 120 characters.
- Wrap every loop and conditional body in braces, and mark intentional switch fall-through with `// Fallthrough`.
- Initialize variables at declaration when practical and keep them in the smallest scope possible; do not expose class fields publicly.
- Write English, American-spelled Javadoc for public classes and methods, except for getters, setters, and methods whose inherited documentation applies exactly.

### Git

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

- Write commit subjects in the imperative mood, capitalize the first letter, omit the final period, and keep them within 72 characters (50 preferred).
- For non-trivial commits, add a body separated by a blank line, wrapped at 72 characters, explaining what changed and why.
- Use meaningful kebab-case branch names.

### Markdown and documentation

When writing Markdown, follow the [SE-EDU Markdown style guide](https://se-education.org/guides/conventions/markdown.html): use GitHub Flavored Markdown, blank lines around headings, lists, and code blocks, `*` bullets, and generic `1.` numbering. Do not wrap prose to an arbitrary line length.

The [Google developer documentation style guide](https://developers.google.com/style) is recommended for project documentation.

# Guidance for interacting with users

- Explain the rationale for significant actions: what you did and why.
- Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:
  - When suggesting a Git command, briefly explain what it does.
  - Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  - Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  - When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Required UI testing after code updates

After every code update:

1. Review `test/ui-test-plan.md` and update its test cases or expected outputs when
   the change affects commands, console output, or relevant UI coverage.
2. Invoke the project-specific `$test-ui` skill, even when the test plan did not
   need changes.

Do not report the code update as complete until `$test-ui` finishes. If a test
fails, stop the test session as required by the skill and report the failure
instead of running later cases.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
