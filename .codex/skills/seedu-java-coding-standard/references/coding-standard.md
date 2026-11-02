# Project Java coding standard

This project follows the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). Apply the Google Java Style Guide only to topics that the SE-EDU guide does not cover. Existing project instructions choose American English where the source guide allows either spelling system.

## Naming

- Use lowercase package names rooted at the project name, such as `megia.model`.
- Name classes and enums with nouns in PascalCase. Name variables in camelCase, constants in SCREAMING_SNAKE_CASE, and methods with verbs in camelCase.
- Keep acronyms in normal word case inside identifiers, use English names, and choose longer names for wider scopes.
- Prefer `is`, `has`, `was`, `can`, or `should` for boolean variables and methods. A boolean setter has the form `setFound(boolean isFound)`.
- Use plural names for collections and arrays. Reserve `i` for a first loop or iterator and `j`, `k`, and later letters for nested loops.
- Give related constants a shared prefix so they group naturally.
- Test methods may use `featureUnderTest_scenario_expectedBehavior`; production method names do not use underscores.

## Layout

- Indent with four spaces and no tabs. Aim for at most 110 characters per line and never exceed 120.
- Indent continuation lines eight spaces beyond their parent. Break after commas and before operators, including `.`, `&` in type bounds, and `|` in multi-catch clauses. Keep a method or constructor name with its opening parenthesis and prefer higher-level expression breaks.
- Use K&R braces: the opening brace stays on the declaration or control-statement line. Format `if`/`else`, loops, `switch`, and `try`/`catch`/`finally` consistently with that style.
- Put spaces around operators, after Java keywords and commas, around ternary colons, and after `for`-loop semicolons.
- Separate logical units within a block with one blank line. Avoid surplus blank lines.

## Packages, imports, and declarations

- Put every class in a logical package. Keep the directory path aligned with the package name.
- List imports explicitly; do not use wildcard imports. Remove unused imports and keep a consistent grouping: static imports, Java/Jakarta imports, third-party imports, then project imports, with groups separated by one blank line.
- Attach array brackets to the type, as in `String[] arguments`.
- Declare and initialize variables in the smallest useful scope. Leave a variable uninitialized only when no valid initial value exists.
- Keep fields non-public unless they are constants or the containing type is a behavior-free data class.

## Control flow

- Give every loop and conditional a braced body, including single-statement bodies. Put the condition and body on separate lines.
- In colon-style switches, include `break` where needed and add `// Fallthrough` before every intentional fallthrough. Arrow-style switch rules do not need fallthrough comments.

## Comments and Javadocs

- Write comments in clear American English, without local slang, and indent them with the code they describe.
- Add Javadoc to every class and every public method or constructor. It may be omitted for getters/setters, test code, and overrides whose inherited documentation remains exactly correct.
- Start a method summary with a third-person verb such as `Returns`, `Creates`, or `Sends`. Put `/**` on its own line, align each `*`, and leave no blank line between the Javadoc and declaration.
- Separate the description from block tags with one blank Javadoc line. End every `@param`, `@return`, and `@throws` description with punctuation.
- Include either all `@param` tags or none. Omit them only when every parameter is self-explanatory or fully explained in the main description.
- Use `{@inheritDoc}` when an override needs to reuse and refine inherited documentation.
