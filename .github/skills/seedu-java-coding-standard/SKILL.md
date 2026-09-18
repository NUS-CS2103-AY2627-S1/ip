---
name: seedu-java-coding-standard
description: >-
  Use when writing, editing, reviewing, formatting, or generating Java code,
  Javadoc, tests, classes, methods, or imports in this project. Also use when
  checking naming, layout, braces, comments, or style against the SE Education
  Java coding standard.
disable-model-invocation: false
---

# SE-EDU Java coding standard

Follow the [SE Education Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for **all Java code** in this project. For topics not covered there, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Do not skip this skill because a change is small, generated, a test, or "already close enough."

## Naming

- Package names: all lowercase, project name then logical group (`alfred.ui`).
- Classes and enums: nouns in `PascalCase`.
- Variables: `camelCase`. Methods: verbs in `camelCase`.
- Constants: `SCREAMING_SNAKE_CASE`. Give related constants a common prefix (`COLOR_RED`, `COLOR_GREEN`).
- Treat acronyms as ordinary words: `exportHtmlSource()`, not `exportHTMLSource()`.
- Identifiers in English. Large-scope names are descriptive; short names only for small scopes.
- Loop iterators: `i` first; `j`, `k` only for nested loops.
- Collections and arrays: plural names (`points`, `values`).
- Booleans read as predicates, preferably `is`, `has`, `can`, `should`, or `was`. Boolean setters:

```java
void setFound(boolean isFound);
```

- Test methods may use `featureUnderTest_testScenario_expectedBehavior`. The scenario and/or expected-behavior parts may be omitted.

## Layout

- Indent with **4 spaces**. Never tabs.
- Soft limit 110 characters; hard limit **120**. Wrap with **8 extra spaces** beyond the parent line.
- Break after a comma; break before an operator (including `.`, `&` in type bounds, `|` in multi-catch). Keep a method or constructor name attached to `(`. Prefer higher-level breaks.
- K&R braces: `{` on the same line as the declaration or control statement.
- Always brace `if`/`else`/`for`/`while` bodies, including single-statement bodies. Put the conditional body on its own line.
- `else`, `catch`, and `finally` on the same line as the preceding `}`.
- Traditional `switch`: add `// Fallthrough` when a `case` has no `break` on purpose.
- Spaces around operators and ternary `:`; space after keywords, commas, and `for` semicolons.
- Separate logical units in a block with one blank line.

```java
if (condition) {
    statements;
} else {
    statements;
}

try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
```

## Packages, imports, types, variables

- Every class is in a package.
- Import order must be **consistent** across the project. Import types explicitly; no `java.util.*`.
- Array brackets attach to the type: `int[] values`.
- Declare variables in the smallest scope. Initialize where declared when a valid value exists. Do not invent a placeholder value just to initialize.
- Do not expose mutable class variables as `public`. Public fields are only for constants or behavior-free data classes.

## Comments and Javadoc

- English, American spelling. No slang or locally specific expressions.
- Header comments for **every class and public method**, except:
  - straightforward getters and setters
  - overrides whose parent Javadoc still applies exactly
  - test classes and test methods
- Method summaries start with a third-person verb: `Returns ...`, `Adds ...`, `Sends ...` (not `Return` or `Returning`).
- Form:

```java
/**
 * Returns the location for the specified position.
 *
 * @param position Position to evaluate.
 * @return Computed location.
 * @throws IllegalArgumentException If the position is invalid.
 */
public Location computeLocation(Position position) {
    // ...
}
```

- `/**` on its own line for that form. Align each `*`. One space after each `*`.
- Blank line between the description and tags. No blank line between the block and the declaration.
- End `@param` / `@return` / `@throws` descriptions with punctuation.
- All useful `@param` tags, or none. Omit tags that only repeat self-explanatory names.
- Omit `@return` when the method is `void` or the return value is already obvious.
- `{@inheritDoc}` when the parent comment applies but needs extra detail. If the parent comment does **not** describe the override (for example a default that subclasses change), write a full comment instead of omitting it.
- Short class-member comments may be one line: `/** Number of connections to this database */`
- Indent comments with the code they describe.

## Common mistakes

| Mistake | Required |
| --- | --- |
| Skip Javadoc on `main` or other public methods | Document them unless an exception above applies |
| Omit Javadoc on an override whose parent text is about the default | Write override-specific Javadoc |
| `if (done) doWork();` | Braces, body on the next line |
| `import java.util.*;` | Explicit imports |
| Subject-style Javadoc (`Return the list`) | Third-person verb (`Returns the list`) |
