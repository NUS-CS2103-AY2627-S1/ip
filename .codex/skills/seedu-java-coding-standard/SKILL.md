---
name: seedu-java-coding-standard
description: Apply the mandatory SE-EDU basic and intermediate Java coding rules when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Use the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
for every Java change in this repository. Use the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics the SE-EDU standard does
not cover. Explicit user and repository requirements still take precedence.

## Apply the standard

Before finishing a Java change, review every affected production and test file against these rules. Fix violations in
the affected code without changing behavior merely for style. When asked for a repository-wide standards update,
audit all Java files.

### Naming

- Write package names in lowercase, rooted in the project or group name.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase for variables, and
  SCREAMING_SNAKE_CASE for constants.
- Use English names. Treat abbreviations and acronyms as ordinary words when embedded in a name, such as `Ui` or
  `exportHtmlSource`.
- Give wide-scope variables descriptive names; reserve short scratch names such as `i` and `j` for small scopes and
  nested loops.
- Name booleans so they read as booleans, preferably with `is`, `has`, `was`, `can`, or `should`. Name boolean setters
  in the form `setFound(boolean isFound)`.
- Use plural names for collections. Give related constants a common prefix.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`, omitting parts only when appropriate.

### Layout

- Indent with four spaces and never tabs. Use K&R braces.
- Keep lines below the 120-character hard limit and preferably below 110 characters.
- Indent wrapped lines eight spaces beyond their parent line. Break after commas and before operators, including `.`,
  `&` in type bounds, and `|` in multi-catch. Keep a method name attached to its opening parenthesis and prefer
  higher-level breaks.
- Format methods, constructors, conditionals, loops, `switch`, `try`/`catch`/`finally`, and ternaries as shown in the
  SE-EDU standard. Indent `case` and `default` one level inside `switch`.
- Add `// Fallthrough` whenever a colon-style switch case intentionally falls through.
- Put spaces around operators, after Java keywords and commas, around ternary colons, and after semicolons in `for`
  headers. Separate logical units in a block with one blank line.

### Statements and declarations

- Put every class in a package.
- Order imports consistently, group them logically, list every imported type explicitly, and remove unused imports.
- Attach array brackets to the type, as in `int[] values`.
- Declare variables in the smallest practical scope and initialize them at declaration when a real valid value is
  available.
- Do not expose class variables as `public` unless the class is a behavior-free data class; constants are exempt.
- Always use braces for loop and conditional bodies, and place the body on separate lines.

### Comments and Javadocs

- Write comments in English using American spelling and no local slang. Indent comments with the code they explain.
- Add descriptive Javadocs to every public class and public method, except getters/setters, test code, and overrides
  whose inherited documentation applies exactly. Keep project-required Javadocs on non-public declarations too.
- Start a Javadoc summary with a third-person verb such as `Returns`, `Adds`, or `Sends`. Put `/**` on its own line,
  align subsequent `*` characters, leave one blank line before block tags, punctuate tag descriptions, and place no
  blank line between the Javadoc and declaration.
- Either document all parameters with `@param` or omit all `@param` tags when every parameter is self-explanatory.
  Omit `@return` for `void` methods or when the return value is already obvious. Use `{@inheritDoc}` when an override
  needs additions to inherited documentation.

## Verify the result

- Search changed Java files for tabs and lines over 120 characters.
- Re-read names, imports, braces, wrapping, scopes, and Javadocs; automated formatting does not replace this review.
- Run the project-required JUnit and console UI checks after code changes.
