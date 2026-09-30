---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, editing, reviewing, or refactoring Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Review workflow

When changing Java code:

1. Apply every applicable rule below to new and edited code.
2. Check nearby existing code and fix clear violations when doing so remains within the user's requested scope.
3. Check production and test code for naming, layout, imports, declarations, control-flow braces, and comments.
4. Run `awk 'length($0) > 120 { print FILENAME ":" FNR ":" length($0) }'` over all affected Java files and resolve every result.
5. Compile and run the relevant tests after formatting or naming changes.

## Required rules

### Naming

- Use lowercase package names based on the project name and logical subpackages.
- Name classes and enums with English nouns in PascalCase.
- Name variables in camelCase and methods with English verbs in camelCase.
- Write constants in SCREAMING_SNAKE_CASE; give related constants a common prefix.
- Treat acronyms as words inside names: use `Ui`, not `UI`, and `HttpClient`, not `HTTPClient`.
- Use longer descriptive names for wider scopes; short names such as `i`, `j`, and `k` are acceptable for local iterators.
- Name booleans so they read as boolean values, typically with `is`, `has`, `can`, or `should`.
- Use plural nouns for collections.

### Layout

- Indent with four spaces and never tabs. Indent continuation lines eight spaces beyond their parent line.
- Keep every line at or below 120 characters. Break long expressions at readable boundaries.
- Use K&R braces: opening braces stay on the declaration or control statement line.
- Separate logical units inside a block with one blank line.
- Use consistent whitespace around operators and after commas; do not pad inside parentheses.

### Declarations and imports

- Put every class in a package.
- Keep import ordering consistent and import classes explicitly; do not use wildcard imports.
- Attach array brackets to the type, for example `String[] args`.
- Declare variables in the smallest practical scope and initialize them at declaration when possible.
- Keep class variables non-public unless they are constants or the class is a behavior-free data class.

### Control flow

- Always use braces around loop and conditional bodies, including single statements.
- Put a loop's condition on its own control-statement line and follow the standard K&R forms for `if`/`else`, `for`, `while`, `do`/`while`, `switch`, and `try`/`catch`.

### Comments

- Write comments in English and indent them with the code they describe.
- Add descriptive Javadoc to every public class and public method, except getters/setters, test code, and overrides whose inherited Javadoc applies exactly.
- Start Javadoc with a summary sentence and add information that explains the contract rather than restating the implementation.
- Use `@param`, `@return`, and `@throws` when they add information. Either document all parameters or omit all parameter tags when every parameter is already self-explanatory.
- Use `{@inheritDoc}` when an override needs to reuse and extend its parent's contract.
