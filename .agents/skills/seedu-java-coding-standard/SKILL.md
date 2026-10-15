---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard whenever creating, editing, formatting, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the basic and intermediate rules in the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the Google Java Style Guide for topics the SE-EDU standard does not cover.

## Naming

- Use lowercase package names, PascalCase noun names for classes and enums,
  camelCase verb names for methods, and camelCase names for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Use English names. Keep abbreviations within names lowercase.
- Name booleans so they read as boolean conditions, preferably with prefixes
  such as `is`, `has`, `was`, or `should`.
- Use plural names for collections. Reserve short iterator names such as `i`
  for small loop scopes.
- Name tests using `featureUnderTest_testScenario_expectedBehavior` when the
  three-part form helps explain the test.

## Layout

- Indent with four spaces, never tabs. Keep lines at or below 120 characters
  and aim for fewer than 110.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas
  and before operators when wrapping.
- Use K&R braces. Always use braces around loop and conditional bodies.
- Indent `case` labels one level inside a switch and their statements another
  level. Mark intentional fall-through with `// Fallthrough`.
- Surround operators with spaces, put spaces after commas and reserved words,
  and separate logical units with blank lines.

## Declarations and statements

- Put every class in a package and list imports explicitly in a consistent
  order; do not use wildcard imports.
- Attach array brackets to the type.
- Initialize variables at declaration when possible and declare them in the
  smallest useful scope.
- Do not expose mutable class fields publicly. Preserve information hiding.

## Comments and Javadoc

- Write comments in English using American spelling.
- Add descriptive Javadoc headers to all classes and public methods, except
  self-explanatory getters/setters, inherited overrides, and test code where
  the standard permits omission.
- Start Javadoc with a short summary sentence. Keep the opening `/**` on its
  own line, align each `*`, and leave a blank line before block tags.
- If one parameter needs an `@param` tag, document all parameters. End
  `@param`, `@return`, and `@throws` descriptions with punctuation.
- Indent comments to match the code they describe.

After editing Java, review the diff against these rules before running the
project's required JUnit and UI test suites.
