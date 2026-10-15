# SE-EDU Java Coding Standard

This repository follows the basic and intermediate conventions described in
the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
The rules below are a project-focused checklist. Use the Google Java Style
Guide as the fallback for cases not addressed here.

## Naming

- Use lowercase package names. Package the project under a suitable root name.
- Name classes and interfaces with PascalCase nouns.
- Name methods and variables with camelCase. Method names should normally be
  verbs or verb phrases.
- Name constants with uppercase words separated by underscores.
- Give boolean variables and methods predicate-style names such as `isDone`,
  `hasNextCommand`, or `canSave`.
- Use plural names for variables that represent collections.
- Avoid abbreviations, single-letter names outside small conventional loops,
  and names that encode a variable's type.

## Layout and whitespace

- Indent with four spaces and do not use tabs.
- Treat 110 characters as the preferred line length and 120 as the hard limit.
- Indent wrapped continuation lines by at least eight additional spaces.
- Break after commas and before operators when wrapping expressions. Keep
  related operators aligned when that improves readability.
- Use K&R braces: put the opening brace on the declaration or control line and
  the closing brace on its own line.
- Always use braces for conditionals and loops, including one-line bodies.
- Put spaces around binary and ternary operators, after commas and semicolons,
  and between a control keyword and its opening parenthesis.
- Do not add spaces immediately inside parentheses or array brackets.
- Separate logical sections with a blank line, but avoid repeated or decorative
  blank lines.

## Packages and imports

- Put every class in a package.
- Use explicit, minimal imports rather than wildcard imports.
- Keep imports ordered consistently: static imports first when present, then
  standard-library, third-party, and project imports in logical groups.
- Put array brackets with the type, for example `String[] arguments`.

## Variables and encapsulation

- Declare variables in the smallest practical scope and initialize them near
  their declarations.
- Make fields private unless a wider access level is genuinely needed.
- Do not expose mutable state through public fields. Public fields should be
  limited to intentional constants or simple data structures.
- Prefer clear named constants over unexplained repeated literals.

## Control flow

- Keep conditions direct and readable. Avoid redundant comparisons of boolean
  values with `true` or `false`.
- In traditional `switch` statements, make case termination explicit with
  `break`, `return`, or a `// Fallthrough` comment. Arrow-form cases may be
  indented normally.
- Keep methods focused. Extract non-trivial sections when that gives them a
  meaningful name and makes the original method easier to understand.

## Comments and Javadoc

- Write comments in clear English with consistent spelling. Explain intent or
  non-obvious reasoning rather than restating the code.
- Use Javadoc for public classes and non-trivial public methods. Simple getters,
  setters, overrides, and test methods may omit it when their purpose is clear.
- Start a Javadoc block with `/**`, place the summary on the next line, and end
  sentences with punctuation.
- Use `@param`, `@return`, and `@throws` where applicable. Capitalize and
  punctuate their descriptions consistently.
- Put short implementation comments on the line before the code they explain.
  Use `//` for ordinary comments and reserve block comments for longer notes.

## Final review checklist

- Names communicate intent and use the prescribed capitalization.
- Indentation, wrapping, braces, spacing, and blank lines are consistent.
- Imports are explicit, minimal, grouped, and ordered.
- Fields have the narrowest useful visibility.
- Public APIs and non-obvious logic have useful, grammatical Javadocs.
- No comment merely narrates an obvious statement.
- Tests follow the same source conventions as application code.
