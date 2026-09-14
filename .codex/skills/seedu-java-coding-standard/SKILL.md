---
name: seedu-java-coding-standard
description: Review and update this project's Java source and tests to follow the SE-EDU basic and intermediate Java coding standard. Use for Java implementation, refactoring, code review, formatting, naming, imports, Javadocs, and test naming.
---

# SE-EDU Java Coding Standard

Apply the SE-EDU Java coding standard at https://se-education.org/guides/conventions/java/intermediate.html to all Java code in this project. Use the official guide as the source of truth; use Google Java Style for topics it does not cover.

## Required checks

- Keep every class in a lowercase logical package under the project package root.
- Use PascalCase nouns for classes/enums, camelCase verbs for methods, and camelCase variables.
- Use SCREAMING_SNAKE_CASE for constants and plural names for collections.
- Use boolean names that read as booleans, such as `isDone`, `hasData`, or `shouldAbort`.
- Use four spaces, K&R braces, explicit imports, and consistent import ordering.
- Keep lines at or below 120 characters; wrap long lines with an additional eight-space indentation.
- Use braces for every loop and conditional body.
- Initialize variables at declaration when practical and keep them in the smallest scope.
- Keep fields private unless a documented exception applies.
- Use English and American spelling in identifiers and comments.
- Write descriptive Javadocs for public classes and public methods, including useful `@param`, `@return`, and `@throws` tags. Add Javadocs to non-trivial private members or methods.
- Name JUnit methods using `featureUnderTest_testScenario_expectedBehavior()`; the second or third part may be omitted only when the test scope is obvious.

## Workflow

1. Inspect the relevant source and test files before editing.
2. Report violations grouped by naming, layout, statements, imports, visibility, comments, and tests.
3. Make the smallest safe changes that preserve behavior.
4. Run Java 25 compilation and the relevant Gradle tests.
5. After behavior changes, update `test/ui-test-plan.md` and invoke the project `test-ui` skill using the complete plan.
6. Do not commit or push unless explicitly requested.

## Review cautions

- Do not rename public APIs or packages solely for style without checking all references.
- Do not replace explicit imports with wildcard imports.
- Do not add comments that merely restate obvious code; make comments explain intent or behavior.
- Preserve existing validation, persistence formats, user-visible messages, and test expectations unless the task explicitly changes them.
