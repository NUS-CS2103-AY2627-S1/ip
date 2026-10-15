---
name: seedu-java-coding-standard
description: >-
  Provides the full SE-EDU Java coding standard (basic + intermediate rules) for this project.
  Activate this skill whenever writing, reviewing, or refactoring Java code to ensure every rule
  is applied correctly before producing or evaluating any Java source.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html
For topics not covered here, fall back to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## Naming

### Packages
- All lower case: `com.company.application.ui`
- For school projects use project/group name followed by logical group names: `todobuddy.ui`, `todobuddy.file`
- Do **not** use `edu.nus.comp.*` or similar NUS-specific prefixes.

### Classes and Enums
- Must be **nouns** written in `PascalCase`: `Line`, `AudioSystem`, `TaskList`

### Variables
- `camelCase`: `line`, `audioSystem`
- Variables with a **large scope** must have long, descriptive names.
- Variables with a **small scope** can be short. Scratch variables for integers: `i`, `j`, `k`, `m`, `n`; for characters: `c`, `d`.

### Constants
- `SCREAMING_SNAKE_CASE`: `MAX_ITERATIONS`, `COLOR_RED`
- See [Google Style Guide §5.2.4](https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names) for what counts as a constant.

### Methods
- Must be **verbs** in `camelCase`: `getName()`, `computeTotalWidth()`

### Test Methods
- May use underscores in format: `featureUnderTest_testScenario_expectedBehavior()`
- e.g. `sortList_emptyList_exceptionThrown()`, `getMember_memberNotFound_nullReturned()`
- Second and/or third parts can be omitted depending on coverage scope.

### Abbreviations and Acronyms
- Do **not** uppercase when part of a name:
  - Good: `exportHtmlSource()`, `openDvdPlayer()`
  - Bad: `exportHTMLSource()`, `openDVDPlayer()`

### Booleans
- Must sound like booleans. Prefer `is`, `has`, `was`, `can` prefixes:
  - Variables: `isSet`, `isVisible`, `isFinished`, `isFound`, `isOpen`, `hasData`, `wasOpen`
  - Methods: `boolean hasLicense()`, `boolean canEvaluate()`
- Boolean setter form: `void setFound(boolean isFound)`

### Collections
- Use **plural** names: `Collection<Point> points`, `int[] values`

### Iterator Variables
- Use `i`, `j`, `k` for loop iterators. `j`, `k` etc. for nested loops only.

