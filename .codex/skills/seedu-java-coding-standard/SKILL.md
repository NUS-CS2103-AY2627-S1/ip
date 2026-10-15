---
name: seedu-java-coding-standard
description: Review and write Java code in this project using the SE-EDU basic + intermediate coding standard.
---

# SE-EDU Java coding standard

Apply this skill to every Java source change and code review in this project. The authoritative reference is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google Java Style Guide for topics not covered there.

Check the following before considering Java work complete:

- Use lowercase package names rooted in the project name, PascalCase nouns for classes/enums, camelCase verbs for methods, camelCase variables, and SCREAMING_SNAKE_CASE constants. Use English names, descriptive names for larger scopes, boolean names such as `isDone`/`hasData`, and plural names for collections.
- Use four-space indentation, K&R braces, spaces around operators and after keywords/commas, logical blank lines, and a 120-character hard line limit (prefer under 110). Wrap long lines with readable breaks and eight-space continuation indentation.
- Put every class in a package; keep imports explicit and consistently ordered. Never use wildcard imports. Put array brackets on the type (`int[] values`).
- Initialize variables at declaration when possible and keep them in the smallest scope. Do not expose class fields publicly except constants or behaviorless data classes. Always use braces for loops and conditionals, including one-line bodies; keep conditional bodies on separate lines. Mark intentional switch fallthrough with `// Fallthrough`.
- Write English comments using American spelling. Add descriptive Javadoc to every public class and public method, except getters/setters, exact overrides, and test code. Javadoc should explain behavior and document parameters, return values, and thrown exceptions where relevant.

When reviewing code, report findings with absolute file paths and line numbers, explain why each conflicts with the standard, and distinguish definite violations from reasonable style improvements. Do not make unrelated refactors merely to reformat code.
