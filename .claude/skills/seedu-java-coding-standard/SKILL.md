---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (basic + intermediate rules) that all Java code in this project must follow — naming, layout, statements, packages/imports, and comments/Javadoc. Use whenever writing, reviewing, refactoring or reformatting Java code in this repository, or when asked whether some code follows the coding standard.
---

# SE-EDU Java coding standard (basic + intermediate)

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

This is the coding standard for **all** Java code in this repository. Use the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
for anything not covered here.

## How to use this skill

- **When writing new Java code:** follow the rules below as you write. Do not
  write code first and tidy it afterwards.
- **When changing existing Java code:** the code you touch must come out
  compliant. Fixing an unrelated violation elsewhere in the same file is fine
  and welcome; rewriting whole files that the task did not ask about is not.
- **When asked to check compliance:** work through the checklist at the end of
  this file, and report each violation with its file, line and the rule it
  breaks — do not silently fix things without saying what changed and why.
- **After any change under `src/`:** the project's usual testing rule still
  applies — update `test/ui-test-plan.md` if visible behavior changed, then run
  the `test-ui` skill.

## Naming

| Kind | Rule | Example |
| --- | --- | --- |
| Package | all lower case, rooted at the project name | `bob`, `bob.task`, `bob.ui` |
| Class / enum | noun, `PascalCase` | `Line`, `AudioSystem` |
| Variable | `camelCase` | `line`, `audioSystem` |
| Constant | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS`, `COLOR_RED` |
| Enum constant | `SCREAMING_SNAKE_CASE` | `TODO`, `DEADLINE` |
| Method | verb, `camelCase` | `getName()`, `computeTotalWidth()` |

- **What counts as a constant** is decided by the [Google Java Style Guide][g]:
  a `static final` field whose contents are **deeply immutable**. A
  `static final ArrayList` or `static final String[]` is *not* a constant — its
  contents can still change — so it takes a `camelCase` name, or is made truly
  immutable (`List.of(...)`) and keeps the constant name.
- **Abbreviations and acronyms are not uppercased** inside a name:
  `exportHtmlSource()`, not `exportHTMLSource()`. The Google guide's rule for
  the awkward cases is to lowercase the acronym entirely and then uppercase
  only its first letter: `XmlHttpRequest`, `newCustomerId`, `supportsIpv6OnIos`
  — never `XMLHTTPRequest`, `newCustomerID` or `supportsIPv6OnIOS`.
- **All names in English.**
- **Scope sets length:** wide scope → long descriptive name; a scratch variable
  living a few lines may be short. Loop iterators may be `i`, `j`, `k`; `j` and
  `k` only for nested loops.
- **Booleans must read like booleans**, and as far as possible carry an
  `is` / `has` / `was` / `can` / `should` prefix, so a linter can check the rule:
  `isSet`, `hasData`, `wasOpen`, `canEvaluate()`, `shouldAbort`.
  Boolean setters take the form `void setFound(boolean isFound)`.
- **Collections take plural names:** `Collection<Point> points`, `int[] values`.
- **Associated constants share a prefix:** `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- **Test methods** may use underscores, in the form
  `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `sortList_emptyList_exceptionThrown()`. The last part, or the last two, may be
  dropped when the test covers more than one scenario.

[g]: https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names

## Layout

- **Indent 4 spaces. Never tabs.**
- **Line length:** soft limit 110 characters, hard limit 120. Wrap longer lines.
- **Wrapped lines are indented 8 spaces** (twice the normal indent) past the
  parent line.
- **Break lines to aid reading**, not wherever the IDE suggests:
  - break *after* a comma;
  - break *before* an operator, including the `.` separator, the `&` in type
    bounds, and the `|` in a multi-catch;
  - keep a method or constructor name attached to its opening `(`;
  - prefer a higher-level break (outside the parentheses) to a lower-level one.
- **A ternary** is written either on one line, or split before both the `?` and
  the `:` — these are the two accepted forms:

  ```java
  alpha = (aLongBooleanExpression) ? beta : gamma;

  alpha = (aLongBooleanExpression)
          ? beta
          : gamma;
  ```

- **K&R (Egyptian) brackets** — the opening brace ends the line that opens the
  block; `} else {` and `} catch (...) {` sit on one line.
- **Statement forms** to follow exactly:

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

  try {
      statements;
  } catch (Exception exception) {
      statements;
  } finally {
      statements;
  }
  ```

- **`switch`** may use either the colon form or the arrow form:

  ```java
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

  switch (condition) {
      case ABC -> method("1");
      default -> method("0");
  }

  int size = switch (condition) {
      case ABC -> 1;
      default -> 0;
  };
  ```

  A `case` in the colon form that has no `break` **must** carry an explicit
  `// Fallthrough` comment, so the omission is visibly deliberate.
- **White space within a statement:**
  - operators surrounded by spaces — `a = (b + c) * d;`
  - a reserved word followed by a space — `while (true) {`
  - a comma followed by a space — `doSomething(a, b, c);`
  - a colon surrounded by spaces when used as a binary/ternary operator (not
    after a `switch` label);
  - the semicolons of a `for` header followed by a space.
- **Separate logical units within a block with one blank line**, each usually
  introduced by a comment.

## Statements

### Packages and imports

- **Every class is in a package.** For a school project the root package name is
  the project name, followed by logical groupings — `bob`, `bob.task`, `bob.ui`.
  Never `edu.nus.comp.*` or similar: the code is not produced by the university.
- **Import ordering must be consistent** across the project (static imports,
  then `java`/`javax`, then third-party, then the project's own).
- **List imported classes explicitly.** `import java.util.List;`, never
  `import java.util.*;`.

### Types

- **Array brackets attach to the type:** `int[] a = new int[20];`, not `int a[]`.

### Variables

- **Initialize a variable where it is declared, in the smallest possible
  scope.** If no valid value is available at that point, leave it uninitialized
  rather than assigning a phony one.
- **No `public` class variables**, unless the class is a pure data class with no
  behavior. Constants are exempt. Use non-public fields with accessors.

### Loops and conditionals

- **Always brace the body**, however short it is — loop bodies and conditional
  bodies alike.
- **The conditional goes on its own line:** `if (isDone) { doCleanup(); }` split
  across lines, never `if (isDone) doCleanup();`.

## Comments

- **All comments in English, American spelling**, no local slang.
  (`recognize`, not `recognise`; `behavior`, not `behaviour`.)
- **Header comments are required for every class and every public method.**
  They may be omitted only for:
  1. getters and setters,
  2. overriding methods, when the parent's Javadoc applies exactly as-is,
  3. classes and methods used for testing.
- **Javadoc form:**

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

  - opening `/**` on its own line, subsequent `*` aligned under it, a space
    after each `*`;
  - the first sentence is a short summary — Javadoc puts it in the summary
    table — and for a method it starts `Returns ...`, `Sends ...`, `Adds ...`
    (not `Return`, not `Returning`);
  - a blank line between the description and the tag section;
  - punctuation after each parameter description;
  - no blank line between the comment and the thing it documents;
  - `@return` may be omitted when the method returns nothing or the return value
    is already obvious from the description;
  - `@param` is **all or nothing**: document every parameter, or none. Omit them
    all when the names are self-explanatory or already covered in the prose;
  - `{@inheritDoc}` reuses a parent's comment when an override needs only a
    small addition.
- **Single-line Javadoc for a field** is fine:
  `/** Number of connections to this database */`
- **Indent a comment with the code it describes.** Trailing comments
  (`process("ABC"); // process a dummy String first`) are allowed.

## Review checklist

Run through this before reporting a Java change as done.

- [ ] Every file starts with a `package` declaration.
- [ ] No wildcard imports; import order consistent.
- [ ] Classes/enums are `PascalCase` nouns; methods are `camelCase` verbs;
      variables are `camelCase`; genuinely-immutable constants are
      `SCREAMING_SNAKE_CASE` — and mutable `static final` fields are not.
- [ ] Booleans read like booleans and carry an `is`/`has`/`was`/`can`/`should`
      prefix where reasonable; collections have plural names.
- [ ] 4-space indent, no tabs, lines within 110 characters (120 at the
      absolute most), wrapped lines indented by 8.
- [ ] K&R braces; every `if`/`for`/`while` body braced, even one-liners.
- [ ] `switch` labels indented one level inside the `switch`; every colon-form
      `case` without a `break` carries a `// Fallthrough` comment.
- [ ] Spaces around operators, after commas, after reserved words and after the
      semicolons in a `for` header.
- [ ] No `public` non-constant fields.
- [ ] Variables declared in the smallest scope and initialized there.
- [ ] Every class and every public method has a header comment, except getters,
      setters, overrides and test code.
- [ ] Javadoc first sentences start `Returns`/`Adds`/`Prints`...; `@param` is
      all-or-nothing; tags are separated from the description by a blank line.
- [ ] Every `@param`, `@return` and `@throws` description ends in punctuation —
      on its last line where the description spans several.
- [ ] Comments are in English with American spelling.
