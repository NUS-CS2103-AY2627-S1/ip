---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, reviewing, or testing Java code in this project.
---

# SE-EDU Java Coding Standard

Apply these rules to all Java production and test code in this repository. Use
the [SE-EDU standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the authority and Google Java Style only for topics it does not cover.

## Naming

- Use lowercase package names and PascalCase noun names for classes and enums.
- Use camelCase verb names for methods and camelCase noun names for variables.
- Use SCREAMING_SNAKE_CASE for constants. Give related constants a common prefix.
- Use English names. Keep acronyms lowercase within compound names, such as
  `exportHtmlSource` rather than `exportHTMLSource`.
- Give wider-scope variables more descriptive names. Reserve `i`, `j`, and similar
  names for small loop scopes.
- Prefix boolean names with words such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Name test methods using
  `featureUnderTest_testScenario_expectedBehavior` where applicable.

## Layout

- Indent with four spaces and never tabs.
- Keep lines below 110 characters where practical and never exceed 120.
- Indent wrapped lines eight spaces beyond the parent line.
- Break after commas and before operators or method-chain dots.
- Keep a method or constructor name attached to its opening parenthesis.
- Use K&R braces. Always use braces for loops and conditionals.
- Put conditional bodies on lines separate from their conditions.
- Separate logical blocks with one blank line.
- Mark intentional switch fallthrough with `// Fallthrough`.

## Statements and structure

- Put every class in a logical package rooted at `crow`.
- List imports explicitly, remove unused imports, and keep their ordering
  consistent with nearby files.
- Attach array brackets to the type, such as `String[] arguments`.
- Declare variables in the smallest useful scope and initialize them at
  declaration whenever a real initial value is available.
- Do not expose mutable class fields publicly; use encapsulation.

## Documentation

- Write comments in English using American spelling.
- Add descriptive Javadocs to every public class and public method, except
  straightforward getters/setters, exact overrides, and test code.
- Start Javadoc summaries with a third-person verb such as `Returns`, `Adds`, or
  `Creates`.
- Keep parameter documentation complete: document all parameters or omit all
  when every name is self-explanatory.
- Explain purpose and behavior rather than restating implementation mechanics.

## Review workflow

When changing Java code:

1. Apply the rules while editing rather than as a separate cosmetic rewrite.
2. Check every changed Java line for the hard 120-character limit.
3. Run the relevant tests and the full Gradle check with Java 25 when practical.
4. Report any deliberate exception to the standard and its rationale.
