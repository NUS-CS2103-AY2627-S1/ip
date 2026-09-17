# SE-EDU intermediate Java rules

This is the project-local working summary of the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Follow Google Java Style for topics the SE-EDU guide does not specify.

## Naming

- Use lowercase package names under a project- or group-specific root.
- Name classes and enums with singular nouns in PascalCase.
- Name methods with verbs in camelCase.
- Name variables in camelCase and constants in `SCREAMING_SNAKE_CASE`.
- Do not capitalize an entire acronym inside an identifier. Prefer `XmlParser`
  to `XMLParser`.
- Use English names and avoid uncommon abbreviations.
- Give wider-scope variables more descriptive names. Restrict `i`, `j`, and
  `k` to short-lived indexes; use `j` or `k` only for nested loops.
- Phrase boolean names so they read as true or false, preferably with prefixes
  such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections and a shared prefix for related constants.
- JUnit test methods may use
  `featureUnderTest_testScenario_expectedBehavior` when a descriptive camelCase
  name would be unwieldy.

## Layout and formatting

- Indent with four spaces and never tabs.
- Keep lines within 120 characters; aim for about 110 where practical.
- Indent continuation lines by at least eight spaces beyond the parent line.
- Break after commas and before operators or dots. Keep a method name together
  with its opening parenthesis and prefer breaks at higher syntactic levels.
- Use K&R braces: opening brace on the same line and closing brace aligned with
  the construct. Put `else`, `catch`, and `finally` on the closing-brace line.
- Always use braces for `if`, `else`, loops, and similar control structures.
  Put the condition and body on separate lines.
- Add spaces around binary operators and after commas and control-flow
  keywords. Do not add spaces immediately inside parentheses.
- Use blank lines to separate logical units, not to pad code arbitrarily.
- Mark intentional `switch` fall-through with a clear comment.

## Java structure

- Put every class in a package and keep imports explicit; do not use wildcard
  imports. Use one consistent import ordering throughout the project.
- Attach array brackets to the type, for example `String[] names`.
- Declare variables in the smallest useful scope and initialize them at the
  declaration when practical.
- Do not expose public fields except constants or fields in a deliberate data
  class.
- Prefer standard Java control structures and idioms over clever equivalents.

## Comments and JavaDoc

- Write comments in clear American English. Explain intent, contracts, or
  non-obvious reasoning rather than restating code.
- Add descriptive header comments to public classes and public methods, except
  obvious getters and setters, exact inherited overrides, and tests where a
  comment would add no value.
- Use JavaDoc (`/** ... */`) for documentation comments. Start with a summary
  sentence and use proper capitalization and punctuation.
- Start a method summary with a third-person verb such as `Returns`, `Adds`, or
  `Creates`.
- Align JavaDoc lines consistently. Leave a blank line before block tags.
- Describe either all parameters or none. End `@param`, `@return`, and
  `@throws` descriptions with punctuation.
- Use `{@inheritDoc}` when inherited documentation completely describes an
  override.

## Scope discipline

- Apply these rules to lines and declarations touched by the current task.
- Fix nearby violations when the cleanup is small and directly related.
- Keep large, behavior-neutral formatting cleanups in a separate commit so
  functional diffs remain reviewable.
