---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for all Java code created, edited, or reviewed in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for every Java change in this repository. Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Required rules

- Use lowercase package names, PascalCase noun class and enum names, camelCase verb method names, camelCase variables, and SCREAMING_SNAKE_CASE constants.
- Use English names and comments with American spelling. Name booleans so they read as booleans, normally using prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Name collections in the plural. Use short iterator names only for small scopes; reserve `j`, `k`, and later letters for nested loops.
- Indent with four spaces and never tabs. Prefer lines below 110 characters and never exceed 120. Indent wrapped lines eight spaces beyond the parent line, break after commas or before operators, and keep a method name attached to its opening parenthesis.
- Use K&R braces. Put conditional bodies on separate lines and always brace loop and conditional bodies, even when they contain one statement.
- Put spaces around operators and after Java keywords, commas, colons used as operators, and semicolons in `for` headers. Separate logical units with blank lines.
- Put every class in a package. List imports explicitly, keep them minimal, and order groups consistently with static imports first, then `java`, `javax`, third-party, and project imports, separated where groups exist.
- Attach array brackets to the type. Declare variables in the smallest possible scope and initialize them where declared when a valid initial value exists. Do not expose mutable class variables publicly.
- Format `switch`, `try`/`catch`/`finally`, loops, and conditionals as shown in the authoritative standard. Mark intentional fall-through with `// Fallthrough`.
- Write descriptive Javadoc for every public class and public method, except obvious getters/setters, test code, and overrides whose inherited documentation applies exactly. Begin method summaries with a third-person verb such as “Returns”, “Adds”, or “Saves”. Keep Javadoc tags complete, punctuated, and separated from the description by one blank line.

## Workflow

Before completing a Java change:

1. Review every touched Java file against these rules, not only the changed lines.
2. Correct violations within the user-authorized scope without changing behavior unnecessarily.
3. Run the relevant tests and `./gradlew javadoc` when documentation changed.
4. Report any deliberate exception to the standard with its reason.
