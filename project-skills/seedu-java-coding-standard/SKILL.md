---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java production and test code in this project.
---

# SE-EDU Java coding standard

Apply these rules to every Java change in this repository. The authoritative source is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics not covered there.

- Use lowercase package names; PascalCase noun names for classes and enums; camelCase names for variables and verb-based methods.
- Use SCREAMING_SNAKE_CASE for constants. Name booleans with forms such as `is`, `has`, `was`, or `can`; use plural names for collections and English names throughout.
- Use four spaces, K&R braces, explicit imports, type-attached array brackets, initialized variables in the smallest practical scope, and braces for every loop and conditional body.
- Keep lines at or below 120 characters (prefer below 110); wrap readable continuations with an additional eight spaces and separate logical units with blank lines.
- Keep imports consistently ordered and never use wildcard imports.
- Keep class fields non-public unless the class is a behavior-free data class or the field is a constant.
- Add descriptive English-American-spelling Javadoc headers to public classes and public methods, except getters/setters, applicable overrides, and test code. Use a short first-sentence summary and correctly punctuated `@param`, `@return`, and `@throws` tags where useful.
- Before finishing a Java change, inspect the diff for these rules and run the project’s required tests.
