---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java code change in this project. Follow the SE-EDU basic and intermediate rules; use the Google Java Style Guide for topics not covered here.

## Required conventions

- Keep package names lowercase; use PascalCase nouns for classes and camelCase for methods and variables.
- Use SCREAMING_SNAKE_CASE for constants and boolean names that read as booleans, such as `isDone` or `hasData`.
- Use English and American spelling in identifiers and comments. Avoid uppercase acronyms inside names.
- Indent with four spaces, use K&R braces, and keep lines at most 120 characters. Prefer line lengths below 110 characters and indent wrapped lines by eight spaces.
- Separate logical units with one blank line. Keep variables initialized in the smallest valid scope.
- Put every class in a package, use explicit imports, keep import ordering consistent, and never use wildcard imports.
- Always use braces for loops and conditionals, including single-statement bodies. Do not expose mutable class fields publicly.
- Add descriptive Javadoc to public classes and methods. A summary sentence should describe the behavior; document parameters and exceptions when they add value. Getters, setters, exact overrides, and test methods may omit redundant comments.

For details and examples, consult the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
