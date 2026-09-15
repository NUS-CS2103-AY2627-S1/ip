---
name: seedu-java-coding-standard
description: Review and write Java code in this project according to the SE-EDU basic and intermediate Java coding standard.
---

# SE-EDU Java coding standard

Apply this standard to every Java source change in this project:

- Use lowercase package names; PascalCase nouns for classes/enums; camelCase verbs for methods and variables; and SCREAMING_SNAKE_CASE for constants. Boolean names should read as predicates, and collection names should be plural.
- Use four-space indentation, K&R braces, spaces around operators and after commas, one logical unit per blank-line-separated block, and a hard maximum line length of 120 characters (prefer less than 110). Wrapped continuation lines use an additional eight spaces.
- Put every class in a package. Keep imports explicit, minimal, and consistently ordered; never use wildcard imports. Put array brackets on the type.
- Initialize variables at declaration when practical and keep them in the smallest scope. Keep class fields non-public unless they are constants or the class is a behaviorless data class.
- Always use braces for loops and conditionals, including single-statement bodies. Put conditional bodies on separate lines and make intentional switch fallthrough explicit with `// Fallthrough`.
- Write English, American-spelled comments. Add descriptive Javadoc to public classes and public methods, except getters/setters, exact overrides, and test code. Begin method summaries with an action such as “Returns” or “Adds”.

For topics not covered here, follow the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) and its linked Google Java Style Guide. Review all touched Java files against these rules before finishing.
