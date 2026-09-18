---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when writing, changing, or reviewing Java source and tests in this project.
---

# SE-EDU Java coding standard

Use the [basic and intermediate standard](https://se-education.org/guides/conventions/java/intermediate.html).
Consult it for detailed cases; use its linked Google Java guide for uncovered topics.
SE-EDU overrides conflicting fallback rules.

## Review checklist

- Use lowercase packages, PascalCase type names, camelCase variables and verb-based
  methods, and SCREAMING_SNAKE_CASE constants. Keep acronyms in normal name case.
- Prefer English, descriptive names, plural collections, and boolean predicates
  such as `isDone`. Test names may use `method_scenario_expectedResult`.
- Use four-space indentation, eight additional spaces for continuations, and K&R
  braces. Aim below 110 columns; never exceed 120. Wrap after commas and before operators.
- Indent switch labels one level; mark intentional fallthrough. Always brace
  conditionals and loops. Separate logical blocks with one blank line.
- Keep imports explicit, ordered consistently, and free of unused entries.
  Attach array brackets to types. Declare variables near use with narrow scopes;
  do not expose mutable public fields.
- Write English comments with American spelling. Use multiline Javadoc for classes
  and public methods; getter/setter, inherited-documentation, and test exceptions
  follow the source. Start method summaries with third-person verbs. Punctuate tags;
  include all parameter descriptions or omit all when self-explanatory.

Preserve behavior during style cleanup. Run the repository's required tests;
do not claim automated compliance without an actual check.
