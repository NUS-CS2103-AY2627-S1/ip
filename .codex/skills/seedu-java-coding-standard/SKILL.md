---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to Java code created, changed, or reviewed in this project.
---

# SE-EDU Java Coding Standard

Follow these rules for all Java production and test code in this project. When reviewing existing code, fix violations
in the code within the user's requested scope without making unrelated behavioral or design changes.

The authoritative source is the
[SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics the SE-EDU standard
does not cover. If this summary and the authoritative source differ, follow the authoritative source.

## Naming

- Use lowercase package names rooted in the project or group name.
- Use PascalCase noun names for classes and enums.
- Use camelCase verb names for methods and camelCase names for variables.
- Use SCREAMING_SNAKE_CASE for constants and common prefixes for related constants.
- Keep names in English. Write acronyms as ordinary name parts, such as `exportHtmlSource`, not `exportHTMLSource`.
- Give wider-scope variables more descriptive names; reserve short scratch names such as `i` for small scopes and
  nested-loop names such as `j` for nested loops.
- Name booleans to read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`. Name a
  boolean setter and parameter like `setFound(boolean isFound)`.
- Use plural names for collections.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`, omitting parts when appropriate.

## Layout

- Indent with four spaces and never tabs. Indent wrapped lines eight spaces beyond their parent line.
- Keep lines below the 120-character hard limit and aim for fewer than 110 characters.
- Break after commas and before operators, including `.`, `&` in type bounds, and `|` in multi-catch. Keep a method
  or constructor name attached to its opening parenthesis and prefer higher-level expression breaks.
- Use K&R braces. Put control-flow bodies on separate lines and always enclose loop and conditional bodies in braces.
- Surround operators with spaces; add a space after Java keywords, commas, and `for` semicolons. Surround ternary
  colons with spaces.
- Separate logical units within a block with one blank line.
- Format methods, conditionals, loops, `try`/`catch`/`finally`, and `switch` statements as shown by the source guide.
  Add `// Fallthrough` whenever a colon-style `case` intentionally continues into the next case.

## Declarations

- Put every class in a package.
- Keep import ordering consistent, group imports coherently, list every imported type explicitly, and remove unused
  imports. Never use wildcard imports.
- Attach array brackets to the type, such as `int[] values`.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid value is
  available.
- Do not expose mutable class variables as `public`; use encapsulation. Public constants and behavior-free data
  classes are the stated exceptions.

## Comments and Javadocs

- Write comments in clear English using American spelling and no local slang. Indent comments with the surrounding
  code.
- Add descriptive Javadocs to every class and public method, except trivial getters/setters, test code, and exact
  overrides whose inherited documentation applies unchanged.
- Begin a Javadoc with a short summary sentence using third-person verb forms such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line, align each `*`, put a space after it, and leave no blank line between the Javadoc and its
  declaration.
- Put a blank Javadoc line between the description and tags. End every parameter description with punctuation.
- Include either all `@param` tags or none. Omit them only when every parameter is already self-explanatory or fully
  explained in the description. Omit `@return` only when it adds no information.
- Use `{@inheritDoc}` when an override needs inherited documentation plus a behavioral clarification.
