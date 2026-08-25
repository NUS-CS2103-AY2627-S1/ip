# Java Coding Standard (Basic + Intermediate)

> Converted to portable Markdown from the SE-EDU Java coding standard (basic + intermediate).
>
> Source: https://se-education.org/guides/conventions/java/intermediate.html  
> Upstream repository: `se-edu/guides` (MIT License)

**Versions:** [Basic Rules](https://se-education.org/guides/conventions/java/basic.html) · [Basic + Intermediate Rules](https://se-education.org/guides/conventions/java/intermediate.html) · [All Rules](https://se-education.org/guides/conventions/java/index.html)

Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for any topics not covered in this document.

## Contents

- [Naming](#naming)
- [Layout](#layout)
- [Statements](#statements)
  - [Package and Import Statements](#package-and-import-statements)
  - [Types](#types)
  - [Variables](#variables)
  - [Loops](#loops)
  - [Conditionals](#conditionals)
- [Comments](#comments)
- [References](#references)
- [Contributors](#contributors)

---

## Naming

### Names representing packages should be in all lower case

```java
com.company.application.ui
```

[More on package naming](https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html)

For school projects, the root name of the package should be your group name or project name followed by logical group names, e.g. `todobuddy.ui`, `todobuddy.file`, etc.

> **Rationale:** Your code is not officially "produced by NUS", therefore do not use `edu.nus.comp.*` or anything similar.

### Class/enum names must be nouns and written in PascalCase

```java
Line, AudioSystem
```

### Variable names must be in camelCase

```java
line, audioSystem
```

### Constant names must use SCREAMING_SNAKE_CASE

Constant names must be all uppercase, using underscores to separate words. To determine what exactly is considered a constant, refer to the [Google Java Style Guide section on constant names](https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names).

```java
MAX_ITERATIONS, COLOR_RED
```

### Method names must be verbs and written in camelCase

```java
getName(), computeTotalWidth()
```

Underscores may be used in test method names using the following three-part format:

```text
featureUnderTest_testScenario_expectedBehavior()
```

For example:

```java
sortList_emptyList_exceptionThrown()
getMember_memberNotFound_nullReturned()
```

The third part, or both the second and third parts, can be omitted depending on what is covered in the test. For example, `sortList_emptyList()` can test `sortList()` for all variations of the empty-list scenario, while `sortList()` can test the method across all scenarios.

### Abbreviations and acronyms should not be uppercase when used as part of a name

**Good**

```java
exportHtmlSource();
openDvdPlayer();
```

**Bad**

```java
exportHTMLSource();
openDVDPlayer();
```

### All names should be written in English

> **Rationale:** The code is meant for an international audience.

### Variable-name length should reflect variable scope

Variables with a large scope should have long names; variables with a small scope can have short names.

Scratch variables used for temporary storage or indices can be kept short. A programmer reading such variables should be able to assume that their values are not used outside a few lines of code. Common scratch variables for integers are `i`, `j`, `k`, `m`, and `n`, and for characters `c` and `d`.

> **Rationale:** When the scope is small, the reader does not have to remember the variable for long.

### Boolean variables and methods should be named to sound like booleans

```java
// variables
isSet, isVisible, isFinished, isFound, isOpen, hasData, wasOpen

// methods
boolean hasLicense();
boolean canEvaluate();
boolean shouldAbort = false;
```

As much as possible, use a prefix such as `is`, `has`, `was`, etc. for boolean variable and method names so that linters can automatically verify that this style rule is being followed.

Setter methods for boolean variables must be of the form:

```java
void setFound(boolean isFound);
```

> **Rationale:** This is the naming convention for boolean methods and variables used by Java core packages. It also makes code read like normal English, e.g. `if (isOpen) ...`.

### Use plural names for collections of objects

```java
Collection<Point> points;
int[] values;
```

> **Rationale:** This improves readability because the name gives the reader an immediate clue about the type of variable and the operations that can be performed on its elements.

### Iterator variables can be called `i`, `j`, `k`, etc.

Variables named `j`, `k`, etc. should be used for nested loops only.

```java
for (Iterator i = points.iterator(); i.hasNext(); ) {
    ...
}

for (int i = 0; i < nTables; i++) {
    ...
}
```

> **Rationale:** The notation is taken from mathematics, where it is an established convention for indicating iterators.

### Associated constants should have a common prefix

```java
static final int COLOR_RED   = 1;
static final int COLOR_GREEN = 2;
static final int COLOR_BLUE  = 3;
```

> **Rationale:** This indicates that the constants belong together and makes them appear together when sorted alphabetically.

---

## Layout

### Use 4 spaces for basic indentation, not tabs

```java
for (i = 0; i < nElements; i++) {
    a[i] = 0;
}
```

### Keep line length at or below 120 characters

Try to keep lines shorter than 110 characters as a soft limit. It is acceptable to exceed that slightly, but 120 characters is the hard limit. If a line exceeds the limit, wrap it at an appropriate point.

Indentation for wrapped lines should be **8 spaces** more than the parent line, i.e. twice the normal indentation of 4 spaces.

```java
setText("Long line split"
        + "into two parts.");

if (isReady) {
    setText("Long line split"
            + "into two parts.");
}
```

### Place line breaks to improve readability

When wrapping lines, the main objective is to improve readability. Do not always accept the auto-formatting suggested by the IDE.

In general:

- Break after a comma.
- Break before an operator. This also applies to operator-like symbols such as:
  - the dot separator `.`
  - the ampersand in type bounds, e.g. `<T extends Foo & Bar>`
  - the pipe in catch blocks, e.g. `catch (FooException | BarException e)`

```java
totalSum = a + b + c
        + d + e;

setText("Long line split"
        + "into two parts.");

method(param1,
        object.method()
                .method2(),
        param3);
```

A method or constructor name stays attached to the opening parenthesis `(` that follows it.

**Good**

```java
someMethodWithVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongName(
        int anArg, Object anotherArg);
```

**Bad**

```java
someMethodWithVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongName
        (int anArg, Object anotherArg);
```

Prefer higher-level breaks to lower-level breaks. In the example below, the first form is preferred because the break occurs outside the parenthesised expression, which is at a higher level.

**Good**

```java
longName1 = longName2 * (longName3 + longName4 - longName5)
        + 4 * longname6;
```

**Bad**

```java
longName1 = longName2 * (longName3 + longName4
        - longName5) + 4 * longname6;
```

Two acceptable ways to format ternary expressions are:

```java
alpha = (aLongBooleanExpression) ? beta : gamma;

alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

### Use K&R-style brackets

Also known as [Egyptian style](https://blog.codinghorror.com/new-programming-jargon/).

**Good**

```java
while (!done) {
    doSomething();
    done = moreToDo();
}
```

**Bad**

```java
while (!done)
{
    doSomething();
    done = moreToDo();
}
```

### Method definitions

Method definitions should have the following form:

```java
public void someMethod() throws SomeException {
    ...
}
```

### `if`/`else` statements

```java
if (condition) {
    statements;
}
```

```java
if (condition) {
    statements;
} else {
    statements;
}
```

```java
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
```

### `for` statements

```java
for (initialization; condition; update) {
    statements;
}
```

### `while` and `do-while` statements

```java
while (condition) {
    statements;
}
```

```java
do {
    statements;
} while (condition);
```

### `switch` statements

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

Arrow form:

```java
switch (condition) {
    case ABC -> method("1");
    case DEF -> method("2");
    case XYZ -> method("3");
    default -> method("0");
}
```

Switch expression:

```java
int size = switch (condition) {
    case ABC -> 1;
    case DEF -> 2;
    case XYZ -> 3;
    default -> 0;
};
```

The explicit `// Fallthrough` comment should be included whenever there is a `case` statement without a `break` statement.

> **Rationale:** Leaving out `break` is a common error, so it must be made clear when the omission is intentional.

### `try-catch` statements

```java
try {
    statements;
} catch (Exception exception) {
    statements;
}
```

```java
try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
```

### White space within a statement

The following examples illustrate the expected use of whitespace in Java code.

| Rule | Good | Bad |
|---|---|---|
| Operators should be surrounded by spaces. | `a = (b + c) * d;` | `a=(b+c)*d;` |
| Java reserved words should be followed by whitespace. | `while (true) {` | `while(true){` |
| Commas should be followed by whitespace. | `doSomething(a, b, c, d);` | `doSomething(a,b,c,d);` |
| Colons should be surrounded by whitespace when used as a binary/ternary operator. This does not apply to `switch x:`. Semicolons in `for` statements should be followed by a space. | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

> **Rationale:** Whitespace makes the individual components of statements stand out and improves readability.

### Separate logical units within a block by one blank line

```java
// Create a new identity matrix
Matrix4x4 matrix = new Matrix4x4();

// Precompute angles for efficiency
double cosAngle = Math.cos(angle);
double sinAngle = Math.sin(angle);

// Specify matrix as a rotation transformation
matrix.setElement(1, 1,  cosAngle);
matrix.setElement(1, 2,  sinAngle);
matrix.setElement(2, 1, -sinAngle);
matrix.setElement(2, 2,  cosAngle);

// Apply rotation
transformation.multiply(matrix);
```

> **Rationale:** Blank lines improve readability by separating logical units. Each block is often introduced by a comment, as in the example above.

---

## Statements

### Package and Import Statements

#### Put every class in a package

Every class should be part of some package.

> **Rationale:** Grouping classes into packages helps you and other developers understand the structure of the codebase.

#### Keep import ordering consistent

> **Rationale:** Consistent ordering makes it easier to browse imports and determine dependencies when there are many imports.

Example:

```java
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;

import com.google.common.io.Files;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import junit.framework.AssertionFailedError;
```

IDEs support automatic import ordering. However, the default ordering differs between IDEs. Teams should therefore use a consistent ordering scheme.

#### Always list imported classes explicitly

**Good**

```java
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
```

**Bad**

```java
import java.util.*;
```

> **Rationale:** Explicit imports provide useful documentation about a class's dependencies and make the class easier to understand and maintain. IDEs can be configured to keep the import list minimal and up to date.

### Types

#### Attach array specifiers to the type, not the variable

**Good**

```java
int[] a = new int[20];
```

**Bad**

```java
int a[] = new int[20];
```

> **Rationale:** Array-ness is a feature of the base type, not the variable. Java allows both forms, but the former is preferred.

### Variables

#### Initialise variables where they are declared and use the smallest possible scope

**Good**

```java
int sum = 0;
for (int i = 0; i < 10; i++) {
    for (int j = 0; j < 10; j++) {
        sum += i * j;
    }
}
```

**Bad**

```java
int i, j, sum;
sum = 0;
for (i = 0; i < 10; i++) {
    for (j = 0; j < 10; j++) {
        sum += i * j;
    }
}
```

> **Rationale:** This helps ensure that variables are valid whenever they are in scope. If a variable cannot be initialised to a valid value where it is declared, leave it uninitialised rather than assigning a phoney value.

#### Class variables should not be public unless the class is a data class with no behaviour

This rule does not apply to constants.

**Bad**

```java
public class Foo {
    public int bar;
}
```

> **Rationale:** Public variables violate Java's information-hiding and encapsulation principles. Use non-public variables and accessor methods instead.

### Loops

#### Always wrap loop bodies in curly brackets

**Good**

```java
for (i = 0; i < 100; i++) {
    sum += value[i];
}
```

**Bad**

```java
for (i = 0, sum = 0; i < 100; i++)
    sum += value[i];
```

> **Rationale:** Java permits a one-statement loop body without braces, but this is error-prone and strongly discouraged.

### Conditionals

#### Put the conditional body on a separate line

**Good**

```java
if (isDone) {
    doCleanup();
}
```

**Bad**

```java
if (isDone) doCleanup();
```

> **Rationale:** This makes debugging easier. With a one-line conditional, it is less apparent whether the condition evaluated to true.

#### Wrap single-statement conditionals in curly brackets

**Good**

```java
InputStream stream = File.open(fileName, "w");
if (stream != null) {
    readFile(stream);
}
```

**Bad**

```java
InputStream stream = File.open(fileName, "w");
if (stream != null)
    readFile(stream);
```

The body of a conditional should be wrapped in curly brackets regardless of the number of statements.

> **Rationale:** Omitting braces can lead to subtle bugs.

---

## Comments

### Write all comments in English

Use American spelling and avoid local slang.

> **Rationale:** The code is meant for an international audience.

### Write descriptive header comments for all public classes and methods

Header comments are required for all classes and public methods, but may be omitted for:

1. Getters and setters.
2. Overridden methods, provided the parent method's Javadoc applies exactly as-is to the overridden method.
3. Classes and methods used for testing.

> **Rationale:** Public methods are intended to be used by others, and users should not be forced to read the implementation to understand the method's exact behaviour. Even self-explanatory code shows *how* something works, not necessarily *what* it is supposed to do.

### Javadoc format

Javadoc comments should have the following form:

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

In particular:

- Put the opening `/**` on a separate line.
- Write the first sentence as a short summary of the method, because Javadoc automatically places it in the method summary table and index.
- In method header comments, start the first sentence in the form `Returns ...`, `Sends ...`, `Adds ...`, etc., rather than `Return ...` or `Returning ...`.
- Align subsequent `*` characters with the first one.
- Put a space after each `*`.
- Put an empty line between the description and parameter section.
- Put punctuation after each parameter description.
- Do not put a blank line between the documentation block and the method or class.
- `@return` can be omitted if the method returns nothing or if the return value is obvious from the rest of the comment.
- `@param` entries can be omitted if all parameter names are self-explanatory or are already explained in the main part of the comment. This means a comment should generally document all parameters with `@param`, or none of them.
- When documenting overridden methods, `{@inheritDoc}` can be used to reuse the parent method's header comment while adding modifications for behaviour that differs slightly from the parent.

### Single-line Javadoc for class members

Javadoc for class members can be written on a single line:

```java
/** Number of connections to this database */
private int connectionCount;
```

### Indent comments relative to their position in the code

**Good**

```java
while (true) {
    // Do something
    something();
}
```

**Bad**

```java
while (true) {
        // Do something
    something();
}
```

**Bad**

```java
while (true) {
// Do something
    something();
}
```

> **Rationale:** Correct indentation prevents comments from breaking the logical structure of the program.

Trailing comments are also allowed:

```java
process("ABC"); // process a dummy String first
```

---

## References

1. [Oracle's Java Style Guide](https://www.oracle.com/docs/tech/java/codeconventions.pdf)
2. [Google's Java Style Guide](https://google.github.io/styleguide/javaguide.html)

## Contributors

- Nimantha Baranasuriya — Initial draft
- Dai Thanh — Further tweaks
- Tong Chun Kit — Further tweaks
- Barnabas Tan — Converted from Google Docs to Markdown document

---

## Attribution

This portable Markdown version was converted from the rendered SE-EDU guide at:

https://se-education.org/guides/conventions/java/intermediate.html

The upstream `se-edu/guides` repository is published under the MIT License.
