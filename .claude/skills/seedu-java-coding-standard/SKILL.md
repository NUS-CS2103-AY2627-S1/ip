---
name: seedu-java-coding-standard
description: The SE-EDU (se-education.org) Java coding standard at Intermediate level, which is MANDATORY for all Java code in this project. Use whenever writing, editing, reviewing, or generating any Java code here - new classes, new methods, bug fixes, refactors, tests - and whenever the user asks to check, audit, or fix coding style, naming, layout, Javadoc, or "the coding standard".
---

# SE-EDU Java Coding Standard (Intermediate)

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

**This standard is mandatory for every `.java` file in this project.** Apply it to
code you write, and fix violations in code you touch. Anything not covered here
falls back to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Quick checklist

Run through this before finishing any Java edit:

- [ ] Names: `PascalCase` classes, `camelCase` variables/methods, `UPPER_SNAKE_CASE` constants, lowercase packages
- [ ] No `snake_case` anywhere except constants
- [ ] Methods are verbs; boolean names read like booleans (`is`/`has`/`was`/`can`/`should`)
- [ ] Collections have plural names
- [ ] 4-space indent, no tabs; 8-space indent for wrapped lines
- [ ] Lines <= 120 chars (aim for 110)
- [ ] K&R braces; `{` on the same line, preceded by a space
- [ ] Spaces around operators, after commas, after keywords
- [ ] Every `if`/`for`/`while` body wrapped in braces, even one-liners
- [ ] Variables declared in the smallest scope and initialised at declaration
- [ ] Explicit imports (no `java.util.*`), consistently ordered, none unused
- [ ] Javadoc header comments on all public classes and public methods
- [ ] Logical units separated by one blank line

---

## 1. Naming

| Element | Rule | Good | Bad |
|---|---|---|---|
| Package | all lower case | `quu.task` | `quu.Task`, `quu.task_list` |
| Class / enum | noun, `PascalCase` | `TaskList`, `AudioSystem` | `taskList`, `task_list` |
| Variable | `camelCase` | `taskDetail`, `audioSystem` | `task_detail`, `TaskDetail` |
| Constant | `UPPER_SNAKE_CASE` | `MAX_ITERATIONS`, `COLOR_RED` | `maxIterations` |
| Method | verb, `camelCase` | `getName()`, `computeTotalWidth()` | `name()`, `mark_items()` |

**All names in English.** International audience; use American spelling.

**Abbreviations and acronyms are not uppercased inside a name.**

```java
exportHtmlSource();   // Good
openDvdPlayer();      // Good

exportHTMLSource();   // Bad
openDVDPlayer();      // Bad
```

**Name length tracks scope.** Large scope -> long descriptive name. Small scope
-> short name is fine. Scratch loop indices may be `i, j, k, m, n`; characters
`c, d`. Use `j`/`k` only for nested loops.

**Booleans sound like booleans.** Prefer an `is`/`has`/`was`/`can`/`should` prefix.

```java
// Good
boolean isSet;
boolean isVisible;
boolean hasData;
boolean wasOpen;
boolean hasLicense();
boolean canEvaluate();
void setFound(boolean isFound);

// Bad
boolean checkTask();   // returns a boolean but does not read like one
boolean done;
```

**Collections take a plural name.**

```java
Collection<Point> points;
int[] values;
```

**Associated constants share a common prefix.**

```java
static final int COLOR_RED   = 1;
static final int COLOR_GREEN = 2;
static final int COLOR_BLUE  = 3;
```

**Test methods** use `featureUnderTest_testScenario_expectedBehavior()`:

```java
sortList_emptyList_exceptionThrown()
getMember_memberNotFound_nullReturned()
```

Note the underscores here separate the three *segments*; each segment is still
`camelCase`. This is the one place underscores are expected in a method name.

---

## 2. Layout

**Indentation is 4 spaces. Never tabs.**

```java
for (int i = 0; i < numberOfElements; i++) {
    values[i] = 0;
}
```

**Line length: hard limit 120 chars, soft limit 110.**

**Wrapped lines are indented 8 spaces** (double the normal indentation), so a
continuation is visually distinct from a nested block.

**Where to break a line:**

- Break *after* a comma.
- Break *before* an operator (including `.`, `&` in type bounds, `|` in `catch`).
- Keep the method/constructor name attached to its opening `(`.
- Prefer higher-level breaks to lower-level breaks.

```java
// Good
totalSum = a + b + c
        + d + e;

setText("Long line split"
        + "into two parts.");

method(param1,
        object.method()
                .method2(),
        param3);

longName1 = longName2 * (longName3 + longName4 - longName5)
        + 4 * longName6;

// Bad - breaks inside a parenthesised sub-expression (a lower-level break)
longName1 = longName2 * (longName3 + longName4
        - longName5) + 4 * longName6;
```

Ternaries may stay on one line or break before both `?` and `:`:

```java
alpha = (aLongBooleanExpression) ? beta : gamma;

alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

**K&R ("Egyptian") braces.** The opening brace ends the line it opens.

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

**Standard statement forms:**

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

**Switch:** `case` labels are indented one level inside the `switch`.

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
```

