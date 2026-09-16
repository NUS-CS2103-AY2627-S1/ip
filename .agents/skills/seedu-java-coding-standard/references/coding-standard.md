# Project Java coding-standard checklist

Authoritative source: [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for
topics not covered by SE-EDU.

## Naming

- Use lowercase package names organized by project and responsibility.
- Use English PascalCase nouns for classes and enums.
- Use English camelCase verbs for methods and camelCase nouns for variables.
- Use SCREAMING_SNAKE_CASE for constants; give related constants a common prefix when useful.
- Keep abbreviations within names lowercase, such as `exportHtmlSource` rather than
  `exportHTMLSource`.
- Name booleans so they read as booleans, preferably with `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Prefer descriptive names for broad scopes; short scratch/index names are acceptable only in
  very small scopes.

## Layout

- Indent with four spaces and never tabs.
- Keep lines below the 120-character hard limit and preferably below 110 characters.
- Indent wrapped lines eight spaces beyond the parent indentation. Break after commas and before
  operators or operator-like symbols when that improves readability.
- Use K&R braces. Keep method names attached to their opening parenthesis.
- Always use braces for loop and conditional bodies, including one-line bodies.
- Put conditional bodies on their own lines.
- Indent `case` labels one level inside a `switch`. Mark intentional fall-through with
  `// Fallthrough`.
- Surround operators with spaces, add spaces after commas and reserved words, and separate
  logical units with a blank line.

## Imports, types, and variables

- Put every class in a package and import every used type explicitly; never use wildcard imports.
- Use this project's consistent import groups: static imports, Java/JDK imports, third-party
  imports, then project imports. Separate non-empty groups with one blank line and sort each group.
- Attach array brackets to the type, such as `String[] args`.
- Initialize variables where declared when a valid value is available, and declare them in the
  smallest practical scope.
- Do not expose mutable class fields publicly; use behavior or accessors instead. Constants may be
  public when appropriate.

## Comments and JavaDoc

- Write comments in English using American spelling and indent them with the surrounding code.
- Give every public class and public method a descriptive JavaDoc header. JavaDoc may be omitted
  for obvious getters/setters, tests, and overrides whose inherited documentation applies exactly.
- Begin a method's first sentence with a third-person verb such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line, align each `*`, leave one blank line before block tags, and keep the
  JavaDoc directly adjacent to the declaration.
- If parameter tags add value, document all parameters. End parameter, return, and throws
  descriptions with punctuation.
- Use `{@inheritDoc}` when an override needs to extend inherited documentation.

## Review commands

Run these from the repository root in addition to project-specific tests:

```shell
rg -n $'\t' src/main/java src/test/java
awk 'length($0) > 120 { print FNR ":" length($0) ":" $0 }' $(rg --files src/main/java src/test/java -g '*.java')
./gradlew javadoc test
```
