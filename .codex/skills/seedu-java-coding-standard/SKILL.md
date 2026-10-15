---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU Java coding standard (basic and intermediate rules) when creating, editing, refactoring, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Apply the standard

- Use lowercase logical package names; PascalCase noun names for classes and enums; camelCase verb names for methods; camelCase variables; and SCREAMING_SNAKE_CASE constants.
- Give booleans names that read as predicates, collection variables plural names, and larger-scope variables more descriptive names. Test names may use `feature_scenario_expectedBehavior`.
- Indent with four spaces and no tabs. Keep lines under 120 characters, preferably under 110. Indent continuations eight spaces beyond their parent and break after commas or before operators.
- Use K&R braces. Always brace loop and conditional bodies, place conditional bodies on separate lines, and mark intentional switch fall-through with `// Fallthrough`.
- Put every class in a package. Use consistent, explicit, minimal imports; never wildcard imports.
- Attach array brackets to the type. Declare variables in the smallest useful scope and initialize them at declaration when a valid value is available. Do not expose mutable class fields publicly.
- Separate logical units with blank lines without adding decorative whitespace.
- Write comments in clear English using American spelling. Add descriptive JavaDoc to public classes and methods except self-evident accessors, exact inherited overrides, and test code.
- Format JavaDoc with a short first-sentence summary, aligned `*` characters, a blank line before tags, and punctuation after tag descriptions. Include either all useful `@param` tags or none.

## Review workflow

Inspect every changed Java line and its surrounding declaration. Correct violations within the requested scope, preserve behavior unless behavior changes are explicitly requested, and run the repository's required tests after edits.
