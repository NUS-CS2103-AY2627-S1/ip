---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, changing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
for every Java change in this project. Use the Google Java Style Guide for topics the SE-EDU standard does not cover.
Preserve existing behavior unless the user also requests a behavior change.

## Naming

- Use lowercase package names, PascalCase noun names for classes and enums, camelCase verb names for methods, and
  camelCase names for variables.
- Use SCREAMING_SNAKE_CASE for constants. Give related constants a common prefix.
- Keep abbreviations and acronyms lowercase within names, such as `exportHtmlSource` rather than `exportHTMLSource`.
- Write names in English. Give wider-scope variables more descriptive names; reserve short scratch names for small scopes.
- Name booleans so they read as boolean values, preferably with `is`, `has`, `was`, `can`, or `should` prefixes.
- Use plural names for collections. Test methods may use
  `featureUnderTest_testScenario_expectedBehavior`.

## Layout

- Indent with four spaces and never tabs. Use K&R braces.
- Aim for at most 110 characters per line and never exceed 120. Indent wrapped lines eight spaces beyond their parent.
- When wrapping, break after commas and before operators, including `.`, `&`, and `|`. Keep a method name attached to its
  opening parenthesis and prefer higher-level breaks.
- Surround operators with spaces; put spaces after Java keywords, commas, and `for` semicolons.
- Separate logical units with one blank line. Avoid trailing whitespace and redundant blank lines.
- Always use braces for loop and conditional bodies, and put conditional bodies on separate lines.
- Add `// Fallthrough` to a colon-style `switch` case that intentionally falls through.

## Declarations and imports

- Put every class in a package and separate the package declaration from imports or the type declaration with a blank line.
- List imports explicitly; do not use wildcard imports. Remove unused imports and keep ordering consistent across the project.
- Attach array brackets to the type, such as `int[] values`.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid value is available.
- Keep class variables non-public unless they are constants or belong to a behavior-free data class.

## Comments and Javadoc

- Write comments in English using American spelling and indent them with the code they describe.
- Add descriptive Javadoc to every public class and public method, except obvious getters/setters, test code, and overrides
  whose inherited documentation applies exactly.
- Start a Javadoc summary with a concise third-person verb such as `Returns`, `Adds`, or `Saves`.
- Use a blank Javadoc line before tags. Capitalize and punctuate tag descriptions. Include either all useful `@param` tags
  or none when every parameter is already self-explanatory.
- Document thrown exceptions and non-obvious return values. Use `{@inheritDoc}` when an override needs inherited text plus
  clarification.

## Before finishing

Review every changed Java line for these rules, run the relevant JUnit and UI validation required by `AGENTS.md`, and report
any deliberate exception to the standard.
