---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, reviewing, or testing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for every Java change in this repository. Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Review checklist

- Put every class in a suitable lower-case package rooted at the project name, `mochi`.
- Use noun-based PascalCase names for classes and enums, camelCase verbs for methods, camelCase for variables, and SCREAMING_SNAKE_CASE for constants.
- Keep acronyms in normal camel case, use English names, give wider-scope variables more descriptive names, and name collections in the plural.
- Name booleans so they read as booleans, normally with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use the permitted three-part underscore convention for tests: `featureUnderTest_testScenario_expectedBehavior`.
- Indent with four spaces and no tabs. Use K&R braces and braces around every loop and conditional body.
- Keep lines below 120 characters, preferably below 110. Indent wrapped continuation lines eight spaces beyond their parent and break after commas or before operators.
- Put a blank line between logical blocks. Surround operators with spaces, add spaces after reserved words and commas, and keep statement layout consistent.
- List imports explicitly and consistently: static imports first, then Java/Jakarta imports, third-party imports, and project imports, with blank lines between groups. Remove unused imports.
- Attach array brackets to the type. Declare variables in the smallest practical scope and initialize them at declaration when a valid value is available.
- Keep fields encapsulated; public fields are allowed only for constants or behavior-free data classes.
- Order class members predictably: class documentation, declaration, static fields, instance fields, constructors, then methods.
- Write comments in English using American spelling. Add descriptive JavaDoc to every public class and public method, except straightforward getters/setters, exact overrides, and test code.
- Format JavaDoc with `/**` on its own line, a short first sentence, a blank line before tags, aligned stars, punctuation on tag descriptions, and no blank line before the declaration.
- For switch statements, indent each `case` one level inside the switch and include `// Fallthrough` whenever omission of `break` is intentional.

## Workflow

Before finishing a Java task:

1. Review all changed Java lines against the checklist, including tests.
2. Check for tabs, wildcard imports, and lines longer than 120 characters.
3. Run the project tests and JavaDoc task under Java 25 when documentation changed.
4. Fix coding-standard violations introduced by the task; do not perform unrelated behavioral refactors solely for style.
