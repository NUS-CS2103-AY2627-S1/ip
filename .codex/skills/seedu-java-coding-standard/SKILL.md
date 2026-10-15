---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java coding standard

Apply the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to all Java code in this project. Use the Google Java Style Guide for topics not covered by SE-EDU.

Key requirements:

- Use lowercase package names; PascalCase nouns for classes and enums; camelCase for variables and verb-named methods; SCREAMING_SNAKE_CASE for constants. Use English and meaningful names, boolean names that read as predicates, and plural names for collections.
- Use four spaces, K&R braces, consistent whitespace, and lines no longer than 120 characters (prefer under 110). Wrap long lines with an additional eight-space indentation. Separate logical units with one blank line.
- Put every class in a package, keep imports explicit and consistently ordered, and attach array brackets to the type.
- Initialize variables at declaration when practical and keep them in the smallest scope. Do not expose class variables publicly except data-class fields and constants.
- Always use braces for loops and conditionals, and put conditional bodies on separate lines. Mark intentional switch fall-through with `// Fallthrough`.
- Write English, American-spelled comments. Add descriptive Javadoc to every public class and public method unless it is a getter/setter, an overriding method whose inherited documentation applies exactly, or a test method. Format Javadoc with a summary sentence, aligned tags, punctuation, and no blank line before the declaration.

When changing existing code, preserve behavior and user-visible output while correcting violations that are in scope for the change.
