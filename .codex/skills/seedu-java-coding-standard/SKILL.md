---
name: seedu-java-coding-standard
description: Apply the required SE-EDU basic and intermediate Java coding rules when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the basic and intermediate rules in the official
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
for every Java change in this project. The
[CS2103T standards page](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html)
makes those two levels required. Treat advanced rules as optional unless the user or
project instructions explicitly require them. For topics the SE-EDU standard does not
cover, follow the Google Java Style Guide as directed by the official standard.

When reviewing code, distinguish a required-rule violation from an optional advanced
rule or a general code-quality preference.

## Naming

- Use lowercase package names and PascalCase noun names for classes and enums.
- Use camelCase verb names for methods and camelCase names for variables.
- Use SCREAMING_SNAKE_CASE for constants and common prefixes for related constants.
- Keep names in English. Write embedded abbreviations and acronyms as words, such as
  `exportHtmlSource`, rather than in all capitals.
- Give wide-scope variables descriptive names; short scratch names are acceptable in
  small scopes. Reserve `j`, `k`, and similar iterator names for nested loops.
- Name boolean variables and methods so they read as predicates, preferably beginning
  with `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections. Test methods may use the documented underscore
  format `featureUnderTest_testScenario_expectedBehavior`.

## Layout

- Indent with 4 spaces and never tabs. Keep lines below 110 characters where practical
  and never exceed 120 characters.
- Indent wrapped lines 8 spaces beyond their parent. Break after commas and before
  operators, including operator-like dots and pipes, when wrapping.
- Use K&R braces and the standard layouts for methods, conditionals, loops,
  `switch` statements, and `try`/`catch`/`finally` blocks.
- Put spaces around operators and after commas and semicolons where appropriate. Put a
  space after Java keywords such as `if`, `for`, and `while`.
- Separate logical units within a block with a blank line when it improves readability.

## Statements

- Put every class in a logical package. Keep import ordering consistent, list imports
  explicitly, and remove unused imports.
- Attach array brackets to the type, as in `String[] arguments`.
- Declare variables in the smallest practical scope and initialize them at declaration
  when a valid initial value is available.
- Do not expose mutable class variables publicly unless the class is a behavior-free
  data class. Constants are exempt.
- Always use braces around loop and conditional bodies, including single statements.
- Mark intentional traditional-switch fallthrough with `// Fallthrough`.

## Comments and Javadoc

- Write comments in English using American spelling and avoid local slang.
- Add descriptive header comments to every class and public method, except where the
  official standard permits omission for straightforward getters/setters, inherited
  override documentation that applies exactly, and test code.
- Describe intended behavior rather than implementation mechanics. Start a method
  summary with an action such as `Returns`, `Adds`, or `Creates`.
- Format Javadocs as specified by the official standard: align the stars, separate the
  description from tags with a blank line, punctuate tag descriptions, and place no
  blank line between the documentation block and its declaration.
- Include all `@param` tags or omit all of them when every parameter is self-explanatory.
  Add `@return` and `@throws` information when it adds information not already clear
  from the summary.
