---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for every production or test Java change in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
to all Java code created, edited, or reviewed in this project. Use the Google Java Style Guide for
topics the SE-EDU standard does not cover.

## Required workflow

1. Before editing Java, inspect the affected files for existing violations that overlap the change.
2. Write new and changed production and test code to this standard.
3. Correct overlapping violations without changing program behavior. Do not broaden a focused
   task into an unrelated repository-wide refactor unless the user requests a full audit.
4. Run Checkstyle and the required project tests after code changes.

## Rules to enforce

- Use lowercase package names, PascalCase noun names for classes and enums, camelCase verb names
  for methods, camelCase variable names, and SCREAMING_SNAKE_CASE constant names. Keep names in
  English; name every boolean field, local variable, parameter, and method to read as a predicate.
  Prefer prefixes such as `is`, `has`, `can`, `should`, or `was` (for example, `isDone`,
  `hasTime`, and `shouldMarkDone`) and avoid action-like names such as `markDone`. Use plural names
  for collections.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Indent with four spaces and never tabs. Keep lines at or below 120 characters, preferably below
  110. Indent wrapped lines eight spaces beyond their parent; break after commas and before
  operators, preferring higher-level breaks.
- Use K&R braces. Always brace loop and conditional bodies and place their statements on separate
  lines. Add `// Fallthrough` to an intentionally falling-through switch case.
- Put every class in a package. Use explicit imports and a consistent ordering: static imports,
  Java/Jakarta imports, third-party imports, then project imports, separated into groups.
- Attach array brackets to the type. Declare variables in the smallest useful scope and initialize
  them at declaration when a valid value is available. Do not expose mutable class fields publicly.
- Surround operators with spaces, add spaces after Java keywords, commas, and `for` semicolons,
  and separate logical blocks with blank lines.
- Write comments in English with American spelling. Add descriptive Javadoc to every public class,
  constructor, and method, except obvious getters/setters, test code, and exact overrides. Start a
  method summary with a third-person verb such as “Returns”, “Creates”, or “Adds”; punctuate tag
  descriptions and include either all useful `@param` tags or none.

When a rule is ambiguous, consult the authoritative page rather than inventing a local convention.
