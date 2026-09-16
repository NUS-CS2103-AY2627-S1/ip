---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU Java coding standard for Java source and test code in this project. Use whenever creating, editing, refactoring, or reviewing Java code in this repository.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) for every Java change in this project. Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics that the SE-EDU standard does not cover.

## Workflow

1. Inspect every Java file in the scope of the change, including tests.
2. Preserve behavior while correcting relevant style violations.
3. Recheck the changed Java lines against the rules below.
4. Compile and run the relevant Gradle tests with Java 25.

## Required rules

- Put every class in a logical, all-lowercase package rooted at the project name, `brucli`.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase nouns for variables, and SCREAMING_SNAKE_CASE for constants.
- Keep acronyms lowercase within identifiers.
- Give boolean variables and methods names that read as booleans, normally using prefixes such as `is`, `has`, `can`, or `should`.
- Use plural names for collections.
- Indent with four spaces and never tabs. Indent wrapped lines eight spaces beyond the parent line.
- Keep lines within 120 characters and aim for fewer than 110. Break after commas and before operators when wrapping improves readability.
- Use K&R braces. Always brace loop and conditional bodies, including single-statement bodies.
- Indent `case` and `default` labels one level inside their `switch`. Add `// Fallthrough` when a traditional switch case intentionally falls through.
- Surround operators with spaces, add spaces after commas and reserved words, and separate logical units with blank lines.
- Declare variables in the smallest practical scope and initialize them where declared when a valid value is available.
- Keep mutable fields non-public unless the class is deliberately a behavior-free data class.
- List imports explicitly. Group static imports first, followed by `java`, `javax`, third-party, and project imports; separate groups with one blank line and keep each group consistently ordered.
- Write English comments using American spelling.
- Add descriptive Javadoc to every public class and public method, except straightforward getters/setters, exact overrides, and test code. Put the opening `/**` on its own line, start with a concise third-person summary, and punctuate parameter descriptions.
- Do not add Javadoc to an override when the inherited contract applies exactly; use `{@inheritDoc}` only when extending that contract.

## Review boundary

Do not make unrelated design or behavior changes merely to satisfy a stylistic preference. When a rule is not covered above, consult the linked authoritative standard before deciding, then fall back to Google Java style.
