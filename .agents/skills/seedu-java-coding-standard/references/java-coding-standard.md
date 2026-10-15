# SE-EDU Java Coding Standard: Project Rules

Source: [Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)

The source page is authoritative. Follow the Google Java Style Guide for topics
that it does not cover.

## Naming

- Use lowercase package names. For school projects, use the project or group name
  as the root package, followed by logical subpackages.
- Use noun-based PascalCase names for classes and enums.
- Use camelCase for variables and verb-based camelCase for methods.
- Use SCREAMING_SNAKE_CASE for constants. Give associated constants a common
  prefix.
- Name tests using
  `featureUnderTest_testScenario_expectedBehavior`; omit later parts only when the
  test covers the wider scope implied by the shorter name.
- Do not capitalize abbreviations or acronyms within names. Use English names.
- Give large-scope variables descriptive names; reserve short names for small,
  obvious scratch scopes.
- Make boolean names read as booleans. Prefer prefixes such as `is`, `has`, `was`,
  `can`, or `should`. Name boolean setters like `setFound(boolean isFound)`.
- Use plural names for collections and arrays.
- Use `i` for a primary iterator and `j`, `k`, and later letters for nested loops.

## Layout

- Indent with four spaces and never tabs.
- Prefer lines below 110 characters; never exceed 120 characters.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas
  and before operators, including `.`, `&` in type bounds, and `|` in catches.
- Keep a method or constructor name attached to its opening parenthesis and prefer
  higher-level line breaks.
- Use K&R braces: place opening braces on the declaration or control-statement
  line, and place `else`, `catch`, and `finally` on the closing-brace line.
- Format methods, conditionals, loops, switches, and try-catch blocks consistently
  with K&R style. Mark intentional traditional-switch fall-through with
  `// Fallthrough`.
- Put spaces around operators, after Java keywords and commas, around ternary
  colons, and after semicolons in `for` headers.
- Separate logical units within a block with one blank line.

## Statements and declarations

- Put every class in a package.
- Keep imports explicit, minimal, and consistently grouped in this order: static
  imports, Java standard-library imports, third-party imports, then project
  imports. Separate non-empty groups with one blank line.
- Attach array brackets to the type, for example `int[] values`.
- Declare variables in the smallest practical scope and initialize them at the
  declaration when a valid value is available. Do not use a phony initializer
  merely to satisfy this preference.
- Do not expose class variables publicly unless the class is a behavior-free data
  class; constants are exempt.
- Always use braces around loop and conditional bodies, including single
  statements. Put a conditional body on a separate line.

## Comments and Javadocs

- Write comments in English, use American spelling, and avoid local slang.
- Add descriptive Javadocs to public classes and public methods. They may be
  omitted for getters and setters, test code, and overrides whose inherited
  documentation applies exactly.
- Put `/**` on its own line. Start with a short summary sentence using third-person
  wording such as “Returns,” “Sends,” or “Adds.”
- Align each `*`, include a space after it, and leave one empty Javadoc line before
  block tags.
- End parameter descriptions with punctuation and place no blank line between the
  Javadoc and its declaration or annotations.
- Include `@param` for every parameter or omit all `@param` tags when every name is
  already self-explanatory. Omit `@return` for `void` methods or when the return
  value is obvious from the description.
- Use `{@inheritDoc}` when inheriting documentation and add details when an
  override behaves differently.
- Indent comments with the code they describe. Trailing comments are allowed.
