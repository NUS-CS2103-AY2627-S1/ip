# Required Java rules

This is a working checklist for the basic and intermediate rules in the
[authoritative SE-EDU standard](https://se-education.org/guides/conventions/java/intermediate.html).
Consult the authoritative page if a case is not covered here.

## Naming

- Use lowercase package names, PascalCase class and enum names, camelCase variable and method
  names, and SCREAMING_SNAKE_CASE constants.
- Use verbs for methods. Name booleans so they read as booleans, normally with prefixes such as
  `is`, `has`, `was`, `can`, or `should`.
- Use plural nouns for collections. Use English names and avoid fully capitalized acronyms inside
  camelCase or PascalCase names.
- Use descriptive names for large scopes. Short scratch names such as `i` are suitable only for
  small scopes; reserve `j`, `k`, and later letters for nested loops.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout

- Indent with 4 spaces, never tabs. Use K&R braces.
- Keep lines below the 110-character soft limit and never exceed 120 characters. Indent wrapped
  continuation lines by 8 spaces relative to the parent line.
- Break after commas and before operators. Keep a method name attached to its opening parenthesis.
- Surround operators with spaces and put one space after Java keywords, commas, and semicolons.
- Separate logical units with blank lines and end every source file with exactly one newline.

## Statements and declarations

- Put every class in a package and list imported classes explicitly; do not use wildcard imports.
- Keep import ordering consistent: static imports, Java/JDK imports, third-party imports, and
  project imports, with blank lines between groups when present.
- Attach array brackets to the type, for example `String[] values`.
- Initialize variables where declared when a valid initial value exists, and declare them in the
  smallest practical scope.
- Do not expose class variables publicly unless the class is a behavior-free data class. Public
  constants are exempt.
- Always use braces for loop and conditional bodies. Put the condition and body on separate lines.
- Mark intentional fall-through between traditional `switch` cases with `// Fallthrough`.

## Comments and JavaDoc

- Write comments in English using American spelling and no local slang.
- Write descriptive header comments for every public class and public method. They may be omitted
  for getters/setters, overrides whose inherited JavaDoc applies exactly, and test code.
- Start JavaDoc with `/**` on its own line. Begin with a short summary sentence using forms such as
  “Returns”, “Adds”, or “Sends”. Leave a blank line before block tags.
- Document all parameters or none. Add punctuation to parameter, return, and throws descriptions.
- Keep comments indented with the code they describe. Use `{@inheritDoc}` for an override only when
  inherited documentation needs to be reused with further clarification.
