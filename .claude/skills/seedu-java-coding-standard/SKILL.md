---
name: seedu-java-coding-standard
description: The mandatory Java coding standard for this repository (se-education "intermediate" level). Consult BEFORE writing or editing any .java file in src/main or src/test — covers naming, layout, line length, imports, braces, statement form, and Javadoc rules, plus a self-review checklist.
---

# seedu Java coding standard (this project)

Condensed from the se-education guides:
- Basic: https://se-education.org/guides/conventions/java/basic.html
- Intermediate: https://se-education.org/guides/conventions/java/intermediate.html

Anything not covered here defers to the Google Java Style Guide.

Apply this to **all** `.java` files in the repo — production (`src/main/java`)
**and** tests (`src/test/java`).

---

## 1. Naming

| Thing | Rule | Example |
|---|---|---|
| Package | all lower case, no underscores | `pan`, `todobuddy.ui` |
| Class / enum | noun, PascalCase | `Parser`, `TaskList` |
| Method | verb, camelCase | `parseDeadline()`, `computeTotalWidth()` |
| Variable | camelCase | `taskList`, `audioSystem` |
| Constant (`static final`) | SCREAMING_SNAKE_CASE | `MAX_ITERATIONS`, `INPUT_FORMAT` |
| Boolean var / method | reads like a yes/no: `is` / `has` / `was` / `can` | `isDone`, `hasLicence()`, `void setFound(boolean isFound)` |
| Collection | plural | `Collection<Task> tasks`, `int[] values` |
| Loop counters | `i`, `j`, `k` — `j`/`k` only for nested loops | |

- All names in **English**.
- Acronyms are **not** all-caps inside a name: `exportHtmlSource()`, not
  `exportHTMLSource()`; `openDvdPlayer()`, not `openDVDPlayer()`.
- Related constants share a prefix: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- Wide scope → longer, descriptive names; tiny scope → short names allowed.
- Test methods: `featureUnderTest_testScenario_expectedBehavior()`
  (e.g. `parseDeadline_missingByKeyword_exceptionThrown()`). Parts may be dropped
  when obvious.

## 2. Layout

- **4 spaces** per indent level. **Never tabs.**
- **Line length ≤ 120 characters** (hard limit). Aim to stay **under 110** (soft
  limit) — wrap earlier when it reads better.
- **Wrapped lines** are indented **8 spaces** (2 levels) past the parent line.
- **K&R / "Egyptian" braces** — opening brace on the same line:
  ```java
  while (!done) {
      doSomething();
  }
  ```
- Where to break a long line:
  - break **after** a comma;
  - break **before** an operator (including `.`, and `|` in a multi-catch);
  - keep a method/constructor name attached to its opening `(`;
  - prefer higher-level breaks to lower-level ones.
- Method definition form: `public void someMethod() throws SomeException {`
- Separate **logical units within a block** with **one** blank line (one, not two).

## 3. Whitespace

- Operators surrounded by spaces: `a = (b + c) * d;`
- A reserved word is followed by a space: `while (true) {`, `if (x) {`
- A comma is followed by a space: `doSomething(a, b, c);`
- `for` semicolons are followed by a space: `for (i = 0; i < 10; i++) {`
- `:` is surrounded by spaces when a binary/ternary operator (e.g. in a
  for-each or ternary).

## 4. Statements

- **Every class is in a package.** No class in the default package.
- **Explicit imports only** — never `import java.util.*;`.
- **Import order** (blank line between groups):
  1. `static` imports
  2. `java.*`
  3. `javax.*`
  4. `org.*`
  5. `com.*`
  6. project imports (`pan.*`)
- Array type: `int[] a = new int[20];` — **not** `int a[]`.
- Declare each variable in the **smallest scope** possible and **initialise it
  where it is declared**.
- Instance/class variables are never `public` unless the class is a pure data
  holder with no behaviour. `public static final` constants are the exception.
- **Every loop body and every conditional body is wrapped in braces**, even a
  single statement:
  ```java
  if (isDone) {
      doCleanup();
  }
  ```
  Not `if (isDone) doCleanup();` and not on one line.
- The condition goes on its **own line** (not sharing a line with the body).
- `switch`: add a `// Fallthrough` comment on any `case` that intentionally has
  no `break`. Arrow form (`case X -> ...;`) and `switch` expressions are allowed.

## 5. Comments & Javadoc

- All comments in **English**, American spelling, no local slang.
- **Header (Javadoc) comment on every public class and public method.**
  May be **omitted** for:
  - simple getters / setters,
  - methods that override a documented supertype method (the parent Javadoc
    applies — use `{@inheritDoc}` if you only tweak wording),
  - test classes and test methods.
- Javadoc form:
  - `/**` on its own line; each `*` aligned, with a space after it;
  - first sentence is a short summary in the **third person**: "Returns…",
    "Adds…", "Sends…" — not "Return" / "To return";
  - **one blank line** between the description and the `@param` / `@return` /
    `@throws` block;
  - **no blank line** between the Javadoc and the thing it documents;
  - end each `@param` / `@throws` description with punctuation;
  - `@return` may be omitted when there is no return value or it is already
    obvious from the summary;
  - `@param` is **all-or-nothing**: document every parameter or none.
- One-line member Javadoc is fine: `/** Number of open connections. */`
- Trailing comments are allowed: `process("ABC"); // warm up the cache`

## 6. This repository

- Package root is **`pan`**. New production classes go in `pan` (or a
  sub-package like `pan.ui` if the package grows).
- Tests mirror the class under test: `pan.Parser` →
  `src/test/java/pan/ParserTest.java`.
- Build/verify with `./gradlew build` (compiles + runs the JUnit suite).

## 7. Self-review checklist (run before every Java commit)

- [ ] 4-space indent, no tabs; no line over 120 chars (`grep -rnE '.{121,}' src/`)
- [ ] K&R braces; every `if`/`for`/`while` body braced, condition on its own line
- [ ] Class in a package; imports explicit and in the group order above
- [ ] Names: PascalCase classes, camelCase methods/vars, SCREAMING_SNAKE_CASE
      constants, `is/has/was` booleans, plural collections, no ALLCAPS acronyms
- [ ] `int[] x`, not `int x[]`; variables declared in smallest scope, initialised
      at declaration
- [ ] Public classes/methods have a Javadoc header (except getters/setters,
      overrides, tests); summary in third person; `@param` all-or-nothing; no
      blank line between Javadoc and member
- [ ] Test methods named `featureUnderTest_scenario_expectedBehavior()`
- [ ] One blank line (not two) between logical units
