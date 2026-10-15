---
name: seedu-java-coding-standard
description: Apply the project's SE-EDU basic + intermediate Java conventions when creating, editing, refactoring, or reviewing Java source and tests.
---

# SE-EDU Java coding standard

Follow the [SE-EDU basic + intermediate Java standard](https://se-education.org/guides/conventions/java/intermediate.html).
For topics it does not cover, follow the Google Java Style Guide. Apply these rules to every Java change; for a
repository-wide compliance request, inspect every Java file.

The project's JavaDoc stretch requirement below is stricter than the official guide's getter and override exemptions
and takes precedence for production code.

## Naming

- Use lowercase package names, rooted in the project name and followed by logical groups.
- Name classes and enums with PascalCase nouns.
- Name methods with camelCase verbs.
- Name variables and parameters in camelCase, and constants in `SCREAMING_SNAKE_CASE`.
- Keep names in English. Write embedded acronyms as words, such as `exportHtml`, not `exportHTML`.
- Name booleans as predicates, normally using `is`, `has`, `can`, `was`, or `should`.
- Use plural names for collections and arrays.
- Use short scratch names such as `i` only in very small scopes; use descriptive names for wider scopes.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout

- Indent with 4 spaces, never tabs.
- Prefer lines below 110 characters and never exceed 120 characters.
- Indent wrapped lines 8 spaces beyond their parent. Break after commas and before operators or chained dots, choosing
  the highest-level readable break.
- Use K&R braces. Always use braces for loops and conditionals, including single-statement bodies, and place the body
  on a separate line.
- Put one statement per line.
- Put spaces around operators, after commas and control-flow keywords, and after semicolons in `for` statements.
- Separate logical units with one blank line.

## Packages, imports, and declarations

- Put every class in a package.
- Keep imports consistently ordered, explicit, minimal, and free of wildcards.
- Attach array brackets to the type, for example `String[] names`.
- Initialize variables where declared and declare them in the smallest useful scope.
- Do not expose mutable fields publicly; public fields are allowed only for constants or behaviorless data classes.
- Add `// Fallthrough` when a traditional `switch` case intentionally falls through.

## JavaDoc and comments

- Add JavaDoc to every non-private production class, enum, interface, constructor, and method.
- Add JavaDoc to non-trivial private methods and fields whose purpose or contract is not obvious.
- Use `{@inheritDoc}` for an override only when the inherited contract applies exactly.
- Start with a short summary sentence. Method summaries use third-person present tense, such as `Returns`, `Adds`, or
  `Sends`.
- Put `/**` on its own line, align subsequent `*` characters, leave one blank line before block tags, and leave no blank
  line before the documented declaration.
- Include either all useful `@param` tags or none when every parameter is already self-explanatory. Document meaningful
  return values and thrown exceptions. End tag descriptions with punctuation.
- Test classes and methods may omit header comments when descriptive names make their intent clear, as permitted by the
  SE-EDU standard.
- Write comments in clear American English, align them with the code they describe, and avoid comments that merely
  restate the implementation.

## Completion check

Before finishing, review every changed Java file against this checklist and correct any violations introduced or
exposed by the change.
