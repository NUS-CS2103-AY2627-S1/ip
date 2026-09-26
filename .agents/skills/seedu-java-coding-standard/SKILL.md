---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard when creating, editing, reviewing, or refactoring Java code.
---

# SE-EDU Java Coding Standard

Apply the basic and intermediate rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use Google Java Style only for topics the SE-EDU standard does not cover.

## Naming

- Use lowercase package names organized under the project name.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase nouns for variables.
- Use SCREAMING_SNAKE_CASE for constants; give related constants a common prefix.
- Keep acronyms lowercase within names, such as `exportHtmlSource`.
- Use English names. Give wide-scope variables more descriptive names.
- Name booleans to read as booleans, preferably with `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Use `i`, `j`, and similar iterator names only in small loop scopes.
- Name tests `featureUnderTest_testScenario_expectedBehavior`, omitting parts only when unnecessary.

## Layout and whitespace

- Indent with four spaces and never tabs.
- Keep lines at most 120 characters and preferably below 110.
- Indent wrapped lines eight spaces beyond the parent line.
- Break after commas and before operators, including `.`. Keep method names attached to `(`.
- Prefer high-level line breaks that make the expression easier to read.
- Use K&R braces for classes, methods, conditionals, loops, `switch`, and `try` statements.
- Put spaces around operators and after keywords, commas, and `for` semicolons.
- Separate logical blocks with one blank line.

## Packages, imports, types, and variables

- Put every class in a package matching its source directory.
- Use explicit imports only; do not use wildcard imports.
- Remove unused imports and keep import ordering consistent across the project.
- Attach array brackets to the type, such as `String[] values`.
- Initialize variables where declared when practical and keep them in the smallest useful scope.
- Do not expose class variables publicly unless the class is a behavior-free data class; constants are exempt.

## Control flow

- Always use braces around loop and conditional bodies, including single statements.
- Put conditional bodies on lines separate from their conditions.
- Mark intentional traditional `switch` fall-through with `// Fallthrough`.

## Comments and Javadocs

- Write comments in English with American spelling and focus on intent rather than obvious mechanics.
- Add descriptive Javadocs to every public class and public method, except tests, straightforward getters/setters,
        and overrides whose inherited documentation applies exactly.
- Start a method Javadoc summary with a third-person verb such as `Returns`, `Adds`, or `Sends`.
- Make the first sentence a concise summary and punctuate all descriptions.
- Include either all useful `@param` tags or none when every parameter is already self-explanatory.
- Include `@return` and `@throws` when they add information not already obvious from the summary.
- Indent comments with the code they describe and do not separate a Javadoc from its declaration.

## Review workflow

1. Inspect every changed Java file for the rules above.
2. Run Checkstyle and fix violations in the changed scope.
3. Generate Javadocs and resolve warnings caused by the changed scope.
4. Run the project tests required by `AGENTS.md` using Java 25.
