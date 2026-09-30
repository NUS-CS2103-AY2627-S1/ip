---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Use the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for all production Java code and tests in this repository. For matters it does not cover, follow the Google Java Style Guide.

## Apply these rules

- Keep package names lowercase; class and enum names are PascalCase nouns; methods are camelCase verbs; variables are camelCase; and constants are `SCREAMING_SNAKE_CASE`.
- Use readable English names. Boolean names should read as booleans (`is`, `has`, `can`, `should`, or `was`), and collection names should be plural.
- Indent with four spaces (no tabs). Aim for lines under 110 characters and never exceed 120. Wrap for readability: break after commas and before operators or chained dots, with continuation indentation of eight spaces beyond the parent line.
- Use K&R braces. Put every conditional and loop body on its own line and always include braces. Put `else`, `catch`, and `finally` on the same line as the preceding closing brace.
- Use one space around binary/ternary operators, after keywords, and after commas. Separate logical units with one blank line.
- Give every class a package. Keep imports explicit (no wildcards) and consistently grouped: static imports, `java`, `javax`, third-party, then project imports, with blank lines between nonempty groups.
- Declare and initialize variables in the smallest practical scope. Keep instance fields non-public unless the type is a behavior-free data class; constants are exempt.
- Write English, American-spelling Javadoc for public classes and public methods, except obvious getters/setters, applicable overrides, and test code. Start a method summary with a third-person verb such as “Adds”, “Returns”, or “Marks”; document non-obvious parameters, return values, and exceptions.
- Add `// Fallthrough` for each intentional fall-through in a traditional `switch`.

## Review before finishing

Check modified Java files for the rules above, especially unbraced conditionals, line length, public API Javadoc, import ordering, naming, and accidental public mutable fields. Preserve existing behavior unless the task explicitly asks for a functional change.
