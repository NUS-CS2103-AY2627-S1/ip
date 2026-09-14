# Java conventions

Use the [SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the source of truth. For topics it does not cover, use the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Naming

- Use lowercase package names and PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Keep abbreviations and acronyms lowercase inside identifiers, such as
  `exportHtmlSource`.
- Name booleans to read as booleans, preferably with `is`, `has`, `can`, or
  `should` prefixes.
- Use plural names for collections.
- Use the test naming form
  `featureUnderTest_testScenario_expectedBehavior`; omit parts only when they
  add no useful distinction.
- Use English names. Longer-lived variables need more descriptive names; short
  loop indices may use `i`, with `j` and later letters reserved for nesting.

## Layout

- Indent with four spaces, never tabs. Add eight spaces relative to the parent
  when wrapping a line.
- Keep lines below 110 characters where practical and never exceed 120.
- Use K&R braces. Always brace loop and conditional bodies, including
  single-statement bodies.
- Break after commas and before operators. Keep a method name with its opening
  parenthesis, and prefer a higher-level break over a lower-level break.
- Put `case` labels one indentation level inside their `switch` and indent case
  statements one further level. Mark intentional fall-through with
  `// Fallthrough`.
- Surround operators with spaces, put a space after Java keywords and commas,
  and separate logical units with blank lines.

## Declarations and imports

- Put every class in a logical package.
- Use one consistent import order, list imports explicitly, and remove unused
  imports. Never use wildcard imports.
- Attach array brackets to the type, for example `int[] values`.
- Initialize variables where declared and declare them in the smallest useful
  scope. Do not expose class variables publicly except constants or fields in a
  behavior-free data class.

## Comments and Javadocs

- Write comments in English with American spelling.
- Add descriptive Javadocs to every public class and public method. Javadocs
  may be omitted for self-explanatory getters/setters, exact overrides, and
  test classes or methods.
- Begin a method Javadoc with a concise third-person summary such as
  `Returns ...`, `Adds ...`, or `Sends ...`.
- Use `@param`, `@return`, and `@throws` when they add information. Document all
  parameters or none, punctuate tag descriptions, and separate the description
  from tags with one blank Javadoc line.
- Indent comments with the code they describe and use comments to explain
  intent rather than restating implementation.
