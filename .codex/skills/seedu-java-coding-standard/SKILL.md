---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to production and test Java code in this project.
---

# SE-EDU Java Coding Standard

Apply the basic and intermediate rules in the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to every Java file changed in this project. For topics the guide does not cover, use the Google Java Style Guide.

## Names and declarations

- Put every class in a package. Use a lowercase project or group name as the root package, followed by logical package names.
- Use PascalCase English nouns for classes and enums, camelCase English verbs for methods, and camelCase English names for variables. Test methods may use `featureUnderTest_testScenario_expectedBehavior()`; omit unneeded trailing parts.
- Use `UPPER_SNAKE_CASE` only for constants, and give related constants a common prefix. Use lowercase acronyms within compound names, such as `openDvdPlayer`.
- Name collections in plural form. Give wide-scope variables descriptive names; use short scratch names only in a small local scope, with `j` and later indices reserved for nested loops.
- Name boolean variables and methods as booleans, preferably with `is`, `has`, `can`, `should`, or `was`. Use `setX(boolean isX)` for boolean setters.
- Declare variables in the smallest practical scope and initialize them at declaration when a real initial value is available. Keep class fields non-public unless they are constants or the type is a behavior-free data class.

## Layout and statements

- Indent with four spaces, avoid tabs, and keep lines at or below 120 characters (prefer 110 or fewer). Indent continuation lines by eight additional spaces.
- When wrapping lines, favour readability: break after commas, before operators (including `.`, `&`, and `|`), keep a method or constructor name with its following `(`, and prefer a higher-level break over one inside a nested expression.
- Use K&R braces. Put spaces around binary and ternary operators, after commas and semicolons, and between control-flow keywords and `(`.
- Separate logical units in a block with one blank line.
- Always use braces and a separate line for `if`, `else`, `for`, `while`, and `do` bodies. In a classic `switch`, use `break` after each case unless an intentional fall-through is marked with `// Fallthrough`; include a `default` branch.
- Use explicit, consistently ordered, minimal imports; never use wildcard imports. Keep the project’s configured checkstyle import order.
- Attach array brackets to the type, not the variable.

## Comments

- Write comments and identifiers in American English without local slang. Indent comments at the same level as the code they describe.

## Javadoc

- Write descriptive Javadoc header comments for public classes and public methods. Omit them only for getters/setters, test classes and methods, or an overriding method when its inherited Javadoc applies exactly.
- Put `/**` on its own line. Begin with a concise summary sentence using a third-person action such as `Returns`, `Sends`, or `Adds`; align subsequent `*` characters and put one space after each.
- Separate the description from tags with one blank Javadoc line, but do not leave a blank line between the Javadoc block and its declaration. End parameter descriptions with punctuation.
- Document either every parameter or none: omit all `@param` tags only when the parameter names are self-explanatory or already explained in the description. Omit `@return` for `void` methods or when the return value is obvious from the rest of the comment.
- Use `@inheritDoc` for overrides when it accurately reuses the parent contract; add details when the behaviour differs. A Javadoc comment for a class member may use the one-line form.
