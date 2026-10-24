---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (basic + intermediate rules) that ALL Java code in this project must follow. Use whenever writing, editing, generating, or reviewing Java code in this repository - including new classes, new methods, refactors, and code review. Covers naming, layout, statements, and Javadoc requirements.
---

# SE-EDU Java coding standard (basic + intermediate)

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

This is the coding standard for **all** Java code in this repository. Apply it to every
file you create or edit, and check against it before reporting a change as done.
For anything not covered here, fall back to the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## How to use this skill

1. **Before writing code** - skim "Quick checklist" below so the code is right first time.
2. **After editing** - run the checks in "Verifying compliance" and fix what they find.
3. **When reviewing** - walk the full rule list; cite the rule name when reporting a violation.

## Quick checklist

- [ ] Package all lowercase; every class lives in a package.
- [ ] Class/enum = PascalCase noun. Method = camelCase verb. Variable = camelCase.
      Constant = `SCREAMING_SNAKE_CASE`.
- [ ] Booleans read like booleans (`isDone`, `hasData`, `wasOpen`); collections are plural (`tasks`).
- [ ] Acronyms are not all-caps inside a name (`exportHtmlSource`, not `exportHTMLSource`).
- [ ] 4-space indent, no tabs. Wrapped lines indent 8 spaces. Lines < 110 chars (hard max 120).
- [ ] K&R braces. **Every** `if`/`for`/`while` body is braced, even one-liners, and on its own line.
- [ ] `case` labels are indented 4 spaces inside `switch`.
- [ ] Imports listed explicitly (no `.*`), in a consistent order.
- [ ] Locals initialized at declaration, declared in the smallest scope.
- [ ] No `public` non-constant fields.
- [ ] Javadoc header on every class and every public method/constructor, starting with a
      summary sentence in the form `Returns ...` / `Adds ...` / `Prints ...`.
- [ ] Comments in English, American spelling.

---

## Naming

| Element | Rule | Example |
| --- | --- | --- |
| Package | all lower case; root name = project name, then logical groups | `shannon.task`, `shannon.exception` |
| Class / enum | noun, PascalCase | `Task`, `AudioSystem` |
| Variable | camelCase | `line`, `audioSystem` |
| Constant | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS`, `COLOR_RED` |
| Method | **verb**, camelCase | `getName()`, `computeTotalWidth()` |

Do **not** use `edu.nus.comp.*` or similar as a package root - the code is not officially
produced by the university.

**A method name must be a verb.** A method named `plural(...)` or `taskCount(...)` fails this
rule; `pluralize(...)` and `countTasks(...)` pass. Watch for this - it is the easiest rule to
break accidentally when extracting a small helper.

**Abbreviations and acronyms are not uppercase inside a name.**

```java
exportHtmlSource();   // Good
openDvdPlayer();      // Good
exportHTMLSource();   // Bad
openDVDPlayer();      // Bad
```

**All names in English.** The code is meant for an international audience.

**Scope drives length.** Large scope -> long descriptive name. Small scope -> short name is fine.
Scratch/index variables may be `i`, `j`, `k`, `m`, `n` (integers) or `c`, `d` (characters).
`j`, `k` are for *nested* loops only.

**Booleans sound like booleans.** Prefix with `is`, `has`, `was`, `can`, `should` so linters can
verify the style:

```java
isSet, isVisible, isFinished, isFound, isOpen, hasData, wasOpen

boolean hasLicense();
boolean canEvaluate();
boolean shouldAbort = false;
```

Boolean setters take the form `void setFound(boolean isFound);`.

**Collections are plural.** `Collection<Point> points;`, `int[] values;`

**Associated constants share a prefix**, so they sort and read together:

```java
static final int COLOR_RED   = 1;
static final int COLOR_GREEN = 2;
static final int COLOR_BLUE  = 3;
```

**Test methods** may use underscores in the three-part form
`featureUnderTest_testScenario_expectedBehavior()`, e.g. `sortList_emptyList_exceptionThrown()`.
The third part, or both the second and third, may be omitted when the test covers all
variations of that scenario.

## Layout

**Indentation: 4 spaces, never tabs.**

**Line length: soft limit 110 chars, hard limit 120.** Wrap at a sensible place rather than
accepting whatever the IDE suggests.

**Wrapped lines are indented 8 spaces** (twice the normal 4) relative to the parent line:

```java
setText("Long line split"
        + "into two parts.");
```

**Where to break:**

- Break *after* a comma.
- Break *before* an operator - including the dot separator `.`, the `&` in type bounds
  `<T extends Foo & Bar>`, and the `|` in `catch (FooException | BarException e)`.
- Keep a method/constructor name attached to its opening `(`.
- Prefer higher-level breaks (outside a parenthesized expression) to lower-level ones.

```java
// Good
longName1 = longName2 * (longName3 + longName4 - longName5)
        + 4 * longname6;

// Bad
longName1 = longName2 * (longName3 + longName4
        - longName5) + 4 * longname6;
```

Ternaries may be written on one line, or broken before both `?` and `:`:

```java
alpha = (aLongBooleanExpression) ? beta : gamma;

alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

**K&R (Egyptian) braces** - the opening brace ends the line, it never gets a line of its own:

```java
while (!done) {        // Good
    doSomething();
}

while (!done)          // Bad
{
    doSomething();
}
```

**Statement forms:**

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

**`switch`: `case` labels are indented one level (4 spaces) inside the switch block.** This
applies to the classic form, the arrow form, and switch *expressions*:

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
    case DEF -> method("2");
    default -> method("0");
}

