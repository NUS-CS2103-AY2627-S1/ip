---
name: seedu-java-coding-standard
description: The project's mandatory Java coding standard, based on the SE-EDU intermediate Java conventions (https://se-education.org/guides/conventions/java/intermediate.html). Load this before writing or reviewing any Java code in this project — new files, edits to existing files, and code review alike — to check naming, layout, statement formatting, and comment conventions.
---

# seedu-java-coding-standard

This project's Java code must follow the SE-EDU intermediate Java coding standard
(https://se-education.org/guides/conventions/java/intermediate.html). For anything not covered below, fall
back to the Google Java Style Guide.

Use this skill before writing new Java code, before editing existing Java code, and when reviewing a diff —
check the code against every applicable rule below, not just the one most obviously related to the change
at hand.

## Naming

- Package names: all lower case (e.g. `bott.task`).
- Class/enum names: nouns, PascalCase (e.g. `TaskList`).
- Variable names: camelCase.
- Constant names: `SCREAMING_SNAKE_CASE` (e.g. `MAX_ITERATIONS`).
- Method names: verbs, camelCase (e.g. `getName()`, `computeTotalWidth()`).
  - Test methods may use `featureUnderTest_testScenario_expectedBehavior()`, e.g.
    `sortList_emptyList_exceptionThrown()`. The scenario and/or expected-behavior parts can be omitted
    depending on what the test covers.
- Abbreviations/acronyms inside a name are not all-uppercase: `exportHtmlSource()`, not
  `exportHTMLSource()`.
- All names are in English.
- Scope-appropriate name length: short scope → short name (`i`, `j`, `k`, `m`, `n` for integers; `c`, `d`
  for characters are fine as scratch variables); large scope → long, descriptive name.
- Boolean variables/methods read like booleans, using a prefix such as `is`, `has`, `was`, `can`,
  `should`: `isSet`, `hasData`, `boolean hasLicense()`. Boolean setters: `void setFound(boolean isFound)`.
- Collections/arrays use plural names: `List<Task> tasks;`, `int[] values;`.
- Iterator variables: `i`, `j`, `k`, ...; `j`/`k` etc. only for nested loops.
- Associated constants share a common prefix so they sort and group together, e.g. `COLOR_RED`,
  `COLOR_GREEN`, `COLOR_BLUE`.

## Layout

- Indentation: 4 spaces, never tabs.
- Line length: soft limit 110 characters, hard limit 120. Wrap longer lines at a sensible point.
- Wrapped lines: indent 8 spaces (twice the normal 4) from the parent line.
- Line breaks: break **after** a comma, break **before** an operator (including `.`, the `&` in type
  bounds, and the `|` in multi-catch). A method/constructor name stays attached to its opening `(`.
  Prefer breaking at a higher syntactic level over a lower one.
  ```java
  totalSum = a + b + c
          + d + e;
  method(param1,
          object.method()
                  .method2(),
          param3);
  ```
- Braces: K&R ("Egyptian") style — opening brace on the same line, never its own line.
  ```java
  while (!done) {
      doSomething();
  }
  ```
- Standard forms for method declarations, `if`/`else if`/`else`, `for`, `while`/`do-while`, `switch`
  (traditional `case:`/`break;` or arrow `case ->` form — either is fine, but a fallthrough `case` without
  a `break` must carry an explicit `// Fallthrough` comment), and `try`/`catch`/`finally` — see the
  reference URL for the exact layouts if in doubt; this codebase already follows them.
- Whitespace: a space around binary operators, after reserved words before `(`, after commas, and after
  the semicolons in a `for` header. Never `a=(b+c)*d;` or `for(i=0;i<10;i++){`.
- Separate logical units within a block with one blank line (often introduced by a comment on the unit
  that follows).

## Statements

- Every class lives in a package (no default/unnamed package).
- Import order: static imports, then `java.*`, then `javax.*`, then third-party packages, then this
  project's own packages — each group separated by a blank line, alphabetical within a group. (This
  project uses `java.io` → `java.time` → `java.util` → this project's own `bott.*` imports, alphabetized
  within each group.)
- Import classes explicitly; never `import java.util.*;`.
- Array specifiers attach to the type, not the variable: `int[] a`, not `int a[]`.
- Declare and initialize variables at the point of first use, in the smallest scope that needs them —
  not all at the top of a method.
- Non-constant fields are never `public` unless the class is a pure data class with no behavior.
- Loop bodies are always wrapped in `{ }`, even for a single statement.
- A conditional's test is never on the same line as its body — `if (isDone) { doCleanup(); }`, never
  `if (isDone) doCleanup();` — and its body is always wrapped in `{ }`, even for a single statement.

## Comments

- All comments in English, American spelling.
- Every class and every public method needs a header comment, except: getters/setters, an overridden
  method whose parent Javadoc already applies exactly as-is, and test code.
- Javadoc form:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param y Y coordinate of position.
   * @param zone Zone of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  public double computeLocation(double x, double y, int zone)
          throws IllegalArgumentException {
      // ...
  }
  ```
  - Opening `/**` on its own line; first sentence is a short summary (it appears in generated indices),
    phrased as "Returns ...", "Adds ...", "Sends ..." (not "Return"/"Adding"/etc.).
  - A blank line separates the description from the `@param`/`@return`/`@throws` block.
  - `@return` can be omitted if the method returns nothing or the return value is obvious.
  - `@param` is included for every parameter, or omitted for all of them (never a mix) — omit only if
    every parameter name is already self-explanatory or explained in the main text.
  - `@inheritDoc` may be used to reuse and extend a parent method's Javadoc.
  - A single-line form is fine for a field: `/** Number of connections to this database */`.
  - No blank line between the Javadoc block and the class/method it documents.
- Comments are indented to match the code around them; trailing comments on the same line as code are
  fine (`process('ABC'); // process a dummy String first`).

## References

- Source: https://se-education.org/guides/conventions/java/intermediate.html
- Fallback for anything not covered here: the Google Java Style Guide
  (https://google.github.io/styleguide/javaguide.html).
