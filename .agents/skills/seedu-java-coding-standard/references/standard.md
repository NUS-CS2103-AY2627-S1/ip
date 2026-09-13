# SE-EDU Java coding standard: basic and intermediate rules

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

## Naming

- Use lowercase package names organized by project and logical component.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants; give associated constants a common prefix where useful.
- In test names, underscores may separate `featureUnderTest_testScenario_expectedBehavior`.
- Treat abbreviations and acronyms as words inside identifiers, such as `exportHtmlSource`.
- Use English names.
- Use longer, descriptive names for large scopes; short conventional scratch names are acceptable in small scopes.
- Name booleans so they read as booleans, normally with prefixes such as `is`, `has`, `was`, `can`, or `should`.
- Use plural names for collections.
- Reserve `j`, `k`, and similar iterator names for nested loops.

## Layout

- Indent with 4 spaces, never tabs.
- Aim for lines under 110 characters and never exceed 120 characters.
- Indent continuation lines 8 spaces beyond the parent line.
- When wrapping, normally break after commas and before operators, including `.`, `&`, and `|`.
- Keep a method or constructor name attached to its opening parenthesis.
- Prefer high-level breaks that expose the expression's structure.
- Use K&R braces: opening braces stay on the declaration or control-statement line.
- Format methods and control structures consistently. Indent `case` labels one level inside `switch` and their statements one further level when using colon-style cases.
- Mark intentional colon-style fall-through with `// Fallthrough`.
- Surround operators with spaces; put spaces after keywords, commas, and `for` semicolons.
- Separate logical units within a block with a blank line when that improves readability.

## Statements and declarations

- Put every class in a package.
- Keep import ordering consistent, use explicit imports, and remove unused imports. Never use wildcard imports.
- Attach array brackets to the type, for example `String[] values`.
- Initialize variables at declaration when a valid value is available, and declare them in the smallest useful scope.
- Do not expose mutable class variables publicly. Public constants and behavior-free data classes are exceptions.
- Always use braces around loop and conditional bodies, including single statements.
- Put a conditional body on a separate line.

## Comments and Javadoc

- Write comments in English using American spelling and avoid slang.
- Add descriptive Javadoc to every public class and public method, except:
  - straightforward getters and setters;
  - overrides whose inherited Javadoc applies exactly;
  - test classes and test methods.
- Use multiline Javadoc for public classes and methods, with `/**` and `*/` on their own lines.
- Start a method's first sentence with a third-person verb such as “Returns”, “Adds”, or “Sends”.
- Keep the first sentence a concise summary and end descriptions with punctuation.
- Separate the description from tags with one blank Javadoc line.
- Either document all parameters or omit all `@param` tags when the parameter names and main description are sufficient.
- Omit `@return` for `void` methods or when the return value is already obvious from the description.
- Document thrown exceptions when that information helps callers.
- Use `{@inheritDoc}` when an override needs additions to inherited documentation.
- Place no blank line between a Javadoc block and its declaration.
- Indent comments with the surrounding code. Trailing comments are allowed when clear.

For topics not covered here, use the Google Java Style Guide, as directed by the source standard.

