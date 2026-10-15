---
name: seedu-java-coding-standard
description: >-
  Apply the SE-EDU Java coding standard (basic + intermediate level) whenever you
  write, refactor, or review Java in this repository — main code and tests alike.
  Covers naming, indentation, line length and wrapping, brace style, whitespace,
  import ordering, statement layout (switch/loops/conditionals), and Javadoc
  formatting. Consult it before adding or editing any `.java` file so new code
  matches the rest of the project.
---

# SE-EDU Java coding standard (basic + intermediate)

Source: <https://se-education.org/guides/conventions/java/basic.html> and
<https://se-education.org/guides/conventions/java/intermediate.html>.
For anything not covered here, fall back to the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Apply this to **every `.java` file** in `src/main/java` and `src/test/java`.

---

## 1. Naming

| Element | Rule | Examples |
| --- | --- | --- |
| Package | all lowercase, project name as root | `dennis`, `dennis.task`, `dennis.parser` |
| Class / enum / record | **noun**, `PascalCase` | `Task`, `CommandType`, `DeadlineParts` |
| Method | **verb**, `camelCase` | `getStatusIcon()`, `computeTotalWidth()` |
| Variable / parameter | `camelCase` | `taskNumber`, `fullCommand` |
| Constant (`static final`) | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS`, `SAVE_SEPARATOR` |
| Collection | **plural** noun | `List<Task> tasks`, `int[] values` |

More rules:

- **English only.** No local slang.
- **No uppercase acronyms inside a name.** `exportHtmlSource()`, not
  `exportHTMLSource()`; `openDvdPlayer()`, not `openDVDPlayer()`.
- **Booleans read like a yes/no question**: prefix with `is`, `has`, `was`,
  `can`, `should`. `isDone`, `hasNext()`, `canEvaluate()`. Setter keeps the
  prefixed name: `void setFound(boolean isFound)`.
- **Related constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- **Scope drives length.** Wide-scope names are descriptive; a short-lived loop
  or scratch variable may be `i`, `j`, `k` (integers) or `c`, `d` (chars). Use
  `j`, `k` only for nested loops.
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `parseDate_missingZeroPadding_throwsDennisException()`. The second and/or third
  part may be dropped when the scope is obvious.

---

## 2. Layout and formatting

- **Indent with 4 spaces. Never tabs.**
- **Line length**: aim under 110 characters; 120 is the hard limit. Wrap longer
  lines.
- **Continuation lines indent 8 spaces** (double the normal indent) from the
  line they continue:
  ```java
  setText("Long line split"
          + "into two parts.");
  ```
- **Break _before_ an operator, _after_ a comma.** Keep the method name attached
  to its opening `(`. Prefer breaking at the highest syntactic level available.
- **K&R ("Egyptian") braces** — opening brace on the same line:
  ```java
  while (!done) {
      doSomething();
  }
  ```
  `else`, `catch`, and `finally` go on the same line as the preceding `}`.
- **Whitespace inside statements**:
  - spaces around binary/ternary operators: `a = (b + c) * d;` not `a=(b+c)*d;`
  - a space after `if`, `for`, `while`, `switch`, `catch`, `return`:
    `while (true) {` not `while(true){`
  - a space after every comma: `doSomething(a, b, c);` not `doSomething(a,b,c);`
- **One blank line** between logical blocks inside a method (often each block
  opens with a short `//` comment). No runs of 2+ blank lines.
- **Array brackets bind to the type**: `int[] a = new int[20];` not
  `int a[] = ...`.

---

## 3. Statements

### Imports

- Every class is in a package.
- **List every import explicitly. No wildcard imports** (`import java.util.*;`
  is banned) — explicit imports document what the file depends on.
- **Group and order imports, one blank line between groups**, alphabetical
  within a group:
  1. `static` imports
  2. standard Java — `java.*`, `javax.*`
  3. other third-party libraries — `org.*` and the rest
  4. this project — `dennis.*`

  ```java
  import static org.junit.jupiter.api.Assertions.assertEquals;

  import java.time.LocalDate;
  import java.util.List;

  import org.junit.jupiter.api.Test;

  import dennis.DennisException;
  import dennis.task.Task;
  ```

### Switch

- Indent `case` one level (4 spaces) inside the `switch`.
- Every `case` ends in `break`/`return`/`throw`. If a `case` deliberately falls
  through **and has statements**, mark it with a `// Fallthrough` comment.
  (Stacked empty labels such as `case A:` immediately above `case B:` need no
  comment.)
- Arrow (`case X -> ...`) and `switch` expressions are fine on Java 14+.

```java
switch (type) {
    case "T":
        task = new Todo(description);
        break;
    case "D":
        task = new Deadline(description, by);
        break;
    default:
        throw new DennisException("unknown type");
}
```

### Loops and conditionals

- **Always brace the body**, even for a single statement:
  ```java
  if (stream != null) {
      readFile(stream);
  }
  ```
  Never `if (stream != null) readFile(stream);`.
- **Keep the body on its own line**, below the condition — not on the same line
  as the `if`/`for`. This keeps line-based debuggers usable.

### Variables

- **Declare at first use and initialise there.** Don't hoist declarations to the
  top of the method or seed them with placeholder values.
- **Declare in the narrowest scope** that works.
- **Never `public`** for a mutable field (constants excepted, and data-only
  carrier classes such as records). Use `private`/`protected` and accessors.

---

## 4. Comments and Javadoc

- All comments in **English, American spelling**.
- Indent a comment to match the code it describes. Trailing comments
  (`foo(); // why`) are allowed.
- **Public classes and methods need a Javadoc header.** May be omitted for:
  - plain getters/setters,
  - overrides where the parent's Javadoc still applies exactly (use
    `{@inheritDoc}` if you need to add to it),
  - test methods.
- Javadoc format:
  - `/**` on its own line; each `*` aligned and followed by a space; `*/` on its
    own line; **no blank line between the Javadoc and the declaration**.
  - **First sentence is a standalone summary** (it is lifted into summary
    tables). Method summaries are descriptive, not imperative: "Returns the…",
    "Adds a…", "Sends the…".
  - Blank line between the description and the `@param`/`@return`/`@throws` block.
  - `@param` tags are **all-or-nothing**: document every parameter or none. End
    each tag's text with punctuation.
  - `@return` may be dropped when the method returns nothing or the return is
    obvious from the summary.
  - One-liner form is fine for fields and simple members: `/** The due date. */`.

```java
/**
 * Returns the lateral location of the specified position.
 * If the position is unset, {@code NaN} is returned.
 *
 * @param x    x coordinate of the position.
 * @param zone zone of the position.
 * @return the lateral location.
 * @throws IllegalArgumentException if {@code zone <= 0}.
 */
public double computeLocation(double x, int zone) throws IllegalArgumentException {
    ...
}
```

---

## 5. Project specifics

- **Java 25.** Modern language features (records, arrow `switch`, pattern
  matching, text blocks) are in scope.
- **Root package is `dennis`.** Everything lives under it (`dennis.command`,
  `dennis.task`, …).
- Import order concretely for this repo: `static` → `java.*` → `org.*` (JUnit) →
  `dennis.*`.
- Tests mirror the class under test: `dennis.foo.Bar` →
  `src/test/java/dennis/foo/BarTest.java`, with
  `featureUnderTest_testScenario_expectedBehavior()` method names.
- **Checkstyle enforces this standard.** The rules live in
  `config/checkstyle/` (the SE-EDU config). Run
  `./gradlew checkstyleMain checkstyleTest` (or `./gradlew check`) and make it
  pass before finishing. A few things Checkstyle is stricter about than prose
  review: `DeclarationOrder` (static fields first, then instance fields, then
  constructors, then methods — access order `public → protected → package →
  private` within each), `OverloadMethodsDeclarationOrder` (overloads grouped
  together), a newline at end of file, and wrapping a line *before* an operator
  and *after* `(` / `,` (so a wrapped lambda argument reads
  `foo(Bar.class, () ->` then the body on the next line).

---

## 6. Pre-commit checklist

- [ ] 4-space indent, no tabs; no lines over 120 chars.
- [ ] Imports: explicit (no `*`), grouped `static / java / org / dennis`, blank
      line between groups, alphabetical within.
- [ ] Every `if`/`for`/`while` body is braced and on its own line.
- [ ] `switch` cases indented one level; each case terminates or is marked
      `// Fallthrough`.
- [ ] Spaces around operators, after commas, after keywords.
- [ ] Names: classes are `PascalCase` nouns, methods are `camelCase` verbs,
      constants are `SCREAMING_SNAKE_CASE`, no `UPPERCASE` acronyms mid-name,
      booleans are `is/has/was`-style.
- [ ] Variables declared at first use, in the narrowest scope; no `public`
      mutable fields.
- [ ] Public classes/methods have a Javadoc header whose first sentence is a
      descriptive summary; `@param` is all-or-nothing.
