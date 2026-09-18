---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding-standard rules for Java code in this project. Use whenever creating, editing, refactoring, or reviewing Java source or tests.
---

# SE-EDU Java Coding Standard

Apply the basic and intermediate rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
For topics it does not cover, follow the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Review workflow

When Java code changes, review every affected Java file against the rules below.
Fix violations within the user's requested scope and preserve behavior unless the user asks for a behavior change.
Apply the same rules to production and test code except where the standard explicitly exempts tests, such as Javadocs.

## Required rules

- Put every class in a lowercase package. For this project, use the `meka` root package and add logical subpackages only when the code base grows enough to benefit from them.
- Use noun-based PascalCase names for classes and enums, camelCase verbs for methods, camelCase for variables, and SCREAMING_SNAKE_CASE for constants.
- Treat abbreviations and acronyms as words inside identifiers, such as `Meka` rather than `MEKA` and `exportHtml` rather than `exportHTML`.
- Name booleans so they read as boolean conditions, normally with `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections. Use short iterator names only for small loop scopes; reserve `j`, `k`, and later letters for nested loops.
- Use English names and comments, American spelling, and names whose detail matches their scope.
- Indent with 4 spaces and continuation lines with 8 additional spaces. Do not use tabs.
- Keep lines at or below 120 characters and aim for 110. Break after commas and before operators, dots, or multi-catch pipes when wrapping improves readability.
- Use K&R braces. Always brace loop and conditional bodies, including single statements, and put conditional bodies on separate lines.
- Format `switch` case labels one indentation level inside the switch. Add `// Fallthrough` whenever a statement-style case intentionally falls through.
- Surround operators with spaces, add a space after reserved words and commas, and separate logical units inside a block with blank lines.
- List imports explicitly. Group and order imports consistently: static imports, Java/Jakarta imports, third-party imports, then project imports, with one blank line between groups.
- Attach array brackets to the type. Initialize variables at declaration when a real value is available, and declare them in the smallest useful scope.
- Keep non-constant class variables private unless the class is intentionally a behavior-free data class.
- Write descriptive Javadocs for every production class and public method, except getters/setters and overrides whose inherited documentation applies exactly. Test classes and methods are exempt.
- Start method Javadocs with a third-person verb such as `Returns`, `Adds`, or `Sends`. Put `/**` on its own line and keep one blank line before tags.
- Either document all parameters or omit all `@param` tags when every parameter is self-explanatory. End `@param`, `@return`, and `@throws` descriptions with punctuation.
- Use test method names in the form `featureUnderTest_testScenario_expectedBehavior`, omitting later parts only when the broader scope is intentional.

## Completion checks

Before finishing a Java change:

1. Search affected files for lines over 120 characters, wildcard imports, default-package classes, unbraced control flow, and inconsistent case indentation.
2. Confirm names and Javadocs communicate intent and follow the rules above.
3. Run the project-required Java 25 unit and UI tests.
