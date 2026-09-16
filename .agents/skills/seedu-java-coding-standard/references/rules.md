# SE-EDU Java Coding Standard: Basic and Intermediate Rules

Source: [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html), reviewed 2026-08-28.

Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics not covered here.

## Naming

- Write package names in lowercase. For a school project, start the package hierarchy with the group or project name, followed by logical component names.
- Use PascalCase nouns for classes and enums.
- Use camelCase for variables and verb-based method names.
- Use SCREAMING_SNAKE_CASE for constants. Give related constants a common prefix.
- Name tests `featureUnderTest_testScenario_expectedBehavior`. Omit the scenario or expected-behavior segment when it adds no useful distinction.
- Treat acronyms as ordinary words inside identifiers, such as `exportHtmlSource`, not `exportHTMLSource`.
- Write identifiers in English.
- Give widely scoped variables descriptive names. Short scratch names such as `i` are acceptable only within a few nearby lines.
- Name booleans so they read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`. Name a boolean setter parameter after the property, such as `setFound(boolean isFound)`.
- Use plural names for collections and arrays.
- Use `i` for a loop iterator and reserve `j`, `k`, and similar names for nested iterators.

## Layout

- Indent with 4 spaces. Do not use tabs.
- Keep lines below 110 characters where practical and never exceed 120 characters.
- Indent wrapped lines 8 spaces beyond the parent line.
- When wrapping, normally break after a comma and before an operator, including `.`, `&` in a type bound, and `|` in a multi-catch clause.
- Keep a method or constructor name attached to its opening parenthesis.
- Prefer a higher-level syntactic break over a lower-level break.
- Keep a short ternary expression on one line. For a wrapped ternary, place `?` and `:` at the continuation indentation.
- Use K&R braces: place an opening brace on the declaration or control-statement line and the closing brace on its own line.
- Format `else`, `catch`, and `finally` on the same line as the preceding closing brace.
- Indent `case` and `default` labels one level inside a `switch`, and indent their statements one further level. This also applies to arrow-style switch labels.
- Add `// Fallthrough` whenever a colon-style switch case intentionally has no terminating `break`.
- Put spaces around operators, after Java keywords, and after commas. Surround a ternary colon with spaces, and put a space after each semicolon in a `for` header.
- Separate logical units within a block with one blank line.

## Packages, imports, types, and variables

- Put every class in a package.
- Keep import ordering consistent. Group static imports separately, group imports by top-level package, and sort each group consistently.
- Import classes explicitly. Do not use wildcard imports.
- Attach array brackets to the type, as in `String[] values`.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid initial value is available.
- Do not expose class variables publicly unless the class is intentionally a behavior-free data class. Constants may be public when appropriate.

## Statements

- Put each conditional body on lines separate from its condition.
- Always use braces for `if`, `else`, `for`, `while`, and `do-while` bodies, including single-statement bodies.
- Follow the layout and whitespace rules consistently for methods, loops, conditionals, switch statements, and try-catch-finally statements.

## Comments and Javadoc

- Write comments in English using American spelling and avoid local slang.
- Write descriptive Javadoc for every class and public method. Javadoc may be omitted for getters and setters, test classes and methods, and an override whose inherited documentation applies exactly.
- Put `/**` on its own line for a normal Javadoc block and keep the block immediately adjacent to the declaration.
- Begin the first sentence with a concise third-person verb such as `Returns`, `Sends`, `Adds`, or `Creates`.
- Align each `*`, put one space after it, and separate the description from block tags with a blank Javadoc line.
- End every `@param` description with punctuation.
- Include `@return` unless the return value is obvious from the description. Omit it for `void` methods.
- Include either all `@param` tags or none. Omit them only when every parameter is already self-explanatory or fully explained in the description.
- Use `{@inheritDoc}` when an override needs to reuse and extend inherited documentation.
- A short member comment may use a one-line Javadoc form.
- Indent comments to match the code they describe. Trailing comments are allowed when they remain clear.