### Associated Constants
- Use a common prefix to group them: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`

### Language
- All names must be in **English**.

---

## Layout

### Indentation
- 4 spaces (never tabs).

### Line Length
- Soft limit: 110 characters.
- Hard limit: 120 characters.
- If a line exceeds the limit, wrap it. Wrapped continuation lines are indented **8 spaces** (double normal) from their parent line.

### Line Wrapping
The primary goal is **readability**, not blindly following IDE auto-format.

- Break **after** a comma.
- Break **before** an operator. This applies to: `+`, `-`, `*`, `/`, `.` (dot separator), `&` (in type bounds `<T extends Foo & Bar>`), `|` (in catch `catch (FooException | BarException e)`).
- A method or constructor name stays **attached** to its opening `(`:
  ```java
  // Good
  someVeryVeryLongMethodName(
          int anArg, Object anotherArg);

  // Bad — space before (
  someVeryVeryLongMethodName (int anArg, Object anotherArg);
  ```
- Prefer **higher-level** breaks (outside parentheses) over lower-level (inside):
  ```java
  // Good
  longName1 = longName2 * (longName3 + longName4 - longName5)
          + 4 * longname6;

  // Bad
  longName1 = longName2 * (longName3 + longName4
          - longName5) + 4 * longname6;
  ```
- Two acceptable forms for ternary:
  ```java
  alpha = (aLongBooleanExpression) ? beta : gamma;

  alpha = (aLongBooleanExpression)
          ? beta
          : gamma;
  ```

### Brace Style
Use **K&R / Egyptian** style — opening brace on the **same line**, never on a new line:
```java
// Good
while (!done) {
    doSomething();
    done = moreToDo();
}

// Bad
while (!done)
{
    doSomething();
}
```

### Statement Block Forms
Method definitions:
```java
public void someMethod() throws SomeException {
    // ...
}
```

`if-else`:
```java
if (condition) {
    statements;
}

if (condition) {
    statements;
} else {
    statements;
}

if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
```

`for`:
```java
for (initialization; condition; update) {
    statements;
}
```

`while` and `do-while`:
```java
while (condition) {
    statements;
}

do {
    statements;
} while (condition);
```

### Whitespace within Statements
```java
a = (b + c) * d;          // spaces around operators
while (true) {             // space between keyword and (
doSomething(a, b, c, d);  // space after each comma
for (i = 0; i < 10; i++) {
```

### Blank Lines
Separate logical units within a method body with **one blank line**, typically preceded by a comment:
```java
// Create a new identity matrix
Matrix4x4 matrix = new Matrix4x4();

// Precompute angles for efficiency
double cosAngle = Math.cos(angle);
double sinAngle = Math.sin(angle);
```

---

## Statements

### Package and Import Statements
- Every class must be in a **package**.
- Import order must be **consistent** — use IDE auto-ordering (IntelliJ default is fine).
- **Never use wildcard imports** (`import java.util.*;`). List each class explicitly:
  ```java
  // Good
  import java.util.List;
  import java.util.ArrayList;
  import java.util.HashSet;

  // Bad
  import java.util.*;
  ```

### Types
- Array specifiers belong to the **type**, not the variable:
  ```java
  int[] a = new int[20];  // Good
  int a[] = new int[20];  // Bad
  ```

### Variables
- Declare in the **smallest possible scope**.
- **Initialize** where declared. Leave uninitialized rather than setting a phony initial value when a proper initial value is unavailable.
- **Never** declare a non-constant instance/class variable `public` (constants are exempt). Use private fields with accessor methods instead.

### Loops
Loop body **must always** be wrapped in `{ }`, even for a single statement:
```java
// Good
for (int i = 0; i < 100; i++) {
    sum += value[i];
}

// Bad
for (int i = 0; i < 100; i++)
    sum += value[i];
```

### Conditionals
- The conditional body must go on a **new line** — never inline with the condition.
- Body **must always** be wrapped in `{ }`, even for a single statement:
```java
// Good
if (isDone) {
    doCleanup();
}

// Bad — body on same line as condition
if (isDone) doCleanup();

// Bad — body on new line but without braces
if (isDone)
    doCleanup();
```

### Switch Statements
Traditional form:
```java
switch (condition) {
case ABC:
    statements;
    // Fallthrough
case DEF:
    statements;
    break;
case XYZ:
    statements;
    break;
default:
    statements;
    break;
}
```

Modern arrow-switch form (also acceptable):
```java
switch (condition) {
case ABC -> method("1");
case DEF -> method("2");
case XYZ -> method("3");
default  -> method("0");
}
```

Switch expression form:
```java
int size = switch (condition) {
    case ABC -> 1;
    case DEF -> 2;
    case XYZ -> 3;
    default  -> 0;
};
```

**Always include `// Fallthrough`** when a `case` block intentionally falls through (i.e., has no `break`).

### Try-Catch
```java
try {
    statements;
} catch (Exception exception) {
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

---

## Comments

- All comments must be in **English**. Use American spelling. Avoid local slang.
- Comments must be **indented** at the same level as the surrounding code.
- Trailing end-of-line comments are allowed: `process("ABC"); // process a dummy String`

### When Javadoc Is Required
Write Javadoc on **every public class and every public method**, except:
1. Getters and setters.
2. `@Override` methods whose parent Javadoc applies *exactly* as-is to the override.
3. Methods in test classes.

### Javadoc Format
```java
/**
 * Returns the lateral location of the specified position.
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

Rules:
- Opening `/**` on its own line.
- First sentence is a short summary; start with a **third-person present-tense verb**: `Returns`, `Sends`, `Adds`, `Removes` — not `Return` or `Returning`.
- Subsequent `*` aligned with the first `*`. One space after each `*`.
- **Blank line** between the description and the `@param`/`@return` block.
- Each `@param` description ends with a period.
- **No blank line** between the Javadoc block and the method/class declaration.
- `@return` may be omitted when the method is `void` or the return value is obvious from the rest of the comment.
- `@param` may be omitted for *all* parameters when every parameter name is self-explanatory or all are already described in the main comment body. Either document **all** parameters with `@param`, or **none**.
- For overriding methods with slightly different behavior, use `{@inheritDoc}` plus your additions.

Single-line form is acceptable for class member comments:
```java
/** Number of connections to this database */
private int connectionCount;
```
