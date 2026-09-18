---
name: seedu-java-coding-standard
description: Apply the project's SE-EDU Java coding standard when writing, modifying, or reviewing Java production and test code in this repository.
---

# SE-EDU Java Coding Standard

Keep Java changes consistent with the
[SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
For a topic that guide does not cover, use the Google Java Style Guide. Preserve
program behavior when a task asks only for coding-standard conformance.

## Apply the standard

1. Inspect every Java file in the requested scope before editing. Distinguish a
   genuine violation from a personal preference.
2. Make the smallest focused edits that correct confirmed violations. Do not mix
   in feature work or opportunistic refactoring.
3. Recheck production and test code for the rules below. Preserve useful existing
   Javadoc, adjusting it only where the standard requires.
4. Review `test/ui-test-plan.md`. Update it only if observable console behavior
   changes, then follow the project's required Java 25 test workflows in
   `AGENTS.md`.

## Naming

- Use lowercase package names and group classes into logical `toothless.*`
  packages.
- Name classes and enums with PascalCase nouns. Name methods with camelCase verbs.
  Use camelCase for variables and SCREAMING_SNAKE_CASE for constants.
- Treat acronyms as ordinary words inside identifiers, such as `parseHtml`, not
  `parseHTML`.
- Give booleans names that read as true-or-false statements, normally beginning
  with `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections. Keep broad-scope names descriptive; reserve
  short names such as `i` for small local scopes and loop indices.
- Test methods may use
  `featureUnderTest_testScenario_expectedBehavior`, omitting later parts only
  when they add no information.

## Layout

- Indent with four spaces and never tabs. Use K&R braces.
- Aim for lines below 110 characters and never exceed 120 characters.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas,
  before operators or method-chain dots, and at the highest readable expression
  level. Keep a method name attached to its opening parenthesis.
- Put spaces around operators, after commas and control-flow keywords, and after
  semicolons in `for` headers.
- Separate logical units with one blank line. Indent `case` and `default` labels
  one level inside their `switch`, and indent their statements one further level.

## Declarations and imports

- Put every class in a package. List imports explicitly; never use wildcard
  imports. Remove unused imports.
- Keep import groups consistent with this project: static imports, Java imports,
  third-party imports, then `toothless` imports, with one blank line between
  groups and alphabetical order within a group.
- Attach array brackets to the type, for example `String[] args`.
- Declare and initialize variables in the smallest useful scope. Do not invent a
  placeholder value when no valid initial value exists.
- Keep fields non-public except genuine constants or behavior-free data objects.

## Statements

- Always use braces around loop and conditional bodies, including single-line
  bodies. Put the body on its own line.
- Format `if`/`else`, loops, `try`/`catch`/`finally`, and methods with their
  opening brace on the declaration line.
- Add `// Fallthrough` when a traditional switch case intentionally reaches the
  next case. Prefer arrow cases when they express the logic clearly.

## Comments and Javadoc

- Write comments in clear English using American spelling and indent them with
  the code they describe.
- Give every production class and public method descriptive Javadoc, except
  obvious getters/setters and exact-behavior overrides. Test classes and methods
  are exempt, though retained Javadoc must still be well formed.
- Start a method summary with a third-person verb such as `Returns`, `Adds`, or
  `Parses`. End summary sentences and every `@param` description with
  punctuation.
- Put `/**` on its own line, align each `*`, leave one blank Javadoc line before
  block tags, and do not leave a blank line between Javadoc and its declaration.
- Include `@param` tags for all parameters or omit all of them when every name is
  self-explanatory. Include useful `@return` and `@throws` details without merely
  repeating the signature.
