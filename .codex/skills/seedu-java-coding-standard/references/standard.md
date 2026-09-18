# Project Java coding standard

This checklist paraphrases the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Naming

- Use lowercase package names rooted in the project name. Put every class in a package.
- Use PascalCase nouns for classes and enums; camelCase verbs for methods; camelCase for variables; and SCREAMING_SNAKE_CASE for constants.
- Keep abbreviations inside names in normal camel case. Use English names. Name booleans like questions, preferably with `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections. Reserve short names for small scopes and `i`, then `j`, for nested loop indices.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`, with later parts omitted when unnecessary.

## Layout

- Indent with 4 spaces, never tabs. Prefer at most 110 characters per line; never exceed 120.
- Indent continuations 8 spaces beyond the parent line. Break after commas and before operators; keep a method name with its opening parenthesis and prefer high-level breaks.
- Use K&R braces. Always brace loop and conditional bodies and place their statements on separate lines.
- Surround operators with spaces; put spaces after keywords, commas, and `for` semicolons. Separate logical units with one blank line.
- Format methods, conditionals, loops, `switch`, and `try` statements consistently. Mark intentional switch fall-through with `// Fallthrough`.

## Declarations

- List imports explicitly and order them consistently; never use wildcard imports.
- Attach array brackets to the type.
- Initialize variables at declaration when possible and declare them in the smallest useful scope.
- Keep fields non-public unless they are constants or the class is a behavior-free data class.

## Comments and Javadoc

- Write comments in English with American spelling and align them with the surrounding code.
- Document every class and public method, except getters/setters, exact overrides, and test code where the documentation adds no value.
- Start Javadoc with a short sentence describing the contract, such as “Returns…”, “Adds…”, or “Sends…”. Use complete punctuation and place no blank line between Javadoc and its declaration.
- Add a blank Javadoc line before tags. Either document all parameters or omit all obvious parameters. Give every tag description terminal punctuation. Use `{@inheritDoc}` when an override needs adapted inherited documentation.
