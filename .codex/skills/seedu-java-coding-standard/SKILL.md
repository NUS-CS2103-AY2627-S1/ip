---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, modifying, or reviewing Java code in this project. Use whenever a task touches a .java file; do not invoke for changes that have no Java code.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
for every Java file in this repository. Treat the rules below as mandatory. For topics the SE-EDU standard does
not cover, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html), as directed by
the SE-EDU standard.

Preserve behavior while applying style fixes unless the user also requests a behavior change.

## Naming

- Use lowercase package names. Use the project or group name as the root package, followed by logical groups.
- Use English noun names in `PascalCase` for classes and enums.
- Use English verb names in `camelCase` for methods, and `camelCase` for variables.
- Use `SCREAMING_SNAKE_CASE` for constants. Give associated constants a common prefix.
- Use the test method form `featureUnderTest_testScenario_expectedBehavior`; omit later parts only when they add no
  useful distinction.
- Treat abbreviations and acronyms as words inside names, for example `exportHtmlSource` and `openDvdPlayer`.
- Give wider-scope variables more descriptive names. Reserve short scratch names such as `i`, `j`, and `k` for
  small scopes and loop indices; use `j` and later letters only for nested loops.
- Name booleans to read as booleans, preferably with prefixes such as `is`, `has`, `can`, `was`, or `should`.
  A boolean setter takes a correspondingly named parameter, for example `setFound(boolean isFound)`.
- Use plural names for collections and arrays of objects.

## Layout

- Indent with 4 spaces, never tabs.
- Prefer lines under 110 characters and never exceed 120 characters.
- Indent continuation lines 8 spaces beyond the parent line. Break after commas and before operators, including
  dots used for chained calls. Keep a method or constructor name attached to its opening parenthesis.
- Use K&R braces: put an opening brace on the statement line and a closing brace on its own line. Write `else`,
  `catch`, and `finally` on the same line as the preceding closing brace.
- Indent `case` and `default` labels one level inside a `switch`, and indent their statements one further level.
- Put spaces around binary and ternary operators, after Java keywords, after commas, and after semicolons in a
  `for` header.
- Separate logical units inside a block with one blank line.

## Statements and declarations

- Put every class in a package.
- List imports explicitly; never use wildcard imports. Keep the project's import grouping consistent: static
  imports, then `java` imports, then third-party imports, with a blank line between groups and alphabetical order
  within each group.
- Attach array brackets to the type, for example `int[] values`.
- Initialize variables where they are declared and declare them in the smallest useful scope. Do not use fake
  placeholder values when a valid initial value is unavailable.
- Do not expose class variables publicly unless the class is a behavior-free data class; constants are exempt.
- Always use braces around loop and conditional bodies, including single statements. Put the condition and body
  on separate lines.
- Add `// Fallthrough` when a colon-style `switch` case intentionally continues into the next case.

## Comments and Javadoc

- Write comments in English using American spelling and no local slang.
- Add descriptive Javadoc to every class and public method, including public constructors. It may be omitted for
  getters and setters, test code, or an override when the inherited Javadoc applies exactly.
- Start Javadoc with a short summary sentence using third-person verb form such as `Returns`, `Adds`, or `Creates`.
- Put `/**` and `*/` on their own lines. Align leading asterisks, leave one blank Javadoc line before block tags,
  punctuate parameter descriptions, and place no blank line between Javadoc and the declaration.
- Include `@param` for either all parameters or none, depending on whether the tags add useful information. Include
  `@return` and `@throws` when they add information not already obvious from the summary.
- Indent implementation comments with the code they describe.

## Completion check

Before finishing a Java change:

1. Review every touched Java file against all sections above, including test code where exemptions do not apply.
2. Search for tabs, wildcard imports, and lines over 120 characters.
3. Check public APIs for required Javadoc and inspect names manually for scope, boolean, collection, and acronym rules.
4. Run the project validations required by `AGENTS.md`.
