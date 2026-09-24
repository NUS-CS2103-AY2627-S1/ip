---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard to all Java production and test code in this project.
---

# SE-EDU Java coding standard

Apply the intermediate rules in the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) whenever creating, editing, or reviewing Java code in this repository. For matters it does not cover, follow the Google Java Style Guide as that document directs.

## Required conventions

- Put every class in a lowercase package. Use English, meaningful names: PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase variables, and SCREAMING_SNAKE_CASE constants. Name booleans as predicates such as `isDone` or `hasTime`; name collections with plurals.
- Use 4 spaces (never tabs), K&R braces, a soft 110-character line limit and hard 120-character limit. Indent wrapped lines 8 spaces beyond the parent line; break after commas or before operators and dots when that is clearest.
- Use spaces around operators, after commas, and after control-flow keywords. Separate logical units with one blank line. Keep import ordering consistent and list imports explicitly; do not use wildcard imports.
- Declare and initialize variables in the smallest practical scope. Keep mutable instance fields non-public unless the type is a behavior-free data class. Attach array brackets to the type.
- Always place conditionals and loops on their own lines and wrap their bodies in braces. Mark intentional switch fall-through with `// Fallthrough`.
- Write English comments using American spelling. Give every public class and public method a descriptive Javadoc header, except straightforward getters/setters, overrides whose inherited documentation applies unchanged, and test code. Start Javadoc summaries with a third-person verb such as `Returns`, `Adds`, or `Creates`; include useful `@param`, `@return`, and `@throws` tags.

## Before finishing

Review the touched Java files for these rules. Preserve existing behavior unless the requested change includes a behavior change, and run the relevant build and test checks.
