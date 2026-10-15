# SE-EDU Java coding standard — examples

Source: [basic + intermediate](https://se-education.org/guides/conventions/java/intermediate.html).
Unlisted topics: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Naming

```java
// packages
kiwi.task

// types / variables / constants / methods
Line, AudioSystem
line, audioSystem
MAX_ITERATIONS, COLOR_RED
getName(), computeTotalWidth()

// abbreviations stay mixed-case
exportHtmlSource();
openDvdPlayer();
// not exportHTMLSource() / openDVDPlayer()

// booleans
boolean isSet, isVisible, isFinished, isFound, isOpen, hasData, wasOpen;
boolean hasLicense();
boolean canEvaluate();
boolean shouldAbort = false;
void setFound(boolean isFound);

// collections
Collection<Point> points;
int[] values;

// associated constants
static final int COLOR_RED = 1;
static final int COLOR_GREEN = 2;
static final int COLOR_BLUE = 3;
```

Tests: `sortList_emptyList_exceptionThrown()`, `getMember_memberNotFound_nullReturned()`.

## Line wrapping

```java
setText("Long line split"
        + "into two parts.");
if (isReady) {
    setText("Long line split"
            + "into two parts.");
}

totalSum = a + b + c
        + d + e;
method(param1,
        object.method()
                .method2(),
        param3);

// name stays attached to '('
someMethodWithVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongName(
        int anArg, Object anotherArg);

// prefer the higher-level break
longName1 = longName2 * (longName3 + longName4 - longName5)
        + 4 * longName6;

alpha = (aLongBooleanExpression) ? beta : gamma;
alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

## Whitespace

| Rule | Good | Bad |
| --- | --- | --- |
| Spaces around operators | `a = (b + c) * d;` | `a=(b+c)*d;` |
| Space after reserved words | `while (true) {` | `while(true){` |
| Space after commas | `doSomething(a, b, c, d);` | `doSomething(a,b,c,d);` |
| `for` semicolons | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

## Imports

```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import kiwi.KiwiException;
import kiwi.task.Task;
```

No `import java.util.*;`.

## Variables and arrays

```java
int[] values = new int[20]; // not int values[]

int sum = 0;
for (int i = 0; i < 10; i++) {
    for (int j = 0; j < 10; j++) {
        sum += i * j;
    }
}
```

Do not declare `public int bar;` on a behavioral class.

## Conditionals and loops

```java
// good
if (isDone) {
    doCleanup();
}

for (int i = 0; i < 100; i++) {
    sum += value[i];
}

// bad: one-liners / missing braces
if (isDone) doCleanup();
for (int i = 0; i < 100; i++)
    sum += value[i];
```

## Switch (including modern Java)

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

## Comments

```java
while (true) {
    // Do something
    something();
}

process("ABC"); // trailing comment is allowed
```
