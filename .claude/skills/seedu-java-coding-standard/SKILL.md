---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic-plus-intermediate Java coding standard for Java source and test code in this project. Use whenever creating, editing, formatting, or reviewing Java code here.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic + intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to all Java code in this repository. For topics it does not cover, follow the Google Java Style Guide.

## Review checklist

- Keep every class in a package and keep package names lowercase. For school projects, use the group or project name as the root package rather than an institutional namespace.
- Write all names in English. Name classes and enums as PascalCase nouns, methods as camelCase verbs, variables as camelCase, and constants in `SCREAMING_SNAKE_CASE`. Give associated constants a common prefix.
- Use normal camel-case capitalization for abbreviations and acronyms within names, such as `exportHtmlSource`, not `exportHTMLSource`.
- Name booleans so they read as booleans, preferably with `is`, `has`, `was`, `can`, or `should`. Name boolean setters in the form `setFound(boolean isFound)`.
- Use plural names for collections. Use longer, descriptive names for large scopes and reserve short names for small scopes. Use `i` for the first iterator and `j`, `k`, and later iterator names only for nested loops.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Indent with 4 spaces and never tabs. Prefer lines below 110 characters and never exceed 120.
- Indent wrapped continuations by 8 spaces more than the parent line. Optimize wrapping for readability: break after commas, before operators and operator-like symbols, and preferably at higher-level expressions. Keep a method or constructor name attached to its opening parenthesis.
- Use K&R braces and the standard layouts shown by the authoritative guide for methods, `if`/`else`, `for`, `while`, `do`/`while`, traditional and arrow `switch` statements, switch expressions, and `try`/`catch`/`finally` statements.
- Put conditional bodies on separate lines and always brace loops and conditionals, even for one statement. Add `// Fallthrough` whenever a traditional switch case intentionally falls through.
- Keep import ordering consistent, explicit, minimal, and free of wildcard imports.
- Attach array brackets to the type. Declare variables in the smallest possible scope and initialize them where declared whenever a valid value is available; leave them uninitialized rather than assigning a fake value otherwise. Do not expose class variables publicly unless they are constants or belong to a behaviorless data class.
- Separate logical units within a block with one blank line. Use spaces around operators, after Java keywords and commas, around ternary colons, and after semicolons in `for` statements.
- Write comments in English with American spelling and indent them with the surrounding code.
- Add descriptive Javadoc headers to every non-test class and public method. They may be omitted for getters/setters, test code, and overrides whose inherited documentation applies exactly.
- Format multiline Javadoc with `/**` on its own line, a short first-sentence summary beginning with a third-person verb such as `Returns`, `Adds`, or `Creates`, aligned `*`, a space after each `*`, and no blank line before the declaration. Put a blank line between the description and tags, and end every tag description with punctuation.
- Include `@return` when the return value is not already obvious. Include useful `@param` tags for either every parameter or none. Use `{@inheritDoc}` when inherited documentation needs clarification or extension. Single-line Javadocs are allowed for class members.

## Workflow

1. Inspect nearby Java code before editing and preserve behavior unless the user requested a behavior change.
2. Apply the checklist to changed code and correct existing violations in the requested scope.
3. Search changed Java files for lines over 120 characters, incorrect continuation indentation, wildcard imports, tabs, unbraced or incorrectly laid-out control flow, inconsistent names, and missing required Javadocs.
4. Run the project-required Gradle and UI verification after production-code changes.
