# Project Java coding standard

This is the project-focused checklist derived from the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). The official guide is authoritative; use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics it does not cover.

## Naming

- Use lowercase package names rooted in the project or group name, followed by logical subpackages.
- Name classes and enums with nouns in PascalCase.
- Name variables in camelCase and methods with verbs in camelCase.
- Name constants in `SCREAMING_SNAKE_CASE`. Give associated constants a common prefix.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`; omit the second or third part when it adds no value.
- Treat abbreviations and acronyms as ordinary words inside names (`exportHtmlSource`, not `exportHTMLSource`).
- Use English names. Let a name's descriptiveness match its scope; reserve short scratch names for a few nearby lines.
- Make boolean names read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`. Boolean setters take a correspondingly named parameter, such as `setFound(boolean isFound)`.
- Use plural names for collections. Use `i` for a simple iterator and `j`, `k`, and later letters only for nested iterators.

## Layout and whitespace

- Indent with four spaces and never tabs.
- Prefer lines shorter than 110 characters and never exceed 120 characters.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas and before operators or operator-like tokens such as `.`, `&`, and `|`. Keep a method or constructor name with its opening parenthesis and prefer higher-level breaks.
- Use K&R braces. Put method braces, control-flow braces, `else`, `catch`, and `finally` in the forms shown by the official guide.
- Put spaces around binary and ternary operators, after Java keywords and commas, and after semicolons in `for` headers.
- Separate logical units within a block with a blank line.

## Statements and declarations

- Put every class in a package.
- Keep import ordering consistent, separate import groups, list every imported class explicitly, and remove unused imports. Do not use wildcard imports.
- Attach array brackets to the type (`String[] args`).
- Declare variables in the smallest useful scope and initialize them at declaration when a valid value is available. Declare one variable per statement.
- Do not expose class variables publicly unless the class is a behavior-free data class; constants are exempt.
- Always use braces around loop and conditional bodies, including one-line bodies, and put the body on a separate line.
- Put `default` last in a `switch`. Add `// Fallthrough` before any traditional `case` that intentionally falls through; arrow cases do not need it.

## Comments and Javadocs

- Write comments in English using American spelling and no local slang. Indent comments with the code they describe.
- Add descriptive Javadocs to every class and public method. They may be omitted for tests, ordinary getters/setters, and overrides whose inherited contract applies exactly.
- Start a Javadoc summary with a concise third-person verb such as “Returns”, “Adds”, or “Creates”, and end sentences and tag descriptions with punctuation.
- Put `/**` on its own line, align subsequent `*` characters, include a blank line before tags, and place the Javadoc immediately before the declaration.
- Either document all parameters or omit all `@param` tags when every parameter is already self-explanatory. Omit `@return` only when the return value is already obvious.
- Use `{@inheritDoc}` when an override needs to extend or qualify inherited documentation.