int size = switch (condition) {
    case ABC -> 1;
    case DEF -> 2;
    default -> 0;
};
```

An explicit `// Fallthrough` comment is required wherever a `case` has no `break`, so that a
deliberate fallthrough is not mistaken for the common bug of a forgotten `break`.

**Whitespace within a statement:**

| Rule | Good | Bad |
| --- | --- | --- |
| Operators surrounded by spaces | `a = (b + c) * d;` | `a=(b+c)*d;` |
| Reserved words followed by a space | `while (true) {` | `while(true){` |
| Commas followed by a space | `doSomething(a, b, c);` | `doSomething(a,b,c);` |
| Colons surrounded by space as a binary/ternary operator (not `switch` `case x:`); `;` in `for` followed by a space | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

**Separate logical units within a block with one blank line**, each often introduced by a
comment:

```java
// Create a new identity matrix
Matrix4x4 matrix = new Matrix4x4();

// Precompute angles for efficiency
double cosAngle = Math.cos(angle);
double sinAngle = Math.sin(angle);

// Apply rotation
transformation.multiply(matrix);
```

## Statements

### Package and import

**Put every class in a package.**

**Order imports consistently** - static imports first, then grouped by top-level domain, with a
blank line between groups:

```java
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javafx.geometry.Bounds;
import junit.framework.AssertionFailedError;
```

**List imported classes explicitly** - never `import java.util.*;`. An explicit list documents
the class's dependencies.

### Types

**Array specifiers attach to the type, not the variable** - arrayness is a feature of the type:

```java
int[] a = new int[20];   // Good
int a[] = new int[20];   // Bad
```

### Variables

**Initialize where declared, and declare in the smallest possible scope:**

```java
// Good
int sum = 0;
for (int i = 0; i < 10; i++) {
    sum += i;
}

// Bad
int i, sum;
sum = 0;
for (i = 0; i < 10; i++) {
    sum += i;
}
```

When a valid value genuinely is not available at the declaration (e.g. it is assigned inside a
`try`), leave the variable uninitialized rather than assigning a phony placeholder value.

**Never declare a field `public`** unless the class is a pure data class with no behavior.
This rule does not apply to constants. Public fields break encapsulation; use non-public fields
with accessor methods.

### Loops

**Always brace the loop body**, however few lines it has:

```java
for (i = 0; i < 100; i++) {   // Good
    sum += value[i];
}

for (i = 0; i < 100; i++)     // Bad
    sum += value[i];
```

### Conditionals

**Put the conditional body on its own line, always braced:**

```java
if (isDone) {          // Good
    doCleanup();
}

if (isDone) doCleanup();   // Bad

if (stream != null)        // Bad
    readFile(stream);
```

Writing the body on the same line hides from an IDE debugger whether the condition was true;
omitting braces invites subtle bugs when a second statement is added later.

## Comments

**English, American spelling, no local slang.** (`unrecognized`, not `unrecognised`.)

**Write header comments for every class and every public method.** They may be omitted only for:

1. getters/setters,
2. overriding methods, when the parent's Javadoc applies exactly as-is,
3. classes/methods used for testing.

Public methods are used by others, and those users should not have to read the body to learn
the exact behavior. Code can only show *how* it works; the comment states *what* it is supposed
to do.

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
    // ...
}
```

Note in particular:

- Opening `/**` on its own line; subsequent `*` aligned under the first; a space after each `*`.
- **The first sentence is a short summary** - Javadoc puts it in the summary table and index.
- In method headers that first sentence starts as `Returns ...`, `Sends ...`, `Adds ...`
  (third person), never `Return ...` or `Returning ...`.
- Empty line between the description and the parameter section.
- **A Javadoc block that is only `@param`/`@throws` tags with no summary sentence violates this
  rule** - add the summary sentence.
- Punctuation after each parameter description.
- No blank line between the documentation block and the method/class it documents.
- `@return` may be omitted when nothing is returned, or when the return value is obvious from
  the description.
- `@param` may be omitted when every parameter name is self-explanatory or already explained in
  the description. It is all-or-nothing: document every parameter, or none.
- Use `{@inheritDoc}` to reuse and extend a parent method's header comment.

**Class members may use a single-line Javadoc:**

```java
/** Number of connections to this database */
private int connectionCount;
```

**Indent comments to match the code they describe**, so they do not break up the logical
structure:

```java
while (true) {
    // Do something
    something();
}
```

Trailing comments are allowed: `process("ABC"); // process a dummy String first`

## Verifying compliance

Run these from the repository root after editing Java code:

```bash
# Lines over the hard limit of 120 chars (must be empty)
awk 'length > 120 {print FILENAME":"FNR" ("length" chars)"}' $(find src -name '*.java')

# Lines over the soft limit of 110 chars (review each)
awk 'length > 110 {print FILENAME":"FNR" ("length" chars)"}' $(find src -name '*.java')

# Tabs and trailing whitespace (must be empty)
grep -rnP '\t| +$' src --include='*.java'

# Wildcard imports (must be empty)
grep -rn 'import .*\*;' src --include='*.java'

# `case` labels not indented under their `switch` (must be empty)
awk '/switch *\(/ { match($0, /^ */); switchIndent = RLENGTH }
     /^ *case / { match($0, /^ */); if (RLENGTH <= switchIndent) print FILENAME":"FNR }' \
    $(find src -name '*.java')

# Unbraced single-line conditionals (review each hit)
grep -rnE 'if \(.*\) *[^ {].*;' src --include='*.java'

# British spellings (must be empty)
grep -rnE 'recognis|organis|behaviour|colour|initialis|analys|centre|licence|favour' src --include='*.java'
```

Then confirm the code still builds:

```bash
./gradlew compileJava
```

Reading the diff is still required - the greps catch layout, not naming or Javadoc quality.
