---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to all Java code in this project.
---

# SE-EDU Java coding standard

Apply this skill to every Java source and test change in this repository.

- Use lowercase package names; PascalCase nouns for classes and enums; camelCase verbs for methods and variables; and SCREAMING_SNAKE_CASE for constants. Keep names in English, use boolean prefixes such as `is`, `has`, or `can`, and use plural names for collections.
- Use four spaces, K&R braces, a 120-character hard line limit (prefer 110), readable wrapped-line breaks, spaces around operators and after commas, and blank lines between logical units.
- Keep imports explicit and consistently ordered. Attach array brackets to the type. Initialize variables at declaration where practical and keep them in the smallest scope. Do not expose class fields publicly except constants or behavior-free data classes.
- Always brace loop and conditional bodies, and put conditional bodies on their own lines. Mark intentional switch fallthrough with `// Fallthrough`.
- Write English, American-spelled comments. Add descriptive Javadoc to every public class and public method, except getters/setters, exact overrides, and test code. Follow standard Javadoc structure with a summary sentence and relevant `@param`, `@return`, and `@throws` tags.
- Use the project’s existing behavior and APIs unless a standard violation requires a compatible rename or formatting change.

The source of these rules is the SE-EDU Java coding standard (basic + intermediate):
https://se-education.org/guides/conventions/java/intermediate.html

For topics not covered here, follow the Google Java Style Guide referenced by that standard.
