---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, modifying, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Use the [SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) for every Java code change in this repository. For matters it does not cover, follow the Google Java Style Guide.

## Apply these rules

- Keep every class in a lowercase project-rooted package. Use PascalCase nouns for types, camelCase verbs for methods, camelCase for variables, and `UPPER_SNAKE_CASE` for constants. Name booleans as predicates such as `isDone` or `hasData`; use plural names for collections.
- Use four spaces for indentation, K&R braces, and braces for every loop and conditional body. Keep conditions on their own lines.
- Keep lines at 120 characters or fewer; aim for 110 or fewer. Wrap at readable higher-level boundaries, normally after commas or before operators, using an additional eight spaces of indentation for wrapped lines.
- Use explicit, minimal imports in a consistent order. Do not use wildcard imports.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid value is available.
- Separate logical units with one blank line where that improves readability.
- Write English, American-spelling comments. Add descriptive JavaDoc to every public class and public method, except self-explanatory getters/setters and overrides whose inherited documentation applies unchanged. Document non-trivial private methods when their intent is not immediately clear.
- Use the `featureUnderTest_testScenario_expectedBehavior` convention for long JUnit test names.

## Before finishing

Review changed Java files against these rules, correct applicable violations, and run the relevant project tests.
