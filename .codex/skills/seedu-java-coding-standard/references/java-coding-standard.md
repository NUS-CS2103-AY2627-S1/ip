# SE-EDU Java Coding Standard: Project Reference

This reference summarizes the basic and intermediate rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). The canonical
page takes precedence if this summary is ambiguous. Use the Google Java Style Guide for topics not covered there.

## Naming

- Write package names in lowercase. For school projects, use the project or group name as the root package.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants; give associated constants a common prefix.
- In test names, underscores may separate `featureUnderTest_testScenario_expectedBehavior`.
- Keep acronyms lowercase within names, such as `exportHtmlSource`, not `exportHTMLSource`.
- Write names in English. Use longer names for larger scopes; reserve short scratch names such as `i` for small scopes.
- Make boolean names read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.

## Layout

- Indent with four spaces, never tabs.
- Keep lines below the 120-character hard limit and preferably below 110 characters.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas and before operators or dots when
  that improves readability; keep a method name attached to its opening parenthesis.
- Use K&R braces: put the opening brace at the end of the declaration or control-statement line.
- Surround operators with spaces and place spaces after commas, Java keywords, and `for` semicolons.
- Separate logical units within a block with one blank line.
- Use the standard multi-line forms for methods, `if`/`else`, loops, `switch`, and `try`/`catch` statements.
- Add `// Fallthrough` when a colon-style switch case intentionally continues into the next case.

## Packages, imports, types, and variables

- Put every class in a package.
- Keep import ordering consistent. Group static imports first, then Java/JDK imports, third-party imports, and project
  imports, with blank lines between groups.
- Import classes explicitly; never use wildcard imports.
- Attach array brackets to the type, for example `String[] arguments`.
- Initialize variables where they are declared when a valid value is available, and declare them in the smallest
  practical scope.
- Keep class variables non-public unless the class is a behavior-free data class; constants are exempt.

## Loops and conditionals

- Always enclose loop and conditional bodies in braces, including single-statement bodies.
- Put each conditional body on its own line so it can be debugged clearly.

## Comments and Javadocs

- Write comments in English using American spelling and avoid local slang.
- Write descriptive Javadocs for every public class and public method, except obvious getters/setters, test code,
  and overrides whose inherited documentation applies exactly.
- Start a method Javadoc with a short third-person summary such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line, align subsequent `*` characters, and leave one blank Javadoc line before tags.
- Use either `@param` tags for every parameter or none. Add punctuation to parameter descriptions.
- Document return values and thrown exceptions where the summary does not already make them obvious.
- Keep comments indented with the code they describe. Use `{@inheritDoc}` when an override extends inherited behavior.
