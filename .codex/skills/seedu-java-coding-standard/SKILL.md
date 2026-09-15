---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Apply these rules to all Java production and test code in this project. Use the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the authority. For topics it does not cover, follow the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

When existing code conflicts with these rules, keep behavioral changes separate from style-only changes whenever
practical. Preserve the project's public behavior unless the task explicitly requires changing it.

## Naming

- Write package names in lowercase. For a school project, use the project or group name as the root package.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase names for variables.
- Use SCREAMING_SNAKE_CASE for constants. Give related constants a common prefix.
- Use the test method format `featureUnderTest_testScenario_expectedBehavior`; omit the second or third part only
  when the test genuinely covers the broader scope.
- Treat abbreviations and acronyms as words within names, such as `exportHtmlSource`, not `exportHTMLSource`.
- Write all names in English.
- Give wide-scope variables descriptive names. Short scratch names such as `i`, `j`, and `k` are acceptable only
  within a few lines; reserve `j` and later letters for nested loops.
- Name booleans to read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`.
  Name boolean setters in the form `setFound(boolean isFound)`.
- Use plural names for collections and arrays.

## Layout

- Indent with 4 spaces and never tabs.
- Aim for fewer than 110 characters per line and never exceed 120 characters.
- Indent wrapped lines 8 spaces beyond the parent line. Wrap after commas and before operators, including `.`, `&`,
  and `|`. Keep a method or constructor name attached to its opening parenthesis and prefer higher-level breaks.
- Use K&R braces: place an opening brace at the end of its statement and the closing brace on its own line.
- When a method or constructor declares exceptions, place the `throws` clause on the next line and indent it
  8 spaces beyond the declaration's indentation.
- Always use braces around loop and conditional bodies, including single statements. Put the body on a separate line.
- Format `if`/`else`, `for`, `while`, `do`/`while`, `switch`, and `try`/`catch`/`finally` consistently with K&R
  braces. Add `// Fallthrough` when a colon-style `switch` case intentionally falls through.
- Surround binary and ternary operators with spaces. Add a space after Java keywords, commas, and semicolons in
  `for` headers. Surround a ternary colon with spaces.
- Separate logical units within a block with one blank line.

## Packages, Imports, Types, and Variables

- Put every class in a package.
- Keep import ordering consistent. Group static imports first, then `java`, `javax`, and other imports as appropriate
  for the project, with blank lines between groups.
- Import classes explicitly. Do not use wildcard imports, and remove unused imports.
- Attach array brackets to the type, as in `int[] values`.
- Declare variables in the smallest useful scope and initialize them at declaration when a valid initial value exists.
- Do not expose class variables publicly unless the class is a behavior-free data class. Public constants are allowed.

## Comments and Javadoc

- Write comments in English using American spelling and no local slang.
- Add descriptive Javadoc to every class and public method. Javadoc may be omitted for getters/setters, test methods,
  and overrides when the inherited documentation applies exactly.
- Start a method Javadoc summary with an active third-person verb such as `Returns`, `Sends`, or `Adds`.
- Put `/**` and `*/` on separate lines for normal Javadoc blocks. Align each `*`, put one space after it, and do not
  leave a blank line between the Javadoc and its declaration.
- Keep the first sentence as a short summary. Separate the description from tags with one blank line.
- End parameter descriptions with punctuation. Include `@param` tags for all parameters or none; omit all only when
  every parameter is self-explanatory or already explained by the description.
- Omit `@return` for `void` methods or when the return value is obvious from the description. Document thrown
  exceptions when that information is useful to callers.
- Use `{@inheritDoc}` when an override needs to supplement inherited documentation.
- Indent comments with the code they describe. Trailing comments are allowed when they remain clear.

## Review Checklist

Before completing a Java change:

1. Inspect every changed Java file against the rules above.
2. Check all Java files for tabs, wildcard imports, trailing whitespace, and lines longer than 120 characters.
3. Confirm that names, braces, wrapping, whitespace, and public API Javadocs comply.
4. Follow the project's required build and UI-test workflow after code changes.
