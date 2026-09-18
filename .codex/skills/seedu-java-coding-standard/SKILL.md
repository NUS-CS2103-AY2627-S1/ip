---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java code change in this repository. Apply the SE-EDU basic + intermediate standard at https://se-education.org/guides/conventions/java/intermediate.html; use the Google Java Style Guide for topics it does not cover.

## Required checks

- Put every class in a lower-case project package (`oreo` and its subpackages here).
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase for variables, and `SCREAMING_SNAKE_CASE` for constants. Use boolean-sounding names (`is`, `has`, `can`, `should`, or `was`) and plural names for collections.
- Use four spaces, K&R braces, spaces around operators and after commas, blank lines between logical units, and a hard maximum line length of 120 characters. Wrap continuation lines with eight additional spaces.
- Keep imports explicit and consistently ordered; never use wildcard imports. Attach array brackets to the type. Initialize variables at declaration and keep them in the smallest possible scope.
- Always use braces for loops and conditionals, including one-line bodies. Put conditional bodies on separate lines and add `// Fallthrough` for intentional switch fallthrough.
- Write English comments using American spelling. Add descriptive Javadocs to public classes and public methods, except getters/setters, exact overrides, and test code. Begin method summaries with a present-tense verb such as “Returns” or “Creates”.
- Do not expose public mutable class fields; constants are exempt. A public field is acceptable only for a behaviorless data class.

When changing code, preserve behavior and update tests if behavior changes. Review the project UI test plan and run the required Java 25 checks after edits.
