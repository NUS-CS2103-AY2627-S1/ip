---
name: seedu-java-coding-standard
description: >-
  Applies the SE-EDU Java coding standard (basic + intermediate) from
  se-education.org. Use when writing, editing, reviewing, or generating any
  Java code in this project — including classes, tests, Javadoc, naming,
  imports, layout, braces, and comments.
---

# SE-EDU Java coding standard

Follow **basic + intermediate** rules from
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
For anything not covered there, use the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

**Mandatory for all Java in this project** (`src/main/java` and `src/test/java`).
Apply these rules whenever you write, edit, or review Java. Do not skip this skill.

Full examples: [reference.md](reference.md).

## Workflow

1. Read this skill before changing Java.
2. Write or edit the code to match every rule below.
3. Recheck the checklist before finishing.

## Checklist

- [ ] Naming (packages, types, members, booleans, collections, tests)
- [ ] 4-space indent, K&R braces, lines ≤ 120 (prefer ≤ 110)
- [ ] Wrapped lines: +8 spaces; break after commas, before operators
- [ ] Braces on every `if`/`else`/`for`/`while`/`do`; conditionals on their own line
- [ ] Explicit imports, consistent group order, no wildcards
- [ ] `int[] values` not `int values[]`; init variables at declaration, smallest scope
- [ ] No public fields except constants (or a data class with no behavior)
- [ ] Javadoc on every class and public method (see exceptions)
- [ ] Comments in American English, indented with the code

---

## Naming

| Kind | Rule | Examples |
| --- | --- | --- |
| Packages | all lower case | `kiwi.task` |
| Classes / enums | nouns, PascalCase | `TaskList`, `KiwiDate` |
| Methods | verbs, camelCase | `getName()`, `computeTotalWidth()` |
| Variables | camelCase | `line`, `audioSystem` |
| Constants | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS` |
| Language | English only | — |

- Do **not** uppercase abbreviations in names: `exportHtmlSource()` not `exportHTMLSource()`.
- Large-scope names should be long; tiny-scope scratch vars may be short (`i`, `j`, `k`, `n`, `c`).
- Nested-loop indices: `j`, `k` only inside nested loops.
- Booleans should *sound* boolean. Prefer prefixes `is`, `has`, `was`, `can`, `should`: `isDone`, `hasLicense()`, `shouldAbort`.
- Boolean setters: `void setFound(boolean isFound)`.
- Collections/arrays: plural names: `List<Task> tasks`, `int[] values`.
- Related constants share a prefix: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- Tests may use underscores: `featureUnderTest_testScenario_expectedBehavior()`, e.g. `parse_emptyList_exceptionThrown()`. The last part, or last two parts, may be omitted when the test covers all remaining cases.

Package root is the project name (`kiwi`), not `edu.nus.*`.

---

## Layout

- Indent with **4 spaces**, never tabs.
- Hard line limit **120** characters; try to stay under **110**.
- Continuation indent is **8 spaces** beyond the parent line.
- When wrapping: break **after** a comma; break **before** an operator (including `.`, `&` in type bounds, `|` in multi-catch). Keep the method/constructor name attached to `(`.
- Prefer a higher-level break over a lower-level one.
- Ternary: one line, or break before `?` and before `:`.
- **K&R (Egyptian) braces**: `{` on the same line as `if`/`for`/`while`/`try`/`else`/`catch`/`class`/method.

Statement forms (always with braces):

```java
public void someMethod() throws SomeException {
    ...
}

if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}

for (initialization; condition; update) {
    statements;
}

while (condition) {
    statements;
}

do {
    statements;
} while (condition);

switch (condition) {
    case ABC:
        statements;
        // Fallthrough
    case DEF:
        statements;
        break;
    default:
        statements;
        break;
}

try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
```

Include `// Fallthrough` whenever a `case` does not `break` (or `return`/`throw`).

Whitespace: spaces around operators; space after reserved words (`if (`, `while (`, `catch (`); space after commas; space after semicolons in `for`; spaces around `:` when it is a binary/ternary operator (not `case ABC:`).

Separate logical units in a block with one blank line.

---

## Statements

### Imports and packages

- Every class lives in a package.
- No wildcard imports. List each class: `import java.util.List;` not `import java.util.*;`.
- Keep import **order consistent**. Groups, each internally alphabetical, separated by one blank line:

  1. static imports
  2. `java` / `javax`
  3. third-party (`org`, `com`, `javafx`, …)
  4. project (`kiwi`)

### Types and variables

- Array braces on the type: `int[] values` not `int values[]`.
- Declare in the smallest scope; initialize at the declaration when a real value exists. Do not invent a dummy value just to initialize.
- Class fields must not be `public` unless the type is a data class with no behavior. Constants (`static final`) are exempt.

### Loops and conditionals

- Always use `{ }` for loop and `if`/`else` bodies, even for one statement.
- Put the body on the next line (never `if (isDone) doCleanup();`).

---

## Comments and Javadoc

- English, **American spelling**, no local slang.
- Indent comments with the surrounding code (trailing comments on the same line are allowed).
- **Required** header comments: every class and every public method.
- **May omit** Javadoc for: (i) getters/setters, (ii) overrides when the parent Javadoc still applies exactly, (iii) test classes/methods.

Method Javadoc:

```java
/**
 * Returns lateral location of the specified position.
 * If the position is unset, NaN is returned.
 *
 * @param x X coordinate of position.
 * @param y Y coordinate of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If zone is <= 0.
 */
```

Rules:

- `/**` on its own line; `*` aligned; space after each `*`.
- First sentence is a short summary (Javadoc uses it as the table blurb).
- Method first sentence starts with a verb: `Returns …`, `Adds …`, `Parses …` — not `Return` / `Returning`.
- Blank line between the description and the tag block.
- Period at the end of each `@param` / `@return` / `@throws` description.
- No blank line between the Javadoc block and the method/class.
- `@return` may be omitted when there is no return value, or it is obvious from the description.
- Either document **all** `@param`s or **none** (omit them when names/description already explain every parameter).
- Overrides that change behavior may use `{@inheritDoc}` plus extra text.
- Fields may use a one-line Javadoc: `/** Number of connections to this database */`.
