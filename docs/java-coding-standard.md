# Java Coding Standard (Basic + Intermediate)

This is a project summary of the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
For topics it does not cover, use the Google Java Style Guide. Consult this document before and
during every Java code change.

## Naming

- Package names are lowercase. For student projects, begin with the project or group name and
  use logical subpackages; do not use `edu.nus.comp.*`.
- Class and enum names are English nouns in PascalCase.
- Method names are English verbs in camelCase. Variable names use camelCase.
- Constants use `SCREAMING_SNAKE_CASE`; related constants share a prefix.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`, omitting later
  parts only when appropriate.
- Write acronyms as normal words within names (`exportHtmlSource`, not `exportHTMLSource`).
- Use descriptive, longer names for broad-scope variables and short names only for narrow-scope
  scratch values or loop indices. Use `j`, `k`, and similar names only for nested-loop indices.
- Name booleans to read as booleans, preferably with `is`, `has`, `was`, `can`, or `should`.
  Boolean setters use `setX(boolean isX)`.
- Use plural names for collections.

## Layout and whitespace

- Indent with four spaces, never tabs.
- Keep lines under 110 characters where possible and never exceed 120 characters. Indent wrapped
  lines eight spaces relative to the parent line.
- Wrap for readability: normally break after commas and before operators (including `.`, `&`, and
  `|`); keep a method or constructor name attached to its following `(`; prefer higher-level
  expression breaks.
- Use K&R braces: the opening brace stays on the declaration or control-statement line.
- Use the standard layouts for methods, `if`/`else`, loops, `switch`, and `try`/`catch`/`finally`
  blocks. Put `else` on the same line as the preceding closing brace.
- In a traditional `switch`, include `// Fallthrough` whenever a non-empty case intentionally lacks `break`.
- Put spaces around operators, after reserved words, after commas, and after `for` semicolons.
  Put spaces around ternary/binary colons.
- Separate logical units in a block with one blank line.

## Statements, imports, and variables

- Put every class in a package.
- Keep import ordering consistent, list imported classes explicitly, and do not use wildcard imports.
- Attach array brackets to the type: `int[] values`, not `int values[]`.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid
  initial value exists. Do not use fake initial values merely to initialize early.
- Do not expose mutable class variables as `public`; use encapsulation instead. Constants are
  exempt, and behavior-free data classes are the limited exception.
- Always use braces around loop bodies and conditional bodies, including single statements. Put a
  conditional on its own line.

## Comments and Javadoc

- Write comments in English, with American spelling and no local slang.
- Add descriptive header Javadoc for every class and public method, except getters/setters, exact
  inherited overrides, and test classes/methods.
- Use proper Javadoc formatting: `/**` starts on its own line; the first sentence is a short
  summary beginning with a third-person verb such as “Returns”, “Adds”, or “Sends”; align `*`
  characters and leave a space after each one.
- Leave a blank line between description and tags; give parameter descriptions punctuation; place
  no blank line between Javadoc and the documented declaration.
- Omit `@return` for void methods or when the return is already obvious. Include either all
  `@param` tags or none, depending on whether they add value. Use `@inheritDoc` when an override
  needs inherited documentation with adjustments.
- A simple member Javadoc may be one line. Indent comments to match the code they describe;
  trailing comments are allowed when useful.
