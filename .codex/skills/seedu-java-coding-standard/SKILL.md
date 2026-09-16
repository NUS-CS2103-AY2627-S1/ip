---
name: seedu-java-coding-standard
description: Apply or audit the SE-EDU basic and intermediate Java coding standard when creating, editing, reviewing, or refactoring Java code in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
to all Java production and test code in this repository. Preserve behavior unless the user separately asks for
a behavior change. Use Google Java Style only for topics the SE-EDU standard does not cover.

## Naming

- Use lowercase package names rooted in the project name and logical subpackages.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, and camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Keep abbreviations and acronyms lowercase when embedded in a name.
- Use English names. Give large-scope variables descriptive names; short scratch/index names are acceptable
  only in small scopes. Reserve `j` and `k` for nested loops.
- Prefix boolean names with forms such as `is`, `has`, `was`, `can`, or `should` so they read as predicates.
  A boolean setter parameter should match the property name, such as `setFound(boolean isFound)`.
- Use plural names for collections and arrays.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout and statements

- Indent with 4 spaces, never tabs. Use K&R braces.
- Keep lines at or below 120 characters and aim for less than 110.
- Indent wrapped lines 8 spaces beyond the parent line. Break after commas and before operators or method-chain
  dots; keep a method name attached to its opening parenthesis. Prefer a higher-level break.
- Surround operators with spaces and put spaces after Java keywords, commas, and `for` semicolons.
- Separate logical units with blank lines where this improves readability.
- Put each class in a package. Keep imports minimal, explicit, and consistently grouped and ordered; never use
  wildcard imports.
- Attach array brackets to the type, for example `int[] values`.
- Declare variables in the smallest practical scope and initialize them at declaration when a real value exists.
  Do not expose mutable class fields publicly; public constants and behavior-free data classes are exceptions.
- Always use braces around loop and conditional bodies, with the conditional on its own line.
- In colon-style switches, indent cases one level and add `// Fallthrough` whenever an omitted `break` is
  intentional. Arrow-style switches do not need a fallthrough comment.

## Comments and JavaDoc

- Write comments in English with American spelling and without local slang.
- Write descriptive JavaDoc for all classes and public methods, except straightforward getters/setters,
  overrides whose inherited contract applies exactly, and test-only classes/methods. Also document non-trivial
  private methods when the contract is not obvious.
- Start JavaDoc with a short present-tense summary such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line, align each `*`, leave a blank line before tags, punctuate tag descriptions, and do
  not leave a blank line between the JavaDoc block and its declaration.
- Either document all parameters or omit all `@param` tags when every parameter is self-explanatory or already
  explained. Add `@return` and `@throws` where they provide information not obvious from the summary.
- Indent block comments with their surrounding code. Use comments for rationale and contracts, not narration
  that merely repeats the code.

## Workflow

1. Inspect the applicable `AGENTS.md` files and preserve project-specific requirements.
2. Before editing, identify affected naming, layout, import, statement, and documentation rules.
3. Apply the standard to all new or changed Java lines. When asked for a repository-wide cleanup, audit every
   Java file, including tests.
4. Search for hard-limit violations: lines over 120 characters, tabs, wildcard imports, missing packages,
   single-line conditionals, and braceless loops. Manually review naming, wrapping, scopes, comments, and imports.
5. Run the relevant Java 25 Gradle/JUnit and console tests. Formatting must not change behavior.
6. Report any deliberate exception with its reason instead of silently deviating from the standard.
