# Project Java Coding Standard

This reference distills the basic and intermediate rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the Google Java Style Guide only for topics the SE-EDU standard does not cover.

## Naming

- Use lowercase package names, rooted in the project or group name and divided into logical packages.
- Name classes and enums with English nouns in PascalCase.
- Name methods with English verbs in camelCase.
- Name variables in camelCase and constants in `SCREAMING_SNAKE_CASE`.
- Write acronyms as ordinary words when they form part of a name, such as `exportHtmlSource`.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Make boolean names read as booleans, preferably using prefixes such as `is`, `has`, `was`, `can`,
  or `should`. A boolean setter parameter should use the corresponding boolean name.
- Use plural names for collections.
- Match name length to scope. Short scratch names such as `i` are acceptable in a small scope; reserve
  `j`, `k`, and later letters for nested loops.
- Give related constants a common prefix when that relationship improves discovery.

## Layout and whitespace

- Indent with four spaces and never tabs.
- Prefer lines below 110 characters; never exceed 120 characters.
- Indent continuation lines eight spaces beyond the parent line.
- Wrap for readability: normally break after commas and before operators, including `.`, `&` in type
  bounds, and `|` in multi-catch clauses. Keep a method or constructor name attached to its opening
  parenthesis and prefer higher-level breaks.
- Use K&R braces. Keep opening braces on the declaration or control-statement line.
- Surround operators with spaces. Put spaces after Java keywords, commas, and semicolons in `for`
  clauses. Surround ternary colons with spaces.
- Separate distinct logical units inside a block with one blank line.

## Statements and declarations

- Put every class in a package.
- Keep import ordering consistent, list imports explicitly, and remove unused imports. Do not use
  wildcard imports.
- Attach array brackets to the type, for example `String[] args`.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid
  initial value is available.
- Do not expose class variables publicly unless the class is a behavior-free data class. Constants are
  exempt.
- Use braces for every loop and conditional body, even a single statement. Put the conditional and its
  body on separate lines.
- Format method declarations and `if`, `else`, `for`, `while`, `do`, `switch`, `try`, `catch`, and
  `finally` blocks consistently with K&R braces.
- Mark intentional fall-through in a traditional `switch` with `// Fallthrough`.

## Comments and Javadocs

- Write comments in English using American spelling and avoid local slang.
- Indent comments with the surrounding code. Trailing comments are allowed when they remain clear.
- Write descriptive Javadocs for all classes and public methods. Javadocs may be omitted for getters
  and setters, overrides whose inherited documentation applies exactly, and classes or methods used
  for testing.
- Put `/**` on its own line. Start a method summary with a present-tense verb such as `Returns`,
  `Sends`, or `Adds`, and end sentences and tag descriptions with punctuation.
- Leave one blank Javadoc line between the description and block tags, but no blank source line between
  the Javadoc and its declaration.
- Include either `@param` tags for every parameter or none. Omit them when every parameter is
  self-explanatory or already explained by the main description.
- Omit `@return` for `void` methods and when the return value is already obvious from the description.
- Use `{@inheritDoc}` when an override needs inherited documentation plus a behavior-specific addition.
- Use a single-line Javadoc for a class member only when the short form remains clear.

## Review checklist

Before completing Java work, check:

- names and visibility;
- imports and declaration scope;
- indentation, line length, wrapping, braces, and whitespace;
- required and exempt Javadocs;
- comment language and indentation;
- compilation and relevant tests under Java 25.
