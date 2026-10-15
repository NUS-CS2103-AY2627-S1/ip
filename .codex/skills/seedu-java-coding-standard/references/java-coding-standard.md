# SE-EDU Java Coding Standard: Project Checklist

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

This checklist captures the basic and intermediate rules used by this project. For topics not covered here, follow the Google Java Style Guide as directed by the source.

## Naming

- Use lowercase package names, organized under the project root package.
- Use PascalCase noun names for classes and enums.
- Use camelCase verb names for methods and camelCase names for variables.
- Use SCREAMING_SNAKE_CASE for constants; give related constants a common prefix.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Keep acronyms lowercase except at the start of a PascalCase name: `exportHtml`, `DvdPlayer`.
- Write all names in English.
- Give broad-scope variables descriptive names; short scratch names are acceptable only in small scopes.
- Make boolean names read as booleans, normally using prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Name collections using plural nouns.
- Use `i` for the first loop index and `j`, `k`, and so on only for nested loops.

## Layout and whitespace

- Indent with 4 spaces, never tabs.
- Keep lines below the 110-character soft limit and never exceed 120 characters.
- Indent wrapped continuation lines 8 spaces beyond their parent line.
- Break after commas and before operators, including `.`, `&` in bounds, and `|` in catch clauses.
- Keep a method or constructor name attached to its opening parenthesis.
- Prefer higher-level line breaks that make the expression easier to understand.
- Use K&R braces: opening brace on the same line; closing brace aligned with the construct.
- Surround operators with spaces; add spaces after keywords, commas, and `for` semicolons.
- Separate logical units within a block with one blank line.
- Format `if`/`else`, loops, `try`/`catch`/`finally`, and methods using the source standard's layouts.
- Indent `case` and `default` labels one level inside `switch`, and their statements one further level.
- Add `// Fallthrough` whenever a traditional switch case intentionally has no `break`.

## Statements and declarations

- Put every class in a package.
- Order imports consistently, group them logically, and list every imported class explicitly; never use wildcard imports.
- Attach array brackets to the type, as in `int[] values`.
- Initialize variables where declared and declare them in the smallest practical scope.
- Do not expose class variables publicly unless the class is intentionally a behavior-free data class; constants are exempt.
- Always use braces around loop and conditional bodies, including single-statement bodies.
- Put a conditional and its body on separate lines.

## Comments and Javadoc

- Write comments in English using American spelling and no local slang.
- Write descriptive Javadoc for every public class and public method, except straightforward getters/setters, exact overrides, and test code.
- Start `/**` on its own line and make the first sentence a short summary beginning with a third-person verb such as “Returns”, “Adds”, or “Sends”.
- Align leading `*` characters and leave a space after each one.
- Put a blank Javadoc line between the description and block tags.
- End every parameter, return, and exception description with punctuation.
- Include either all useful `@param` tags or none; omit tags that add no information.
- Keep the Javadoc directly adjacent to the declaration.
- A one-line Javadoc is acceptable for a class member.
- Indent comments with the code they describe.

