---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when adding, modifying, or reviewing Java code in this project.
---

# Apply the SE-EDU Java coding standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

When changing Java code:

- Use English names and comments. Use lowercase packages, PascalCase noun class and enum names, camelCase verb method names, camelCase variables, and SCREAMING_SNAKE_CASE constants.
- Name booleans like boolean values and collections in the plural. Keep names proportionate to their scope and avoid fully capitalized acronyms inside names.
- Indent with four spaces, use K&R braces, keep lines within 120 characters, and indent wrapped lines by eight additional spaces.
- Use explicit imports in a consistent order. Put every class in a package and attach array brackets to the type.
- Declare variables in the smallest useful scope and initialize them at declaration where practical. Keep mutable class fields non-public.
- Always brace loop and conditional bodies. Put loop and conditional expressions on their own lines in the standard layout.
- Add descriptive header comments for classes and public methods, except where the standard permits omission. Explain intent rather than restating implementation.

Review only touched code unless the user asks for a broader cleanup. Preserve behavior when the task is formatting or standards compliance.
