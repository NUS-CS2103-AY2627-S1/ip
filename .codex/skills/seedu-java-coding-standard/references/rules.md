# SE-EDU Java Coding Standard Rules

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

This project follows the SE-EDU Java coding standard basic + intermediate rule set. For topics not covered here, use the Google Java Style Guide.

## Naming

- Use lowercase package names.
- Use PascalCase nouns for classes and enums.
- Use camelCase for variables and verb-based method names.
- Use SCREAMING_SNAKE_CASE for constants.
- Use English names and comments.
- Do not capitalize every letter in acronyms inside identifiers; prefer forms such as `exportHtmlSource`.
- Name boolean variables and methods so they read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Use short scratch variables only for small scopes; use `j` and `k` mainly for nested loops.
- Give associated constants a shared prefix.
- Test methods may use underscores in the form `featureUnderTest_testScenario_expectedBehavior`, with later parts omitted when appropriate.

## Layout

- Indent with 4 spaces, not tabs.
- Keep lines below 120 characters; aim for 110 characters where reasonable.
- Indent wrapped continuation lines by 8 spaces relative to the parent line.
- Break wrapped expressions after commas and before operators or operator-like symbols.
- Keep method and constructor names attached to the opening parenthesis.
- Prefer breaking at higher-level expressions instead of inside nested subexpressions.
- Use K&R braces: the opening brace stays on the same line as the declaration or control statement.
- Separate logical units inside a block with a blank line when it improves readability.
- Surround operators with spaces, put one space after Java reserved words, and put one space after commas and `for` semicolons.

## Statements

- Put every production class in a package.
- Keep import ordering consistent and list every imported class explicitly.
- Attach array specifiers to the type, such as `int[] values`.
- Declare variables in the smallest practical scope and initialize them where declared when a valid value is available.
- Avoid public class variables in production classes unless the class is a behavior-free data class; constants are exempt.
- Always use braces for loop bodies and conditional bodies, even for one-line bodies.
- Put conditionals on their own line rather than writing statement bodies on the same line.
- Add an explicit `// Fallthrough` comment when a `switch` case intentionally falls through.

## Javadocs and Comments

- Write descriptive Javadocs for all public classes and public methods.
- Omit public-method Javadocs only for getters/setters, test methods, or overrides where inherited Javadoc applies exactly.
- Start method Javadoc summaries with a verb such as `Returns`, `Sends`, or `Adds`.
- Align Javadoc `*` characters, keep one space after each `*`, and do not leave a blank line between a Javadoc block and its declaration.
- Use `@param`, `@return`, and `@throws` only when they add useful information; if documenting parameters, document all parameters.
- Indent comments according to the code block they belong to.
- Use American spelling and avoid local slang.
