---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding conventions when creating, reviewing, or modifying Java code in this project.
---

# SE-EDU Java Coding Standard

Apply the conventions from https://se-education.org/guides/conventions/java/intermediate.html to all project Java code.

- Keep `src/main/java` as the source root; put every class in a meaningful package.
- Use descriptive lowerCamelCase names for variables and methods, UpperCamelCase names for classes, and UPPER_SNAKE_CASE for constants.
- Use four spaces for indentation, K&R braces, spaces around operators, and blank lines between logical units.
- Keep lines at 120 characters or fewer; wrap long expressions at readable boundaries with continuation indentation.
- Use explicit, minimal imports in a consistent order; do not use wildcard imports.
- Always use braces for `if`, `else`, `for`, `while`, and similar control-flow bodies.
- Initialize variables at declaration when practical and keep them in the smallest useful scope.
- Keep fields encapsulated; avoid public mutable fields.
- Write descriptive Javadoc header comments for public classes and methods, including useful `@param`, `@return`, and `@throws` information.

When reviewing or changing code, check the affected files against these rules and preserve behavior unless a style correction requires otherwise.
