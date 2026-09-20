---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to this project’s Java source and tests.
---

# SE-EDU Java coding standard

Use this skill whenever adding, modifying, or reviewing Java code in this
repository. The authoritative source is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the Google Java Style Guide for topics it does not cover.

Apply these project rules:

- Keep packages lowercase; use PascalCase nouns for classes and enums,
  camelCase verbs for methods, camelCase for variables, and
  SCREAMING_SNAKE_CASE for constants. Use English names and boolean names
  that read as predicates (`is`, `has`, `can`, or `should`).
- Use four-space indentation, K&R braces, consistent explicit imports, and
  lines no longer than 120 characters (prefer less than 110). Wrap long lines
  at readable boundaries with wrapped indentation eight spaces deeper.
- Initialize variables at declaration when practical and keep them in the
  smallest useful scope. Use plural names for collections and do not expose
  mutable class fields publicly.
- Always use braces for loops and conditionals, including single statements.
  Put conditional bodies on separate lines and keep logical units separated by
  blank lines.
- Write descriptive English/American-English header Javadoc for every public
  class and public method. It may be omitted for getters/setters, or for an
  override when the parent Javadoc applies exactly; use `@inheritDoc` when
  appropriate. Start method summaries with a verb such as “Returns” or
  “Adds”, and document meaningful parameters, return values, and exceptions.
- For tests, use the naming form
  `featureUnderTest_testScenario_expectedBehavior()` where it improves clarity.

Before finishing a Java change, inspect the diff for these rules and run the
project’s relevant tests.
