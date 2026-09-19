---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when reviewing or changing Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java change in this repository, including production code and tests. The authoritative source is the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html); use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics that the SE-EDU guide does not cover.

## Required conventions

- Keep package names lowercase; use PascalCase nouns for classes and enums, camelCase for variables and verb-based methods, and `SCREAMING_SNAKE_CASE` for constants.
- Name booleans like English predicates (`is`, `has`, `can`, `should`, or `was`); use plural names for collections. Keep abbreviations mixed case, such as `exportHtmlSource`.
- Use four spaces, K&R braces, spaces around operators and after commas, blank lines between logical units, and a 120-character hard line limit (prefer under 110). Wrap continuation lines with eight additional spaces and break at readable boundaries.
- Put every class in a package, order imports consistently, and import classes explicitly rather than using wildcard imports.
- Attach array brackets to the type (`String[] values`), initialize variables at declaration when practical, and keep variables in the smallest possible scope. Do not expose class fields publicly except constants or behaviorless data classes.
- Always use braces for loop and conditional bodies, even for one statement. Put conditional bodies on separate lines. Add `// Fallthrough` for intentional fall-through in a traditional `switch`.
- Write comments in English using American spelling. Add descriptive Javadocs to public classes and public methods, except getters/setters, exact overrides, and test code. Start method summaries with an action such as `Returns`, `Adds`, or `Creates`; include useful `@param`, `@return`, and `@throws` details.

## Review workflow

Before finishing a Java change, inspect all touched Java files for naming, layout, imports, braces, visibility, variable scope, and public API documentation. Preserve behavior while making formatting-only changes. Run the project’s Java 25 build and tests after edits.