An explicit `// Fallthrough` comment is required for any `case` without a `break`.
Arrow and expression forms are also acceptable:

```java
switch (condition) {
    case ABC -> method("1");
    default -> method("0");
}

int size = switch (condition) {
    case ABC -> 1;
    default -> 0;
};
```

**Whitespace inside statements:**

| Rule | Good | Bad |
|---|---|---|
| Operators surrounded by spaces | `a = (b + c) * d;` | `a=(b+c)*d;` |
| Java keyword followed by a space | `while (true) {` | `while(true){` |
| Comma followed by a space | `doSomething(a, b, c);` | `doSomething(a,b,c);` |
| Space before `{` | `catch (IOException e) {` | `catch (IOException e){` |
| `;` in a `for` followed by a space | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

**One blank line between logical units within a block.** A short comment
introducing each unit is often worth adding:

```java
// Create a new identity matrix
Matrix4x4 matrix = new Matrix4x4();

// Precompute angles for efficiency
double cosAngle = Math.cos(angle);
double sinAngle = Math.sin(angle);
```

---

## 3. Statements

### Package and imports

**Put every class in a package.** For a school project, root the package at the
project name, then logical groups: `quu.ui`, `quu.task`, `quu.storage`. Do not
use a package root that implies official NUS affiliation.

**Import classes explicitly. No wildcards.**

```java
// Good
import java.util.ArrayList;
import java.util.List;

// Bad
import java.util.*;
```

**Keep import ordering consistent**, static imports first, then groups separated
by blank lines:

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import quu.task.Task;
```

Remove unused imports.

### Types

**Array specifiers attach to the type, not the variable** - "arrayness" belongs
to the base type.

```java
int[] values = new int[20];   // Good
int values[] = new int[20];   // Bad
```

### Variables

**Initialise where declared; declare in the smallest possible scope.**

```java
// Good
int sum = 0;
for (int i = 0; i < 10; i++) {
    for (int j = 0; j < 10; j++) {
        sum += i * j;
    }
}

// Bad
int i, j, sum;
sum = 0;
for (i = 0; i < 10; i++) {
    ...
}
```

**Class variables are never `public`**, except constants and pure data classes
with no behaviour. Public fields break information hiding and encapsulation.

```java
// Bad
public class Foo {
    public int bar;
}
```

### Loops and conditionals

**Always brace the body**, however short.

```java
// Good
for (int i = 0; i < 100; i++) {
    sum += values[i];
}

// Bad
for (int i = 0; i < 100; i++) sum += values[i];
```

**Put the conditional on its own line** so a debugger can break on the body.

```java
// Good
if (isDone) {
    doCleanup();
}

// Bad
if (isDone) doCleanup();

// Bad
if (stream != null)
    readFile(stream);
```

---

## 4. Comments and Javadoc

**All comments in English**, American spelling, no local slang.

**Write descriptive header comments for all public classes and public methods.**
May be omitted for:

- getters and setters,
- overriding methods where the parent Javadoc applies verbatim (use `{@inheritDoc}`),
- test classes and test methods.

**Javadoc form:**

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
    ...
}
```

Conventions:

- `/**` on its own line; subsequent `*` aligned under the first `*`; a space after each `*`.
- The first sentence is a short summary - it is what appears in Javadoc summary tables.
- Method summaries start with a third-person verb: `Returns ...`, `Sends ...`, `Adds ...` -
  not `Return ...` or `Returning ...`.
- Blank line between the description and the `@param`/`@return`/`@throws` block.
- Punctuate parameter descriptions.
- No blank line between the Javadoc block and the class/method it documents.
- `@return` may be omitted when the method returns nothing or the return is obvious.
- `@param` may be omitted when parameters are self-explanatory or already covered in the description.

Single-line member documentation is fine:

```java
/** Number of connections to this database. */
private int connectionCount;
```

**Indent comments to match the code they describe.**

```java
// Good
while (true) {
    // Do something
    something();
}

// Bad
while (true) {
// Do something
    something();
}
```

Trailing comments are allowed: `process("ABC"); // process a dummy String first`

---

## Applying this skill

When asked to check or fix the standard across the project:

1. Grep for `snake_case` identifiers: `grep -rnE '\b[a-z]+_[a-z]' src --include=*.java`
2. Find over-long lines: `awk 'length>120 {print FILENAME":"FNR}' $(find src -name '*.java')`
3. Find missing space before brace: `grep -rnE '\)\{|\}else|try\{' src --include=*.java`
4. Find tabs: `grep -rlP '\t' src --include=*.java`
5. Find unbraced conditionals: `grep -rnE '^[[:space:]]*(if|for|while) \(.*\)[[:space:]]*[^{[:space:]].*$' src --include=*.java | grep -v '{[[:space:]]*$'`
6. Check every public class and public method for a Javadoc header.
7. Rebuild and rerun the tests after any rename - `./gradlew build`.
