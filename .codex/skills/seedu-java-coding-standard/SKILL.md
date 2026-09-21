---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# Seedu Java Coding Standard

Use this skill whenever you create, edit, review, or refactor Java code in this project.

Apply the SE-EDU Java coding standard (basic + intermediate) at
<https://se-education.org/guides/conventions/java/intermediate.html>. For topics
not covered there, follow the Google Java Style Guide.

## Required conventions

- Use lower-case package names; PascalCase nouns for classes and enums; camelCase
  for variables and verb-based methods; and SCREAMING_SNAKE_CASE for constants.
- Keep names in English, use natural boolean names such as `isDone` or
  `hasData`, use plural names for collections, and keep short iterator names
  limited to small loop scopes.
- Use the permitted three-part underscore form for test methods:
  `featureUnderTest_testScenario_expectedBehavior`.
- Use four-space indentation, K&R braces, braces for every loop and conditional,
  spaces around operators and after commas, and blank lines between logical
  units. Keep lines at or below 120 characters (prefer below 110); wrapped
  lines use an additional eight spaces of indentation.
- Keep method names attached to `(` when wrapping calls, break at readable
  higher-level boundaries, and include `// Fallthrough` for intentional switch
  fallthrough.
- Put every class in a package, order imports consistently, and list imported
  classes explicitly rather than using wildcard imports. Attach array brackets
  to the type, initialize variables at declaration where practical, and keep
  variables in the smallest useful scope. Do not expose mutable class variables
  publicly.
- Write English comments using American spelling. Add descriptive Javadoc to
  every class and public method, except getters/setters, applicable inherited
  overrides, and test code. Javadoc summaries use a present-tense verb such as
  “Returns” or “Creates”, include useful parameter/return/throws details, use
  punctuation, and have no blank line between the Javadoc and its declaration.

## Review checklist

Before completing a Java change, inspect the complete changed source and test
files for these conventions, update high-value JUnit coverage for changed
behavior, and run the project’s Java 25 build/tests. Treat formatting tools as
helpers only: review their output for readable line wrapping and logical blank
lines.
