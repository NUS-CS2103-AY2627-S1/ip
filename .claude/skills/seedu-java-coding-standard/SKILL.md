---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (intermediate level) that all Java code in this project must follow - naming, layout, imports, statements, and Javadoc rules. Use whenever writing, reviewing, or refactoring Java in this repository, and when checking existing code for style violations.
---

# SE-EDU Java coding standard

Source: https://se-education.org/guides/conventions/java/intermediate.html

All Java in this repository follows these rules. Apply them when writing new
code and when touching existing code; do not leave a file worse than you found
it.

## Naming

| Thing | Rule | Example |
| --- | --- | --- |
| Package | all lower case | `hermes.task` |
| Class / enum | noun, PascalCase | `Line`, `AudioSystem` |
| Variable | camelCase | `line`, `audioSystem` |
| Constant (`static final`) | SCREAMING_SNAKE_CASE | `MAX_ITERATIONS`, `COLOR_RED` |
| Method | verb, camelCase | `getName()`, `computeTotalWidth()` |
| Test method | `featureUnderTest_testScenario_expectedBehavior()` | `sortList_emptyList_exceptionThrown()` |

- Abbreviations and acronyms are **not** uppercased inside a name:
  `exportHtmlSource()` not `exportHTMLSource()`.
- All names in English.
- Large scope means a long name; small scope may have a short one. `i, j, k, m,
  n` for scratch integers, `c, d` for characters.
- Booleans sound like booleans — prefix `is`, `has`, `was`, `can`, `should`:
  `isSet`, `hasData`, `wasOpen`, `hasLicense()`, `canEvaluate()`.
- **Collections take a plural name**: `Collection<Point> points`, `int[] values`.
- Iterator variables may be `i`, then `j`/`k` for nested loops only.
- Associated constants share a prefix: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.

## Layout

- **4 spaces** indentation, never tabs.
- Line length: soft limit **110**, hard limit **120**.
- Wrapped lines are indented **8 spaces** (twice normal) past the parent line.
- Break **after** a comma, **before** an operator (including `.`, `&`, `|`).
- Keep a method name attached to its opening parenthesis when wrapping.
- Prefer higher-level breaks over breaks inside nested parentheses.
- K&R (Egyptian) braces — opening brace on the same line:

```java
while (!done) {
    doSomething();
    done = moreToDo();
}
```

- `if`/`else`, `for`, `while`, `do-while`, `switch` and `try-catch` follow the
  forms in the source document. An arrow `switch` is fine; a `case` in a
  colon-style switch that falls through needs an explicit `// Fallthrough`.
- Whitespace: operators surrounded by spaces; reserved words followed by a
  space; commas followed by a space; semicolons in `for` followed by a space.
  `a = (b + c) * d;` and `while (true) {`, not `a=(b+c)*d;` or `while(true){`.
- Separate logical units within a block by one blank line.

## Statements

- **Every class belongs to a package.**
- Import order must be consistent, in blocks separated by a blank line:
  1. `import static ...`
  2. `java.*`
  3. `javax.*`
  4. third-party (`org.*`, `com.*`, ...)
  5. this project's own packages
- **List imports explicitly. Never `import x.y.*;`**
- Array specifiers attach to the type: `int[] a`, not `int a[]`.
- Initialize variables where they are declared, in the smallest scope possible.
- Class variables are never `public` unless the class is a data class with no
  behavior. Constants are exempt.
- A loop body is always wrapped in braces, however short.
- A conditional goes on its own line, and its body is always braced:

```java
if (isDone) {
    doCleanup();
}
```

Never `if (isDone) doCleanup();` and never an unbraced body on the next line.

## Comments

- English, American spelling, no local slang.
- **Header comments are required for every class and every public method.**
  They may be omitted only for:
  1. getters and setters,
  2. overriding methods, when the parent's Javadoc applies exactly as is,
  3. classes and methods used for testing.
- Javadoc form:

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
public double computeLocation(double x, double y, int zone) {
```

  - `/**` on its own line, subsequent `*` aligned, a space after each `*`.
  - First sentence is a short summary starting with a verb in the third person:
    `Returns ...`, `Sends ...`, `Adds ...`.
  - Blank line between the description and the tag block.
  - Punctuation after each parameter description.
  - No blank line between the Javadoc and what it documents.
  - `@return` may be omitted when the method returns nothing or the value is
    obvious; `@param` may be omitted when names are self-explanatory or the
    description already covers them.
  - `{@inheritDoc}` reuses a parent's comment.
- Single-line Javadoc is fine for a field: `/** Number of connections. */`
- Comments are indented to match the code they describe. Trailing comments are
  allowed.

## Checking a file

These catch most violations quickly:

```bash
awk 'length > 120 {printf "%s:%d (%d chars)\n", FILENAME, FNR, length}' $(find src -name '*.java')
grep -rn "^import .*\*;" src/
grep -rnE "if \(.*\) [^{]+;" src/
```

The remaining rules — Javadoc coverage, import ordering, naming — need reading
the file. When adding a public method, write its header comment in the same
edit; it is not optional in this project.
