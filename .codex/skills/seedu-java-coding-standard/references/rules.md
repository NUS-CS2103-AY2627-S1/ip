# Java rules checklist

This is a project-focused checklist derived from the SE-EDU Java coding standard (basic and intermediate rules). Consult the canonical source when a case is ambiguous.

## Naming

- Use lowercase package names organized under the project root package.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Keep acronyms lowercase within identifiers, such as `parseXml`, not `parseXML`.
- Use English names. Give broad-scope variables descriptive names and reserve short names for small-scope scratch values.
- Name booleans to read as booleans, preferably with prefixes such as `is`, `has`, `can`, or `should`.
- Use plural names for collections.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout and whitespace

- Indent with 4 spaces, never tabs.
- Keep lines below 120 characters and aim for less than 110. Indent wrapped continuations 8 spaces beyond the parent line.
- Break after commas and before operators when wrapping. Keep method names attached to their opening parenthesis.
- Use K&R braces: the opening brace stays on the declaration or control-statement line.
- Put method bodies on separate lines rather than compressing declarations and statements together.
- Surround binary operators with spaces. Put spaces after commas and Java keywords.
- Separate distinct logical units within a block with one blank line.

## Statements and types

- Put every class in a package matching its directory below `src/main/java` or `src/test/java`.
- Keep import ordering consistent, list imports explicitly, and do not use wildcard imports.
- Attach array brackets to the type, such as `int[] values`.
- Initialize variables at declaration when a valid value is available and declare them in the smallest useful scope.
- Do not expose mutable class fields publicly; constants are exempt.
- Always use braces around loop and conditional bodies, including single statements.
- Put conditional bodies on lines separate from their conditions.
- Add an explicit `// Fallthrough` comment to any traditional switch case that intentionally falls through.

## Comments and Javadocs

- Write comments in English using American spelling.
- Add descriptive Javadocs to every public class and public method, except self-explanatory getters/setters, tests, and overrides whose inherited contract applies unchanged.
- Start method summaries with a third-person verb such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line for class and method Javadocs, align each `*`, and leave no blank line between the Javadoc and declaration. Simple member comments may use the permitted single-line form.
- Separate the summary/details from tags with a blank Javadoc line.
- End parameter and return descriptions with punctuation when they are full descriptions.
- Include all `@param` tags or none; omit them only when every parameter is already self-explanatory or explained in the main description.
- Use `@return` and `@throws` where they add information not already obvious from the summary.
- Indent comments to the same logical level as the code they describe.

## Final audit

- Scan all touched Java files for lines over 120 characters.
- Confirm compilation and automated tests pass.
- When the standard does not address a topic, apply the Google Java Style Guide consistently.
