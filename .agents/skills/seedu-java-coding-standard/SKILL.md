---
name: seedu-java-coding-standard
description: Apply and review this project's Java code against the SE-EDU basic and intermediate Java coding standard. Use whenever creating, modifying, refactoring, or reviewing Java source or test code in this repository.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for every Java file in this project. For topics the guide does not cover, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Apply the standard

- Use lowercase package names; PascalCase noun names for classes and enums; camelCase verb names for methods; camelCase variable names; and SCREAMING_SNAKE_CASE constant names.
- Keep names in English. Use boolean names that read as predicates, such as `isDone`, `hasTasks`, or `canRun`. Name collections with plural nouns. Use short scratch names only in small scopes.
- Indent with 4 spaces and never tabs. Prefer lines shorter than 110 characters and never exceed 120. Indent continuation lines 8 spaces beyond the parent line; break after commas and before operators.
- Use K&R braces. Always brace loop and conditional bodies, even for one statement. Keep conditions and bodies on separate lines. Mark intentional traditional-switch fall-through with `// Fallthrough`.
- Surround operators with spaces; put spaces after Java keywords, commas, and `for` semicolons. Separate logical units with one blank line.
- Put every class in a package. Keep imports explicitly listed, minimal, consistently ordered, and grouped with blank lines. Place array brackets on the type.
- Declare variables in the smallest useful scope and initialize them at declaration when a valid value is available. Keep behavior-bearing fields non-public; constants may be public when appropriate.
- Write comments in English using American spelling. Add descriptive Javadoc to every class and public method, except self-explanatory getters/setters, exact overrides, and test code. Start Javadoc with a concise third-person summary such as `Returns`, `Adds`, or `Creates`; punctuate tag descriptions and include all `@param` tags or none.

## Review workflow

1. Inspect every changed Java file, including tests.
2. Fix violations without changing behavior unless the user requested a behavior change.
3. Check all Java lines for the 120-character hard limit and scan for tabs, wildcard imports, unbraced control flow, misleading names, and missing required Javadoc.
4. Run the project's required build and tests after executable-code changes, following `AGENTS.md`.
