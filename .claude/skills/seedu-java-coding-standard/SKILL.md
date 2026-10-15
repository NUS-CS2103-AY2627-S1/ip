---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (intermediate level) that all Java in this project follows - naming, layout, statements, and comments. Use when writing or editing any Java file, when reviewing code for style, or when asked whether something follows the coding standard.
---

# SE-EDU Java coding standard (intermediate)

The rules below are the intermediate-level standard from
<https://se-education.org/guides/conventions/java/intermediate.html>, which all
Java in this project follows. `config/checkstyle/checkstyle.xml` enforces the
mechanical parts; the rest are checked by reading.

Run `./gradlew checkstyleMain checkstyleTest` after editing Java. A rule below
that checkstyle does not cover still applies.

## Naming

* Packages are all lower case: `thomas.task`, not `thomas.Task`.
* Classes and enums are nouns in `PascalCase`: `Line`, `AudioSystem`.
* Variables are `camelCase`: `line`, `audioSystem`.
* Constants are `SCREAMING_SNAKE_CASE`: `MAX_ITERATIONS`, `COLOR_RED`.
* Methods are verbs in `camelCase`: `getName()`, `computeTotalWidth()`.
* Test methods are `featureUnderTest_testScenario_expectedBehavior()`, for
  example `sortList_emptyList_exceptionThrown()`.
* Abbreviations are not upper case: `exportHtmlSource()`, not
  `exportHTMLSource()`.
* All names are in English. The code is meant for an international audience.
* Name length follows scope: a variable with a large scope gets a long name, one
  with a small scope may be short.
* Booleans read as questions, prefixed `is`, `has` or `was`: `isSet`,
  `hasData`. A setter takes the same form: `void setFound(boolean isFound)`.
* Collections are plural: `Collection<Point> points`, `int[] values`.
* Loop counters may be `i`, then `j`, `k` when nested.
* Constants that belong together share a prefix: `COLOR_RED`, `COLOR_GREEN`.

## Layout

* Indent with 4 spaces. Never tabs.
* Lines are at most 120 characters. Aim for 110.
* A wrapped line is indented 8 spaces, twice the normal indentation.
* Break after a comma, and before an operator, including `.`, `&` and `|`. Keep
  a method name attached to its opening parenthesis. Prefer a break at the
  highest level of the expression.

```java
setText("Long line split"
        + "into two parts.");

method(param1,
        object.method()
                .method2(),
        param3);

longName1 = longName2 * (longName3 + longName4 - longName5)
        + 4 * longname6
```

* A ternary is on one line, or split across exactly three:

```java
alpha = (aLongBooleanExpression) ? beta : gamma;

alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

* Braces are K&R ("Egyptian"): the opening brace ends the line that opens the
  block, never starts a line of its own.
* Separate logical units within a block with one blank line.
* Whitespace:

| Rule | Good | Bad |
|---|---|---|
| Operators surrounded by space | `a = (b + c) * d;` | `a=(b+c)*d;` |
| Reserved word followed by space | `while (true) {` | `while(true){` |
| Comma followed by space | `doSomething(a, b, c, d);` | `doSomething(a,b,c,d);` |
| Semicolons in a `for` | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

* `switch` bodies are indented one level in from the `switch`, in every form:

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

int size = switch (condition) {
    case ABC -> 1;
    default -> 0;
};
```

## Statements

* Every class belongs to a package.
* Import each class explicitly. Never `import java.util.*;` — the explicit form
  documents what the file actually uses.
* Order imports in groups, separated by blank lines, with no blank line inside a
  group: static imports, then `java`, `javax`, `org`, `com`, and anything else.

```java
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import thomas.task.Task;
```

* Array brackets attach to the type: `int[] a`, not `int a[]`. Being an array is
  a feature of the type, not of the name.
* Declare a variable in the smallest scope that works, and initialize it where
  it is declared.
* Class variables are never `public`, unless the class is a data class with no
  behavior. Constants are the exception.
* A loop body is always wrapped in braces, however short it is.
* A conditional goes on its own line, and its body is always wrapped in braces,
  even for a single statement. Both rules exist because the brace-less form is
  easy to break when a second statement is added later.

## Comments

* Comments are in English, in American spelling.
* Write a header comment for every class and every public method. It may be
  omitted for getters and setters, for an overridden method whose parent comment
  applies as it stands, and in test classes.
* Indent a comment to the level of the code it describes.
* Javadoc takes this form:

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

  `/**` on its own line, `*` aligned beneath it with a space after each, no
  blank line between the block and what it documents. The first sentence is a
  summary in the third person — `Returns …`, `Adds …`, never `Return` or
  `Returning` — because Javadoc lifts it into the summary table. One blank `*`
  line separates the description from the tags. Every `@param`, `@return` and
  `@throws` description is capitalized and ends with a period. `@return` may be
  dropped when the method returns nothing or the value is already obvious.
  `@param` is all or nothing: document every parameter, or none.

## Checking a file against this standard

1. Read the naming, layout and statement rules above against the file.
2. Run `./gradlew checkstyleMain checkstyleTest`. It catches indentation, line
   length, import order, unused imports, missing Javadoc and declaration order.
3. Report what was changed and why, per rule. If a rule is deliberately not
   followed, say so and give the reason rather than leaving it unexplained.
