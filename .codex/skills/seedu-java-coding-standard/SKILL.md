---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for Java source and test code in this repository.
---

# SE-EDU Java Coding Standard

Apply this skill whenever creating, editing, reviewing, or generating Java code in this repository. Use the
[SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the authority. For topics it does not cover, follow the Google Java Style Guide.

## Required conventions

- Put every class in a logical, lowercase package rooted at the project name, `gongrilla`.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase for variables, and
  SCREAMING_SNAKE_CASE for constants. Boolean names should read as predicates, such as `isDone` or `hasData`.
- Give collections plural names. Use short index names only in small loop scopes. Write all names in English and
  keep acronyms lowercase within names.
- Indent with four spaces and never tabs. Use K&R braces and braces around every loop and conditional body.
- Keep lines below 120 characters, aiming for 110. Indent continuation lines eight spaces beyond their parent and
  break after commas or before operators when practical.
- Use consistent whitespace around operators and after keywords, commas, colons, and semicolons.
- List imports explicitly; do not use wildcard imports. Keep imports minimal and ordered consistently.
- Attach array brackets to the type. Initialize variables at declaration when a valid value is available and declare
  them in the smallest useful scope. Keep mutable fields non-public.
- Indent `case` labels inside `switch` blocks. Mark intentional fall-through with `// Fallthrough`.
- Write comments in clear English using American spelling. Explain intent, not obvious mechanics, and remove stale
  TODO comments when the described work is complete.
- Add descriptive Javadocs to every public class and public method, except straightforward getters/setters, tests,
  and overrides whose inherited documentation applies exactly. Start summaries with a third-person verb such as
  `Returns`, `Creates`, or `Adds`; document all parameters or none; punctuate tag descriptions; and include relevant
  `@return` and `@throws` tags.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior()`.

## Workflow

1. Inspect nearby code so new formatting and import grouping remain consistent with the project.
2. Apply the rules while editing; do not defer avoidable cleanup in touched code.
3. Before handing off, check changed Java files for tabs, wildcard imports, lines over 120 characters, missing braces,
   stale comments, and missing public API Javadocs.
4. Run the relevant JUnit tests and the broader Gradle test suite when practical.
5. Run `./gradlew javadoc` (or `.\gradlew.bat javadoc` on Windows) when public Javadocs change.
