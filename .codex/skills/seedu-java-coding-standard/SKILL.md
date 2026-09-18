---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when writing, editing, or reviewing Java source and test code in this project. Use for every Java code change; use Google Java Style for topics the SE-EDU standard does not cover.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for every Java source and test file in this project. Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics the SE-EDU standard does not cover.

## Apply the standard

Before editing Java, inspect the target file and nearby classes so names, imports, and layout remain consistent. After editing, review every changed Java line against the checks below and run the verification required by `AGENTS.md`.

### Naming

- Use lowercase package names and English names throughout.
- Name classes and enums as nouns in PascalCase.
- Name variables in camelCase, constants in SCREAMING_SNAKE_CASE, and methods as verbs in camelCase.
- In test methods, use `featureUnderTest_testScenario_expectedBehavior`; omit later parts only when they add no useful distinction.
- Treat acronyms as words inside identifiers, such as `exportHtmlSource`, not `exportHTMLSource`.
- Give wider-scope variables longer, descriptive names. Reserve short names such as `i`, `j`, and `c` for small local scopes; use `j` and later iterator names only for nested loops.
- Name booleans to read as predicates, preferably with `is`, `has`, `was`, `can`, or `should`. Name boolean setters like `setFound(boolean isFound)`.
- Use plural names for collections and arrays. Give related constants a common prefix.

### Layout

- Indent with 4 spaces, never tabs. Keep lines below 110 characters where practical and never exceed 120 characters.
- Indent wrapped continuation lines 8 spaces beyond their parent line. Break after commas and before operators, including `.`, `&`, and `|`; keep a method or constructor name attached to its opening parenthesis.
- Prefer high-level expression breaks and wrap for readability rather than accepting formatter output mechanically.
- Use K&R braces: the opening brace stays on the declaration or control-statement line. Follow the standard forms for methods, conditionals, loops, `switch`, `try`/`catch`, and `finally`.
- Put spaces around operators, after Java keywords and commas, around ternary colons, and after semicolons in `for` headers.
- Separate logical units within a block with one blank line.

### Statements

- Put every class in a package.
- Keep imports consistently ordered in groups: static imports, `java`, `javax`, third-party packages, then project packages. Import every type explicitly; never use wildcard imports. Keep imports minimal and current.
- Attach array brackets to the type, as in `int[] values`.
- Initialize variables where declared and declare them in the smallest useful scope. Do not use placeholder values when a valid initial value is unavailable.
- Keep class variables non-public unless the class is a behavior-free data class; constants may be public.
- Always use braces around loop and conditional bodies. Put each conditional body on separate lines.
- Add `// Fallthrough` before every intentional fallthrough from a colon-style `switch` case.

### Comments and Javadocs

- Write comments in English with American spelling and no local slang. Indent comments with the code they describe.
- Add descriptive Javadocs to every class and public method. Javadocs may be omitted for getters/setters, test code, and exact-behavior overrides whose inherited documentation remains correct.
- Start the first Javadoc sentence with a concise third-person verb such as `Returns`, `Sends`, or `Adds`.
- Put `/**` on its own line for multi-line Javadocs, align each `*`, leave one blank Javadoc line before tags, and place no blank source line between the Javadoc and declaration.
- End every parameter description with punctuation. Include either all `@param` tags or none; omit them only when every parameter is self-explanatory or already explained in the main text.
- Omit `@return` only for `void` methods or when the return value is already obvious. Use `{@inheritDoc}` when an override needs additions to inherited documentation.

## Final audit

Confirm that changed Java files have no tabs, wildcard imports, lines over 120 characters, brace-less conditionals or loops, non-predicate boolean names, undocumented public APIs outside the stated exceptions, or Javadoc parameter descriptions without punctuation. Preserve program behavior unless the user requested a functional change.
