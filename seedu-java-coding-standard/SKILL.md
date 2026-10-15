---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java basic and intermediate coding conventions to this project’s Java source and tests.
---

# SE-EDU Java coding standard

Use this standard for all Java code in this project. Apply the SE-EDU Java basic + intermediate rules:

- Use lowercase package names, PascalCase class names, camelCase variables and verb-based method names.
- Use SCREAMING_SNAKE_CASE for constants and boolean names that read like booleans (`is`, `has`, `can`, `should`).
- Use four spaces, K&R braces, explicit imports, and lines no longer than 120 characters (prefer under 110).
- Keep wrapped lines readable with an eight-space continuation indent; break after commas and before operators.
- Always use braces for loops and conditionals. Keep conditionals, method declarations, and statements readable rather than placing them on one line.
- Initialize variables near their declaration and use the smallest practical scope.
- Add descriptive Javadoc to public classes and public methods. Use a separate `/**` line, aligned `*` lines, a concise first sentence beginning with `Returns`, `Creates`, `Adds`, etc., and correctly punctuated `@param`, `@return`, and `@throws` tags when useful.
- Javadocs may be omitted for getters/setters, tests, and overridden methods when the parent documentation applies exactly; otherwise use `{@inheritDoc}` or a specific description.
- Write comments in English using American spelling and keep them aligned with the code.

For rules not covered here, follow the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) and the Google Java Style Guide.
