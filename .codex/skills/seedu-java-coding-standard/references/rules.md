# SE-EDU Java standard checklist

Source: https://se-education.org/guides/conventions/java/intermediate.html

This checklist summarizes the basic and intermediate rules. For a topic not
covered here, follow the Google Java Style Guide as directed by the source.

## Naming

- Use lowercase package names rooted in the project or group name.
- Use English noun names in PascalCase for classes and enums.
- Use English verb names in camelCase for methods.
- Use camelCase for variables and SCREAMING_SNAKE_CASE for constants.
- Keep acronyms lowercase when embedded in names, such as `Ui` rather than
  `UI` and `exportHtml` rather than `exportHTML`.
- Name boolean variables and methods so they read as booleans, preferably with
  prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections and arrays.
- Give large-scope variables descriptive names. Short scratch names such as
  `i` are acceptable only in small scopes; reserve `j` and later letters for
  nested loops.
- Give associated constants a common prefix where doing so clarifies their
  relationship.
- Test methods may use
  `featureUnderTest_testScenario_expectedBehavior`; parts may be omitted when
  the test scope remains clear.

## Layout and whitespace

- Indent with four spaces and never tabs.
- Prefer lines shorter than 110 characters and never exceed 120 characters.
- Indent wrapped continuation lines eight spaces beyond their parent. Break
  after commas and before operators, dots, and similar operator-like symbols.
- Keep method and constructor names attached to their opening parenthesis.
- Prefer higher-level line breaks that make expressions easier to understand.
- Use K&R braces for classes, methods, loops, conditionals, switches, and
  try/catch/finally blocks.
- Surround operators with spaces; place spaces after Java keywords, commas,
  and `for` semicolons. Space colons when they act as operators.
- Separate logical units in a block with one blank line.

## Packages, imports, and declarations

- Put every class in a logical package under the Java source root. Do not make
  source-root directories such as `src.main.java` part of a package name.
- Keep import ordering consistent. Put static imports first, followed by
  groups such as `java`, `javax`, third-party, and project imports, with blank
  lines between groups.
- Import classes explicitly; never use wildcard imports.
- Attach array brackets to the type, for example `int[] values`.
- Initialize variables where declared when a valid value is available, and
  declare them in the smallest practical scope.
- Keep class variables non-public unless the class is a behavior-free data
  class. Public constants are allowed.

## Control flow

- Always use braces around loop and conditional bodies, including one-line
  bodies, and put each body statement on its own line.
- Use conventional `if`/`else`, `for`, `while`, `do-while`, `switch`, and
  `try`/`catch`/`finally` layouts.
- Mark intentional switch fall-through explicitly with `// Fallthrough`.

## Comments and Javadocs

- Write comments in English, use American spelling, and avoid local slang.
- Add descriptive Javadocs to every class and public method, except getters and
  setters, overrides whose inherited contract applies exactly, and test code.
- Put `/**` on its own line. Start with a concise summary sentence; method
  summaries should begin with a third-person verb such as `Returns`, `Adds`,
  or `Sends`.
- Align leading `*` characters, include a space after each one, and place no
  blank line between the Javadoc block and its declaration.
- Put a blank Javadoc line between the description and block tags.
- End every parameter, return, and throws description with punctuation.
- Either document every parameter with `@param` or omit all `@param` tags when
  every parameter is already self-explanatory or covered by the description.
- Omit `@return` for `void` methods or when the return value is already obvious.
- Use `{@inheritDoc}` when an override needs to inherit and extend a parent
  contract.
- A short member comment may use one line, for example
  `/** Number of stored tasks. */`.
- Indent implementation comments with the code they describe. Trailing
  comments are allowed when clear.
