# The se-edu Java coding standard, intermediate level

Written out from
[the guide](https://se-education.org/guides/conventions/java/intermediate.html).
The guide is the authority; this file is a copy for use while writing. If the
two disagree, follow the guide and say that this file is out of date.

## Naming

| Applies to | Rule | Good | Bad |
| --- | --- | --- | --- |
| Packages | All lowercase | `alfred.task`, `alfred.command` | `alfred.Task` |
| Classes and enums | Nouns, PascalCase | `Line`, `AudioSystem` | `parseLine`, `audio_system` |
| Methods | Verbs, camelCase | `getName()`, `computeTotalWidth()` | `Name()`, `total_width()` |
| Variables | camelCase | `line`, `audioSystem` | `Line`, `audio_system` |
| Constants | Uppercase, underscores | `MAX_ITERATIONS`, `COLOR_RED` | `maxIterations` |

More:

* **Write all names in English.**
* **Do not uppercase abbreviations and acronyms** inside a name. Write
  `exportHtmlSource()` and `openDvdPlayer()`, not `exportHTMLSource()` or
  `openDVDPlayer()`.
* **Match the length of a name to its scope.** A variable that lives across a
  long method earns a long name; one that lives for three lines does not.
  `i`, `j`, `k`, `m`, and `n` are for integers, `c` and `d` for characters.
* **Use `i` for a loop counter**, and `j` and `k` only for loops nested inside
  it.
* **Name booleans so that they read as booleans.** Variables:
  `isSet`, `isVisible`, `isFinished`, `isFound`, `isOpen`, `hasData`,
  `wasOpen`. Methods: `boolean hasLicense()`, `boolean canEvaluate()`. A setter
  takes the form `void setFound(boolean isFound)`.
* **Use the plural for a collection**: `Collection<Point> points`,
  `int[] values`.
* **Give associated constants a common prefix**, so that they sort and read
  together:

  ```java
  static final int COLOR_RED = 1;
  static final int COLOR_GREEN = 2;
  static final int COLOR_BLUE = 3;
  ```

  A shared _suffix_, as in `RED_COLOR` and `GREEN_COLOR`, does not satisfy this
  rule.
* **Name a test method `featureUnderTest_testScenario_expectedBehavior()`**, as
  in `sortList_emptyList_exceptionThrown()` or
  `getMember_memberNotFound_nullReturned()`.

## Layout

* **Indent with 4 spaces.** Never tabs.
* **Keep a line under 120 characters**, and preferably under 110.
* **Indent a wrapped line 8 spaces past the line it continues**, which is twice
  the normal indentation:

  ```java
  setText("Long line split"
          + "into two parts.");
  ```

* **Break a line where it reads best**: after a comma, and _before_ an
  operator, including `.`, `&`, and `|`. Keep a method or constructor name
  attached to its opening parenthesis. Prefer a break at the highest level of
  the expression to one buried inside it.
* **Write a ternary either on one line or across three**, the condition on the
  first and each branch on its own.
* **Put the opening brace at the end of the line that opens the block**, K&R
  style, and the closing brace on a line of its own:

  ```java
  while (!done) {
      doSomething();
      done = moreToDo();
  }
  ```

* **Align each `case` with the `switch` itself**, indenting only the statements
  under it, and give each `case` without a `break` an explicit `// Fallthrough`
  comment:

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

  This is the guide's own form, and it is not what most IDEs produce by default,
  so it is worth setting the IDE to match rather than reindenting by hand.
  Checkstyle enforces it through `caseIndent = 0`.

  An arrow `switch` is laid out the same way, and so is a `switch` used as an
  expression:

  ```java
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
* **Put a space around every operator, after every comma, and after every
  reserved word.** Write `a = (b + c) * d;`, `doSomething(a, b, c, d);`, and
  `while (true) {` — not `a=(b+c)*d;`, `doSomething(a,b,c,d);`, or
  `while(true){`.
* **Separate the logical units within a block with one blank line.**

Statement forms:

```java
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

## Statements

### Packages and imports

* **Put every class in a package.**
* **Import every class explicitly.** No wildcard imports: write
  `import java.util.List;` and `import java.util.ArrayList;`, never
  `import java.util.*;`.
* **Keep the order of imports consistent.** The guide's order is static
  imports, then `java.*`, then `javax.*`, then third-party `org.*`, then
  company packages, then `javafx` and whatever remains.

### Types

* **Attach an array specifier to the type, not to the variable.** Write
  `int[] a = new int[20];`, not `int a[] = new int[20];`.

### Variables

* **Initialize a variable where you declare it, and declare it in the smallest
  scope that will hold it.** Do not gather declarations at the top of a method
  and assign to them later.
* **Never declare a class variable public**, unless the class is a data class
  with no behavior. Constants are the exception and may be public.

### Loops and conditionals

* **Brace every loop body**, however few lines it holds.
* **Put the condition on a line of its own**: not `if (isDone) doCleanup();`.
* **Brace every conditional body too**, including a single statement. Write:

  ```java
  if (stream != null) {
      readFile(stream);
  }
  ```

## Comments

* **Write every comment in English**, in American spelling, without local
  slang.
* **Write a descriptive header comment for every class and every public
  method.** You can leave one off a getter or setter, an overriding method
  whose parent Javadoc still applies exactly, or a class or method used only
  for testing.
* **Indent a comment to the level of the code it describes.** A trailing
  comment on the same line as a statement is allowed.

### Javadoc format

* Put `/**` on a line of its own, align the `*` below it, and leave a space
  after each `*`.
* **Make the first sentence a summary**, because Javadoc lifts it into the
  summary table. Start a method's summary with a third-person verb: `Returns`,
  `Sends`, `Adds` — not `Return` or `Returning`.
* Leave an empty line between the description and the parameter section, and no
  blank line between the comment and the thing it documents.
* Punctuate every parameter description.
* You can omit `@return` when the method returns nothing or the return is
  obvious, and omit `@param` when every parameter is self-explanatory or
  already explained in the description. It is all of the `@param` tags or none.
* Use `{@inheritDoc}` on an overriding method you want to add to rather than
  replace.

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

A member can take a one-line form:

```java
/** Number of connections to this database */
private int connectionCount;
```
