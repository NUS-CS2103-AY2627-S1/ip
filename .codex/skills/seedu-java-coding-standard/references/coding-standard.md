# Java coding-standard checklist

This checklist summarizes the basic and intermediate rules from the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Consult the source when a rule is ambiguous or an edge case is not covered here. Follow the Google Java Style Guide only for topics absent from the SE-EDU standard.

## Naming

- Use lowercase package names organized by project and logical component.
- Name classes and enums with English nouns in PascalCase.
- Name methods with English verbs in camelCase.
- Use camelCase for variables and SCREAMING_SNAKE_CASE for constants.
- Keep acronyms lowercase within camelCase or PascalCase names, such as `exportHtmlSource`.
- Name booleans and boolean methods like predicates, preferably with `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections and arrays.
- Give wide-scope variables descriptive names; reserve short names such as `i` and `j` for small scopes and nested iterators.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout

- Indent with four spaces and never tabs. Indent wrapped continuation lines eight spaces beyond their parent line.
- Keep lines below 110 characters where practical and never exceed 120 characters.
- Use K&R braces. Always use braces for loop and conditional bodies, including single statements.
- Put conditional bodies on separate lines.
- When wrapping, break after commas and before operators. Keep method names attached to their opening parenthesis and prefer higher-level breaks.
- Surround operators with spaces. Put a space after Java keywords, commas, and `for`-loop semicolons.
- Separate logical units within a block with one blank line.
- Indent `case` and `default` labels inside their `switch` block. Mark intentional statement fall-through with `// Fallthrough`.

## Declarations and imports

- Put every class in a package.
- Order imports consistently: static imports, `java`, `javax`, third-party, then project imports, with groups separated by blank lines.
- List imported classes explicitly; do not use wildcard imports. Keep imports minimal and sorted consistently within groups.
- Attach array brackets to the type, such as `String[] arguments`.
- Declare variables in the smallest useful scope and initialize them at declaration when a valid value is available.
- Do not expose mutable class variables as `public`; constants and behavior-free data classes are the exceptions described by the source.

## Comments and Javadocs

- Write comments in English using American spelling and avoid local slang.
- Add descriptive Javadocs to every class and public method, except getters/setters, exact-behavior overrides, and test code as allowed by the source.
- Start a method Javadoc with a concise third-person verb such as “Returns”, “Adds”, or “Sends”.
- Use standard `/** ... */` layout. Align asterisks, leave a blank line before block tags, punctuate tag descriptions, and place no blank line between the Javadoc and declaration.
- Include all `@param` tags or omit them all when parameter names are fully self-explanatory. Omit `@return` only when it adds no useful information.
- Indent comments with the code they describe.
