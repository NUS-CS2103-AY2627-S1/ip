---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java basic and intermediate coding conventions when reviewing or changing Java code.
---

# SEEDU Java Coding Standard

Use this skill for every Java code change in the target project. Follow the SE-EDU Java coding standard (basic + intermediate rules):

- Use lowercase package names; PascalCase nouns for classes/enums; camelCase for variables and verb methods; SCREAMING_SNAKE_CASE for constants.
- Keep names in English, avoid uppercase acronyms, use descriptive names for large scopes, boolean-sounding names for boolean values/methods, and plural names for collections. Keep iterator names such as `i`, `j`, and `k` limited to loop indices.
- Use four spaces, K&R braces, braces for every loop and conditional body, and consistent whitespace around operators, commas, colons, and `for` separators.
- Keep lines at or below 120 characters (prefer below 110); wrap at readable boundaries with wrapped lines indented by an additional eight spaces.
- Keep logical units separated by one blank line and use consistent import ordering with explicit imports only. Put array brackets on the type, initialize variables at declaration when possible, keep declarations in the smallest scope, and avoid public mutable class fields.
- Add descriptive Javadoc headers to every non-test class and public method, except getters/setters and overrides whose inherited documentation applies. Start summaries with forms such as "Creates", "Returns", "Adds", or "Displays"; use aligned `*` lines, blank lines before tags, and punctuation for parameter descriptions.
- Write comments in English using American spelling, avoid slang, and indent comments with the surrounding code. Mark intentional switch fallthrough with `// Fallthrough`.

For topics not covered here, consult the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) and its linked Google Java Style Guide. Preserve behavior and user-facing output while applying the standard. After changes, inspect all affected Java files and run the project's normal tests and UI validation requirements.
