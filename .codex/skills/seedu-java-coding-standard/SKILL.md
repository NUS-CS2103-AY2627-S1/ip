---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to production and test Java code in this repository.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic + intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to every Java source change in this repository. For topics it does not cover, follow the Google Java Style Guide.

## Required checks

- Use English, PascalCase class and enum names, camelCase variables and verb-based method names. Boolean names begin with `is`, `has`, `was`, `can`, or a similarly boolean phrase; collection names are plural.
- Use four spaces for indentation, K&R braces, braces around every conditional and loop body, and logical blank lines between units of work. Keep lines under 120 characters (prefer 110); wrap readable continuations eight spaces past the parent indentation, generally after commas or before operators.
- Put all types in packages. Keep imports explicit, minimal, and consistently grouped: static imports, `java`, `javax`, third-party imports, each group separated by one blank line.
- Declare and initialise variables in their smallest useful scope; do not expose mutable public fields.
- Write English Javadoc for public classes and public methods, except simple getters/setters, tests, and exact overrides. Use a short third-person summary sentence and useful `@param`, `@return`, and `@throws` tags.

Before finalising Java changes, inspect the changed code for these rules. Update existing code only where a rule requires a change; preserve observable behaviour unless the user requests a behaviour change.
