# SE-EDU Java coding standard: basic and intermediate rules

This project follows the basic and intermediate rules from the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Use Google Java Style for topics not covered here.

## Naming

- Use lowercase package names. For a school project, start with the project or group name and then logical subpackages; do not claim an institutional namespace such as `edu.nus.comp`.
- Use English noun names in PascalCase for classes and enums.
- Use English verb names in camelCase for methods.
- Use camelCase for variables and SCREAMING_SNAKE_CASE for constants. Give associated constants a common prefix.
- In names containing abbreviations or acronyms, do not capitalize the entire abbreviation: use forms such as `exportHtmlSource` and `openDvdPlayer`.
- Name booleans so they read as booleans, normally with `is`, `has`, `was`, `can`, or `should`. Boolean setters use forms such as `setFound(boolean isFound)`.
- Use plural names for collections and arrays.
- Match name length to scope: descriptive names for broad scope; short scratch names only for a few nearby lines. Use `i`, `j`, and `k` for iterators, reserving later letters for nested loops.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`; the scenario and expected-behavior parts may be omitted when the test covers all variants.

## Layout

- Indent with four spaces, never tabs.
- Keep lines below 120 characters and aim for fewer than 110. Wrap continuations eight spaces beyond the parent line.
- Wrap for readability: break after commas and before operators, including `.`, `&` in bounds, and `|` in multi-catch. Keep a method or constructor name attached to its opening parenthesis, and prefer higher-level breaks.
- Use K&R braces. Put opening braces on the declaration or control-statement line.
- Put one space around operators, after Java keywords, after commas, around ternary colons, and after semicolons in `for` clauses.
- Separate distinct logical units within a block with one blank line.
- Format `if`/`else`, loops, `try`/`catch`/`finally`, methods, and switches consistently with K&R style. Indent `case` labels one level inside a `switch`.
- Add `// Fallthrough` whenever a colon-style switch case intentionally continues into the next case.

## Packages, imports, and types

- Put every class in a package.
- Keep import ordering consistent. Group static imports first, followed by standard Java/Javax imports and then third-party or project imports, with blank lines between groups.
- Import every type explicitly; never use wildcard imports. Keep imports minimal and remove unused imports.
- Attach array brackets to the type, for example `int[] values`, not `int values[]`.

## Variables

- Initialize variables where declared and declare them in the smallest practical scope. If no valid initial value exists yet, leave the variable uninitialized rather than assigning a fake value.
- Do not expose class variables publicly unless they are constants or belong to a behavior-free data class.

## Loops and conditionals

- Always put loop and conditional bodies in braces, including single-statement bodies.
- Put a conditional and its body on separate lines.

## Comments and Javadoc

- Write comments in English using American spelling, and indent them with the code they explain.
- Add descriptive Javadoc header comments to every public class and public method. A header may be omitted for a getter/setter, a test declaration, or an override whose inherited Javadoc applies exactly as written.
- Start Javadoc with a short summary sentence in third-person verb form, such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line for class and method Javadoc. Align subsequent `*` characters, leave a space after each `*`, and do not leave a blank line between the comment and declaration.
- Separate the description from block tags with a blank Javadoc line. Punctuate parameter descriptions.
- Include `@param` for either all parameters or none. Omit them only when every parameter is self-explanatory or fully explained in the description.
- `@return` may be omitted for `void` methods or when the returned value is already obvious from the description.
- Use `{@inheritDoc}` when inherited documentation needs to be reused or extended.
- Single-line Javadoc is allowed for members, for example `/** Number of active connections. */`.
