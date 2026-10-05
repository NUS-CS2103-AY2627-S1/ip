---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (basic + intermediate levels) that all Java code in this repository must follow, and which Checkstyle (config/checkstyle/checkstyle.xml, run via ./gradlew checkstyleMain checkstyleTest) partially enforces. Load this whenever writing new Java code, editing existing Java code, reviewing or auditing Java for style, fixing Checkstyle failures, or reviewing a peer's Java in a pull request. It covers naming, brace style, indentation, line wrapping, imports, declarations and comments. Trigger on any request to write, refactor, format, lint or review Java in this project, even when the standard is not named explicitly.
---

# SE-EDU Java Coding Standard (basic + intermediate)

CS2103T requires **both** the basic and intermediate rules
(<https://se-education.org/guides/conventions/java/intermediate.html>). Advanced rules are
optional. Every rule below is quoted or paraphrased from that source.

**Checkstyle is not a substitute for this document.** `config/checkstyle/checkstyle.xml`
catches only some of these rules mechanically; the rest are checked by human reviewers, in
peer PR reviews, and in the Practical Exam. Where Checkstyle and this document disagree
about something Checkstyle actually checks, trust Checkstyle and correct this file.

## Naming

- **Packages** — all lower case, e.g. `com.company.application.ui`. For school projects use
  the project name plus logical subdivisions, e.g. `todobuddy.ui`, `todobuddy.file`.
- **Classes / enums** — nouns, PascalCase: `Line`, `AudioSystem`.
- **Variables** — camelCase: `line`, `audioSystem`.
- **Constants** — all uppercase, underscore-separated (SCREAMING_SNAKE_CASE):
  `MAX_ITERATIONS`, `COLOR_RED`.
- **Methods** — verbs, camelCase: `getName()`, `computeTotalWidth()`.
- **All names in English.** Rationale: the code is meant for an international audience.

### Test method names

Format: `featureUnderTest_testScenario_expectedBehavior()` — e.g.
`sortList_emptyList_exceptionThrown()`.

Underscores **may** be used this way; the format is permitted, not mandated. **The third
part, or both the second and third parts, can be omitted** depending on what the test
covers. `sortList_emptyList()` tests `sortList()` for all variations of the empty-list
scenario; `sortList()` tests it for all scenarios. Two-part and one-part names are legal —
do not flag them as violations.

### Booleans

Boolean variables and methods should be named to sound like booleans, typically with an
`is` / `has` / `was` prefix:

- variables — `isSet`, `isVisible`, `isFinished`, `isFound`, `isOpen`, `hasData`, `wasOpen`
- methods — `boolean isCompleted()`, not `boolean getCompleted()`

Setters for booleans keep `set` on the method and move the prefix to the parameter:

```java
void setFound(boolean isFound);
```

### Collections, iterators, scope

- **Plural form for collections**: `Collection<Point> points;`, `int[] values;`
- **Iterator variables** may be called `i`, `j`, `k`. Reserve `j` and `k` for *nested* loops.
- **Scope drives length**: "Variables with a large scope should have long names, variables
  with a small scope can have short names." Scratch variables used for temporary storage or
  indices can be kept short — a reader should be able to assume the value is not used
  outside a few lines.

### Abbreviations and associated constants

- **Abbreviations and acronyms are not uppercase inside a name.**
  Good: `exportHtmlSource()`, `openDvdPlayer()`. Bad: `exportHTMLSource()`, `openDVDPlayer()`.
- **Associated constants should share a common prefix**, not a common suffix:

  ```java
  static final int COLOR_RED = 1;
  static final int COLOR_GREEN = 2;
  ```

  So `MARKER_BY` / `MARKER_FROM` / `MARKER_TO`, not `BY_MARKER` / `FROM_MARKER` / `TO_MARKER`.

## Layout

- **Indentation** — 4 spaces, never tabs.
- **Line length** — soft limit 110 characters, hard limit 120. Never exceed 120.
- **Wrapped lines** — indent 8 spaces (twice the normal indentation) from the parent line.
- **Blank lines** — logical units within a block are separated by one blank line.

### Line breaks

When wrapping, readability is the objective — do not blindly accept the IDE's formatting.

- Break **after** a comma.
- Break **before** an operator. This also applies to operator-like symbols: the dot
  separator `.`, the ampersand in type bounds `<T extends Foo & Bar>`, and the pipe in
  catch blocks `catch (FooException | BarException e)`.

```java
totalSum = a + b + c
        + d + e;
```

### White space within a statement

- Operators are surrounded by spaces: `a = (b + c) * d;` not `a=(b+c)*d;`
- Java reserved words are followed by a space: `while (true) {`
- Space after commas: `doSomething(a, b, c);`
- Space after each `;` in a `for` header: `for (i = 0; i < 10; i++)`

### Braces — K&R ("Egyptian") style

The opening brace stays on the same line; `else`, `catch` and `finally` stay on the same
line as the preceding closing brace.

```java
public void someMethod() throws SomeException {
    statements;
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

### switch

**`case` labels are indented one level inside the `switch`** — this is the form given in the
standard, and it is easy to get wrong:

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

A deliberate fallthrough requires an explicit `// Fallthrough` comment. The modern arrow
form is also acceptable: `case ABC -> method("1");`

## Statements

### Packages and imports

- **Put every class in a package.** No default package.
- **The ordering of import statements must be consistent.** The standard's example groups
  static imports first, then `java`, then `javax`, and so on. Whichever order you adopt,
  apply it identically in *every* file, main and test alike — inconsistency between source
  and test trees is itself the violation.
- **List imported classes explicitly.** `import java.util.List;` — never `import java.util.*;`

### Types and variables

- **Array specifiers attach to the type, not the variable**: `int[] a = new int[20];`
  not `int a[] = new int[20];`
- **Variables are initialized where they are declared**, and declared in the smallest scope
  possible.
- **Class variables should never be declared public** unless the class is a data class with
  no behavior. *This rule does not apply to constants* — a `public static final` constant is
  fine.

### Loops and conditionals

- The loop body is wrapped in braces regardless of how many statements it contains.
- The conditional is put on a separate line from its action.
- Single-statement conditionals still get braces — no bare `if (x) doThing();`

## Comments

- **All comments in English. Use American spelling.** Avoid local slang. (So "behavior",
  not "behaviour"; "capitalization", not "capitalisation".)
- **Write descriptive header comments for all classes and public methods.** They may be
  omitted only for:
  1. getters and setters,
  2. overriding methods, provided the parent's Javadoc applies exactly as-is,
  3. classes and methods used for testing.

  Rationale: public methods are used by others, and users should not have to read the
  method body to learn its behavior. Code can only show HOW it works, never WHAT it is
  supposed to do.
- **Comments are indented relative to their position in the code.**

### Javadoc form

```java
/**
 * Returns lateral location of the specified position.
 * If the position is unset, NaN is returned.
 *
 * @param x X coordinate of position.
 * @param y Y coordinate of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If specified position is invalid.
 */
```

- The opening `/**` sits on its own line.
- The first sentence summarizes the method.
- Subsequent `*` are aligned under the first, each followed by a space.
- A blank line separates the description from the `@param` block.
- `@param` and `@return` descriptions are punctuated.
- `@param` / `@return` may be omitted when the method has no return value or the parameters
  are self-explanatory.

## Checking work against this standard

```bash
./gradlew checkstyleMain checkstyleTest
```

Both are wired into `check`, so `./gradlew build` fails on a mechanically-detectable
violation. Rules Checkstyle cannot see — American spelling in comments, whether a Javadoc
header is actually descriptive, whether a boolean name reads like a boolean, whether
associated constants share a prefix — still need reading the code.
