# Java coding standard

Source: <https://se-education.org/guides/conventions/java/index.html>
(basic + intermediate: <https://se-education.org/guides/conventions/java/intermediate.html>)

This project follows the **basic + intermediate** levels. Sections 1-4 below are
binding. Section 5 records the advanced rules for reference; they are not
enforced here.

Anything this standard does not cover falls back to the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## 1. Naming

| Element | Rule | Example |
| --- | --- | --- |
| Package | All lower case, dot separated | `nori.task` |
| Class / enum | Noun, `PascalCase` | `TaskList`, `CommandType` |
| Method | Verb, `camelCase` | `getName()`, `computeTotalWidth()` |
| Variable | `camelCase` | `taskIndex`, `audioSystem` |
| Constant | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS`, `COLOR_RED` |
| Test method | `featureUnderTest_testScenario_expectedBehavior()` | `sortList_emptyList_exceptionThrown()` |

- **Packages.** For a school project the root name is the project name followed
  by logical group names, e.g. `nori.ui`, `nori.storage`. Do not use
  `edu.nus.comp.*`; the code is not officially produced by NUS.
- **Test method names.** The third part, or both the second and third parts, may
  be omitted: `sortList_emptyList()` tests all empty-list variations, and
  `sortList()` tests all scenarios.
- **Abbreviations and acronyms are not uppercased inside a name.**

  | Good | Bad |
  | --- | --- |
  | `exportHtmlSource();` | `exportHTMLSource();` |
  | `openDvdPlayer();` | `openDVDPlayer();` |

- **All names are in English**, using American spelling.
- **Name length tracks scope.** A wide scope gets a long, descriptive name; a
  scratch variable can be short. Common scratch names are `i, j, k, m, n` for
  integers and `c, d` for characters.
- **Iterator variables** may be `i`; `j`, `k` are for nested loops only.
- **Booleans sound like booleans.** Prefix with `is`, `has`, `was`, `can`,
  `should`:

  ```java
  // variables
  isSet, isVisible, isFinished, isFound, isOpen, hasData, wasOpen

  // methods
  boolean hasLicense();
  boolean canEvaluate();
  boolean shouldAbort = false;
  ```

  A bare verb such as `matches` is not acceptable for a boolean variable; use
  `isMatch`. Setters take the same form: `void setFound(boolean isFound);`
  Rationale: this follows the Java core packages and makes code read like
  English, e.g. `if (isOpen) ...`.
- **Collections take the plural form.**

  ```java
  Collection<Point> points;
  int[] values;
  ```

- **Associated constants share a common prefix**, so they sort together:

  ```java
  static final int COLOR_RED   = 1;
  static final int COLOR_GREEN = 2;
  static final int COLOR_BLUE  = 3;
  ```

---

## 2. Layout

- **Indent with 4 spaces.** Never tabs.
- **Line length: soft limit 110 characters, hard limit 120.** Stay under 110.
- **Wrapped lines indent 8 spaces** (twice the normal indent) from the parent
  line:

  ```java
  setText("Long line split"
          + "into two parts.");
  if (isReady) {
      setText("Long line split"
              + "into two parts.");
  }
  ```

### Where to break a line

The objective is readability; do not blindly accept the auto-format suggestion.

- **Break after a comma.**
- **Break before an operator.** This includes the dot separator `.`, the
  ampersand in type bounds `<T extends Foo & Bar>`, and the pipe in a multi-catch
  `catch (FooException | BarException e)`.

  ```java
  totalSum = a + b + c
          + d + e;
  method(param1,
          object.method()
                  .method2(),
          param3);
  ```

- **A method or constructor name stays attached to its opening parenthesis.**

  | Good | Bad |
  | --- | --- |
  | `someVeryLongName(`<br>`        int anArg, Object anotherArg);` | `someVeryLongName`<br>`        (int anArg, Object anotherArg);` |

- **Prefer a higher-level break to a lower-level one.**

  | Good | Bad |
  | --- | --- |
  | `longName1 = longName2 * (longName3 + longName4 - longName5)`<br>`        + 4 * longname6;` | `longName1 = longName2 * (longName3 + longName4`<br>`        - longName5) + 4 * longname6;` |

- **Ternaries** take one of two forms:

  ```java
  alpha = (aLongBooleanExpression) ? beta : gamma;

  alpha = (aLongBooleanExpression)
          ? beta
          : gamma;
  ```

### Brackets and statement forms

Use **K&R (Egyptian) style**: the opening brace ends the statement line, the
closing brace starts its own line.

| Good | Bad |
| --- | --- |
| `while (!done) {`<br>`    doSomething();`<br>`}` | `while (!done)`<br>`{`<br>`    doSomething();`<br>`}` |

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

Switch statements take one of these forms:

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

The explicit `// Fallthrough` comment is required whenever a `case` has no
`break`. Rationale: leaving out the `break` is a common error, so an intentional
omission must be made obvious. The arrow form needs no such comment.

### White space within a statement

| Rule | Good | Bad |
| --- | --- | --- |
| Operators are surrounded by a space. | `a = (b + c) * d;` | `a=(b+c)*d;` |
| A reserved word is followed by a space. | `while (true) {` | `while(true){` |
| A comma is followed by a space. | `doSomething(a, b, c, d);` | `doSomething(a,b,c,d);` |
| A colon used as a binary or ternary operator is surrounded by space; a semicolon in a `for` header is followed by one. Does not apply to `switch x:`. | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

**Logical units within a block are separated by one blank line.** Each block is
often introduced by a comment.

---

## 3. Statements

### Packages and imports

- **Put every class in a package.**
- **The ordering of import statements must be consistent**, with a blank line
  between groups:

  ```java
  import static org.junit.Assert.assertEquals;

  import java.io.File;
  import java.io.IOException;

  import javax.xml.bind.JAXBContext;

  import org.testfx.api.FxToolkit;

  import javafx.geometry.Bounds;
  import nori.task.TaskList;
  ```

- **List imported classes explicitly.** Never `import java.util.*;`
  Rationale: an explicit list documents the dependencies of the class and keeps
  them reviewable.

#### The groups this project actually enforces

The guide says the ordering must be *consistent* without fixing the groups, so
`config/checkstyle/checkstyle.xml` settles it. `CustomImportOrder` defines four
groups, in this order:

| Group | Matches |
| --- | --- |
| `STATIC` | every `import static` |
| `STANDARD_JAVA_PACKAGE` | `java.*` and `javax.*` |
| `SPECIAL_IMPORTS` | `^org\.` |
| `THIRD_PARTY_PACKAGE` | everything else, including `javafx.*` **and** `nori.*` |

Imports are sorted alphabetically within a group, groups are separated by a
blank line, and a blank line *inside* a group is a violation
(`Extra separation in import group`).

The consequence catches people out: **`javafx.*` and `nori.*` are one group**,
so they run together with no blank line between them.

```java
import java.io.IOException;

import javafx.stage.Stage;
import nori.ui.GuiUi;      // no blank line above this: same group
```

Splitting them reads better and has been suggested in review, but Checkstyle
rejects it, and the standard's own worked example likewise runs `javafx.*` and
`junit.*` together in the final group. Leave them contiguous.

### Types

**Array specifiers attach to the type, not the variable.**

| Good | Bad |
| --- | --- |
| `int[] a = new int[20];` | `int a[] = new int[20];` |

Rationale: the arrayness is a feature of the base type.

### Variables

- **Initialise variables where they are declared, in the smallest possible
  scope.** When it is impossible to initialise to a valid value at the
  declaration, leave the variable uninitialised rather than assigning a phony
  value.
- **Class variables are never `public`**, unless the class is a data class with
  no behaviour. Constants are exempt. Rationale: public variables violate
  information hiding; use non-public fields and accessors.

### Loops and conditionals

- **Always brace the body**, however few statements it holds:

  | Good | Bad |
  | --- | --- |
  | `if (isDone) {`<br>`    doCleanup();`<br>`}` | `if (isDone) doCleanup();` |

  Rationale: omitting braces is error prone, and a single-line conditional makes
  it impossible to see in a debugger whether the branch was taken.
- **Put the condition on a separate line from the action.**

---

## 4. Comments

- **All comments are written in English**, American spelling, no local slang.
- **Write descriptive header comments for all public classes and methods.**
  They may be omitted for:
  - getters and setters,
  - overridden methods, when the parent Javadoc applies exactly as is,
  - classes and methods used for testing.

  Rationale: public methods are used by others, and users should not have to
  read the body to learn the exact behaviour. Code can only say HOW it works,
  not WHAT it is supposed to do.

- **Javadoc takes this form:**

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
  ```

  In particular:
  - The opening `/**` sits on its own line; each following `*` aligns under it,
    with a space after it.
  - **The first sentence is a short summary of the method** — Javadoc places it
    in the summary table and index. It starts in the form "Returns ...",
    "Sends ...", "Adds ..." — not "Return" or "Returning".
  - One empty line between the description and the parameter section.
  - **Punctuation behind each parameter description.**
  - No blank line between the documentation block and the declaration.
  - `@return` may be omitted when the method returns nothing, or when the return
    value is obvious from the rest of the comment.
  - `@param` may be omitted when every parameter is self-explanatory or already
    explained in the main description. A comment has `@param` for **all** its
    parameters, or for none.
  - Use `{@inheritDoc}` to reuse a parent header comment on an override.

- A class member Javadoc may be one line:

  ```java
  /** Number of connections to this database */
  private int connectionCount;
  ```

- **Comments are indented to match the code they describe**, so they do not
  break the logical structure of the program. A trailing comment on the same
  line as code is allowed:

  ```java
  process("ABC"); // process a dummy String first
  ```

---

## 5. Advanced rules (recorded, not enforced here)

These come from the "all rules" page and are listed so the difference is
explicit. This project does not require them.

- **Put related classes in a single package**, the way `java.io` groups
  file-writing classes.
- **Order class and interface declarations** as: class/interface documentation,
  the `class`/`interface` statement, static variables (`public`, `protected`,
  package, `private`), instance variables in the same order, constructors, then
  methods in no specific order. Rationale: a predictable location for each
  element makes code easy to navigate.
- **Order method modifiers** as `<access> static abstract synchronized
  <unusual> final native`, where `<access>` is `public | protected | private` and
  `<unusual>` is `volatile | transient`. The access modifier must come first.
- **Avoid unnecessary use of `this` with fields.** Use `this` only when a field
  is shadowed by a parameter.
- **All non-trivial private methods should carry header comments.** Rationale:
  if the method is hard to describe succinctly, its abstraction is wrong.

---

## 6. User-facing strings

The standard governs code, not copy, but user-facing text is reviewed on the
same terms and the same review has flagged it before. Two rules apply here:

- **Punctuate every message.** A message that ends a sentence ends with a full
  stop; a question ends with a question mark. Do not mix `...` and `…` in one
  code base.
- **Leave no ambiguity.** A message names the command it is about, states what
  went wrong, and shows a correct example. Persona and flavour are welcome, but
  never at the cost of the reader knowing what to type next, and never in an
  accessibility label, where the plain name of the control is the only useful
  text.

---

## 7. Checking the work

- `./gradlew checkstyleMain checkstyleTest` must pass. See
  [checkstyle.md](checkstyle.md).
- `./gradlew build` must pass.
- `javadoc -private -sourcepath src/main/java -subpackages nori` should report
  zero warnings, which catches every missing header comment.
- Check line length before committing:

  ```bash
  awk 'length > 110 {print FILENAME":"FNR": "length}' $(find src test -name "*.java")
  ```
