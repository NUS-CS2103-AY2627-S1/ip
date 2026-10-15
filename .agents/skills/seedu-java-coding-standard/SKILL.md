---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, changing, or reviewing Java code in the Zikiai project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) for every Java file in this project. For topics it does not cover, follow the Google Java Style Guide.

## Review workflow

Before changing Java code, inspect the surrounding class and related tests. Keep the implementation as simple as the requirement permits. After changing it, review every changed Java line against the checklist below and run the verification required by `AGENTS.md`.

## Naming

- Use lowercase package names organized below the project name.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase English names for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Make boolean names read as questions, normally starting with `is`, `has`, `was`, `can`, or `should`.
- Use plural nouns for collections.
- Keep acronyms lowercase within camelCase names, such as `exportHtml` rather than `exportHTML`.
- Use descriptive names for wide scopes; short index names such as `i` are acceptable only for small scopes.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout

- Indent using four spaces and never tabs.
- Prefer lines below 110 characters and never exceed 120 characters.
- Indent wrapped lines eight spaces beyond their parent. Break after commas and before operators, including `.` in method chains.
- Use K&R braces. Always use braces for loop and conditional bodies, even when they contain one statement.
- Put each conditional body on separate lines.
- Surround operators with spaces and follow commas and Java keywords with spaces.
- Separate distinct logical units with one blank line; do not add arbitrary vertical whitespace.

## Packages, imports, types, and variables

- Put every class in a package.
- List imports explicitly; never use wildcard imports.
- Keep imports minimal and consistently grouped: static imports, Java library imports, third-party imports, then project imports. Separate groups with blank lines.
- Attach array brackets to the type, for example `String[] args`.
- Declare variables in the smallest useful scope and initialize them at declaration when a genuine value is available.
- Do not expose mutable class variables publicly. Public constants are acceptable.

## Comments and Javadocs

- Write comments in English using American spelling and no local slang.
- Document every public class and public method, except straightforward getters/setters, test code, and overrides whose inherited contract applies exactly.
- Start a Javadoc summary with a concise third-person verb such as `Returns`, `Adds`, or `Creates`.
- Put `/**` on its own line, align each `*`, and leave a blank line before block tags.
- Document either all parameters or none. End parameter, return, and exception descriptions with punctuation.
- Explain intent and constraints; do not narrate self-explanatory code.

## Final check

Confirm the code compiles, behavior remains correct, relevant JUnit coverage remains sufficient, and the full UI test plan passes. Do not weaken tests to make a style-only change pass.
