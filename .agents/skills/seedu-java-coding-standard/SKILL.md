---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java code change in this repository. The authoritative
source is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the Google Java Style Guide for topics that the SE-EDU guide does not cover.

- Use lowercase package names; PascalCase nouns for classes and enums; camelCase for variables and verb-based methods; SCREAMING_SNAKE_CASE for constants.
- Use English and American spelling in names and comments. Name booleans with readable prefixes such as `is`, `has`, `can`, or `should`; use plural names for collections.
- Use four spaces (never tabs), K&R braces, spaces around operators and after commas, blank lines between logical units, and lines no longer than 120 characters (prefer less than 110).
- Put every class in a package, order imports consistently, import classes explicitly, and attach array brackets to the type.
- Initialize variables at declaration when possible and keep them in the smallest scope. Do not expose class fields publicly except data-class fields and constants. Always use braces for loops and conditionals.
- Write descriptive Javadoc for public classes and public methods, except getters/setters, inherited overrides whose parent Javadoc applies exactly, and test code. Document non-obvious fields and methods as well.
- Add `// Fallthrough` whenever an intentional switch fall-through has no `break` statement.

Review the complete linked guide when a change involves a rule not summarized above, and preserve behavior while correcting style.
