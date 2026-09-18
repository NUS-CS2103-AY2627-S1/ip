---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard when creating, editing, formatting, or reviewing Java production and test code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). For topics it does not cover, follow the Google Java Style Guide while preserving established project conventions.

## Naming

- Use lowercase package names, PascalCase noun names for classes and enums, camelCase variable names, and camelCase verb names for methods.
- Write constants in SCREAMING_SNAKE_CASE.
- Use `featureUnderTest_testScenario_expectedBehavior` for test method names where all three parts add value.
- Treat abbreviations and acronyms as ordinary words inside identifiers, such as `exportHtmlSource`.
- Use English names. Give wide-scope variables descriptive names; short conventional names are acceptable for small-scope scratch values.
- Name booleans to read as booleans, normally with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.

## Layout

- Indent with four spaces and never tabs.
- Keep lines below 110 characters where practical and never exceed 120 characters.
- Indent wrapped lines eight spaces beyond the parent indentation. Break after commas and before operators when wrapping improves readability.
- Use K&R braces. Always use braces for loop and conditional bodies, including single statements.
- Surround operators with spaces, place spaces after commas and reserved words, and separate logical units with one blank line.
- Keep connected constructs such as `if`/`else if`/`else` together without stray blank lines.

## Packages, imports, and declarations

- Put every class in a package.
- List imports explicitly; do not use wildcard imports. Remove unused imports and keep ordering consistent with nearby files.
- Attach array brackets to the type.
- Declare variables in the smallest useful scope and initialize them at declaration when a valid value is available.
- Do not expose mutable class variables publicly unless the class is intentionally a behavior-free data class.

## Comments and JavaDoc

- Write comments in English using American spelling. Explain purpose, intent, or non-obvious behavior rather than restating code.
- Preserve this project's stretch-level requirement: document all non-private classes and methods plus non-trivial private methods and fields.
- Start each JavaDoc summary with a concise verb phrase such as `Returns`, `Adds`, or `Displays`.
- Put `/**` on its own line, align each `*`, leave one blank comment line before block tags, and place no blank line between the comment and declaration.
- End `@param`, `@return`, and `@throws` descriptions with punctuation.
- Include either useful `@param` tags for every parameter or omit all of them when every parameter is already self-explanatory.
- Use `{@inheritDoc}` or omit a header only when an inherited method's documentation applies exactly.

## Applying the standard

Inspect the affected files before editing and make the smallest behavior-preserving change that achieves compliance. Keep command syntax, saved-data formats, and user-facing output stable unless the task explicitly changes them. Run the Java 25 clean build, tests, JavaDoc generation, and whitespace checks after modifying Java code.
