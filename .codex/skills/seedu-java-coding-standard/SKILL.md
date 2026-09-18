---
name: seedu-java-coding-standard
description: Review and update this project's Java code against the SE-EDU basic and intermediate Java conventions.
---

# SE-EDU Java coding standard

Apply these conventions to all Java production and test code in this project.
For topics not listed here, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Review checklist

- Put every class in a lower-case package. Use nouns in `PascalCase` for classes,
  verbs in `camelCase` for methods, `camelCase` for variables, and
  `SCREAMING_SNAKE_CASE` for constants.
- Name booleans with readable prefixes such as `is`, `has`, or `was`; use
  plural names for collections. Test methods may use
  `featureUnderTest_testScenario_expectedBehavior`.
- Use four spaces for indentation, K&R braces, explicit imports, and a
  consistent import order. Do not use wildcard imports.
- Keep lines at or below 120 characters (prefer below 110). Indent wrapped
  lines by eight spaces beyond the parent line and keep method names attached
  to their opening parenthesis.
- Initialize variables near their declaration and keep them in the smallest
  useful scope. Always use braces for loop and conditional bodies, including
  single-statement bodies; make intentional switch fall-through explicit.
- Write descriptive English JavaDoc for every public class and method. Getters,
  setters, and overrides may inherit a suitable parent comment. Document
  non-trivial private members and methods when their purpose is not obvious.

Before handing back Java changes, inspect the complete diff for these rules and
correct violations without changing behavior unless the request requires it.

Source: https://se-education.org/guides/conventions/java/intermediate.html
