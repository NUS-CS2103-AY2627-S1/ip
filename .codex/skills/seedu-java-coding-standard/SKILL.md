---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions when writing, reviewing, or refactoring Java code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java change in this repository. Preserve existing
behavior while applying the following conventions from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Naming

- Use lowercase package names, PascalCase nouns for classes and enums, and
  camelCase verbs for methods.
- Use camelCase for variables and `SCREAMING_SNAKE_CASE` for constants.
- Name booleans with prefixes such as `is`, `has`, `was`, or `can`; use
  `setX` for boolean setters.
- Use plural names for collections, English names, and normally cased
  abbreviations such as `Html` rather than `HTML`.
- Test methods may use the three-part form
  `featureUnderTest_testScenario_expectedBehavior`.

## Layout and statements

- Use four spaces for indentation and K&R braces; never use tabs for
  indentation.
- Keep lines at or below 120 characters, and aim for fewer than 110 where
  practical. Wrap continuations at readable, higher-level boundaries with
  eight-space continuation indentation.
- Put spaces around operators, after commas, and after Java keywords such as
  `if`, `for`, and `while`.
- Separate logical units in a block with one blank line.
- Always use braces for loop and conditional bodies, including one-line
  bodies. Keep conditional expressions and their bodies on separate lines.
- Use a consistent switch style. For colon-form switches, make intentional
  fall-through explicit and include `break` when a case can reach the next
  case; arrow-form switches are preferred when appropriate.

## Packages, imports, and variables

- Put every class in a package.
- Keep import ordering consistent within the project and list imported
  classes explicitly; do not use wildcard imports.
- Attach array brackets to the type, such as `String[] args`.
- Initialize variables at declaration when practical and keep them in the
  smallest scope possible.
- Do not expose class variables publicly except for constants or simple data
  classes with no behavior.

## Comments

- Write comments in English using American spelling and avoid local slang.
- Add descriptive Javadoc header comments to every public class and public
  method, except getters/setters, test code, and overridden methods where the
  inherited documentation applies exactly.
- Start Javadoc with a short summary sentence. Add `@param`, `@return`, and
  `@throws` details when they add useful information, and keep the block
  directly above the declaration.
- Document non-trivial private methods and fields when their purpose or
  behavior is not obvious.
- Indent comments consistently with the code they describe.

When reviewing or changing Java code, report any remaining deviations and
avoid unrelated formatting churn.
