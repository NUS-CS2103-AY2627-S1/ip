---
name: seedu-java-coding-standard
description: The mandatory Java coding standard for this project, from se-education.org's intermediate-level guide. Read this BEFORE writing or editing any .java file in src/main/java or src/test/java, and before reviewing Java code. Covers naming (classes, methods, variables, constants, booleans, collections), layout (4-space indent, 120-char lines, 8-space wrap, K&R braces), whitespace, imports (explicit, ordered, no wildcards), variable scope, mandatory braces, and Javadoc format (third-person summary, punctuated tags, when headers may be omitted). Triggers on "coding standard", "code style", "conventions", "checkstyle", "naming", "Javadoc format", or any Java authoring/review task here.
---

# se-education Java Coding Standard (intermediate level)

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

This standard is **mandatory** for all Java in this repository, in both
`src/main/java` and `src/test/java`. When it is silent on a topic, fall back to
Google's Java Style Guide.

## Quick checklist

Run through this before finishing any Java edit:

- [ ] Names: `PascalCase` classes, `camelCase` methods/variables, `SCREAMING_SNAKE_CASE` constants
- [ ] Booleans read as yes/no questions (`is`, `has`, `was`, `can`, `should`)
- [ ] Methods are verbs; classes are nouns
- [ ] Acronyms are not all-caps inside names (`exportHtmlSource`, not `exportHTMLSource`)
- [ ] 4 spaces, never tabs; lines under 120 chars (aim for 110)
- [ ] Every `if`/`for`/`while` body wrapped in braces, even one-liners
- [ ] Imports explicit and ordered; no wildcards
- [ ] Variables declared in the smallest scope, initialised where declared
- [ ] Javadoc on every non-private class and method (exceptions below)
- [ ] Javadoc summary is third person (`Returns…`), tag descriptions end with a period

## 1. Naming

| Element | Rule | Example |
|---|---|---|
| Package | all lower case | `peter.task` |
| Class / enum | noun, `PascalCase` | `TaskList`, `AudioSystem` |
| Method | verb, `camelCase` | `getName()`, `computeTotalWidth()` |
| Variable | `camelCase` | `taskCount` |
| Constant | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS` |

- **All names in English.**
- **Booleans must sound like booleans.** Prefix with `is`, `has`, `was`, `can`,
  or `should`: `isSet`, `hasData`, `wasOpen`, `canEvaluate`, `shouldAbort`.
  Setters take the same form: `void setFound(boolean isFound)`.
- **Collections take plural names**: `Collection<Point> points`, `int[] values`.
- **Associated constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`.
- **Scope drives length.** Wide-scope names are long and descriptive;
  short-lived scratch names may be short. `i`, `j`, `k`, `m`, `n` for integer
  indices and `c`, `d` for characters are acceptable, with `j`/`k` reserved for
  nested loops.
- **Acronyms are not uppercased mid-name.**

```java
// GOOD
exportHtmlSource();
openDvdPlayer();

// BAD
exportHTMLSource();
openDVDPlayer();
```

### Test method names

Use `featureUnderTest_testScenario_expectedBehavior()`. The third part, or both
the second and third, may be dropped when they add nothing.

```java
sortList_emptyList_exceptionThrown()
getMember_memberNotFound_nullReturned()
```

## 2. Layout

- **Indent 4 spaces. Never tabs.**
- **Line length: 120 characters hard limit**, and prefer to stay under 110.
- **Wrapped lines indent 8 spaces** (double the normal indent).
- **K&R / "Egyptian" braces**: the opening brace ends the line that opens the
  block.

```java
// GOOD
while (!isDone) {
    doSomething();
}

// BAD
while (!isDone)
{
    doSomething();
}
```

### Wrapping

Break after a comma, or before an operator (`+`, `-`, `.`, `&` in type bounds,
`|` in catch clauses). Prefer breaking at the highest syntactic level. Keep a
method name attached to its opening parenthesis.

```java
// GOOD
someMethodWithVeryLongName(
        int anArg, Object anotherArg);

// BAD
someMethodWithVeryLongName
        (int anArg, Object anotherArg);
```

### Whitespace

| Rule | Good | Bad |
|---|---|---|
| Spaces around operators | `a = (b + c) * d;` | `a=(b+c)*d;` |
| Space after reserved words | `while (true) {` | `while(true){` |
| Space after commas | `doSomething(a, b, c);` | `doSomething(a,b,c);` |
| Spaces in `for` clauses | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

Separate logical units inside a block with a single blank line.

## 3. Statements

### Packages and imports

- **Every class lives in a package.**
- **List imports explicitly. Never use a wildcard.**

```java
// GOOD
import java.util.ArrayList;
import java.util.List;

// BAD
import java.util.*;
```

- **Import order must be consistent.** This project uses, with a blank line
  between each group:

  1. static imports
  2. `java.*`
  3. `javax.*`
  4. `org.*`
  5. `com.*`
  6. everything else, including this project's own `peter.*`

  Sort alphabetically within each group.

### Types

Attach array brackets to the type, since arrayness belongs to the type:

```java
int[] values = new int[20];   // GOOD
int values[] = new int[20];   // BAD
```

### Variables

- **Declare in the smallest possible scope and initialise at the point of
  declaration.** Do not hoist declarations to the top of a method.
- **Never make a class variable `public`** unless the class is a behaviourless
  data class. Constants are exempt.

### Braces are mandatory

Wrap every loop and conditional body in braces regardless of length, and put
the controlled statement on its own line. Omitting braces is error prone, and
a one-line `if` is awkward to breakpoint in a debugger.

```java
// GOOD
if (stream != null) {
    readFile(stream);
}

// BAD
if (stream != null) readFile(stream);
```

### Switch

Arrow form (`case ABC -> ...`) and switch expressions are both fine. In the
traditional form, any `case` without a `break` needs an explicit
`// Fallthrough` comment.

## 4. Comments and Javadoc

- **English, American spelling, no slang.**
- **Indent comments to match the code they describe.**

### Where headers are required

Write a Javadoc header for **every class** and **every public method**. You may
omit it for:

1. getters and setters,
2. overriding methods, when the parent's Javadoc applies unchanged,
3. test classes and test methods.

### Format

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
```

Requirements:

- `/**` sits alone on its own line; later `*` characters align under the first,
  each followed by a space.
- The **first sentence is a short summary**, since Javadoc lifts it into the
  summary table.
- Method summaries are **third person, not imperative**: `Returns …`,
  `Sends …`, `Adds …` — never `Return …` or `Send …`.
- One blank line between the description and the first tag.
- **Every tag description ends with punctuation.**
- No blank line between the Javadoc block and the element it documents.
- `@return` may be omitted when the method returns nothing or the value is
  obvious; `@param` may be omitted only when every parameter is
  self-explanatory (include all of them or none).
- `{@inheritDoc}` is the right tool for an override that extends, rather than
  replaces, the parent contract.

Single-line form is fine for a field:

```java
/** Number of connections to this database. */
private int connectionCount;
```

## Verifying

```
./gradlew javadoc      # Javadoc must build; watch for new warnings
./gradlew test         # behaviour must be unchanged by a style fix
```

Style-only edits must not change behaviour. If a rename touches the console
output or a public contract, update `test/ui-test-plan.md` too.
