# SE-EDU Java coding standard — reference

Source: [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html).
Fallback: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

This file expands the checklist in [SKILL.md](SKILL.md). Apply every rule below unless the user explicitly overrides it.

## Naming

| Kind | Rule | Examples |
| --- | --- | --- |
| Package | all lowercase; school projects use project name + logical group, not `edu.nus.*` | `wodan.ui`, `wodan.task` |
| Class / enum | noun, PascalCase | `TaskList`, `CommandWord` |
| Variable | camelCase | `filePath`, `taskNumber` |
| Constant | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS`, `COLOR_RED` |
| Method | verb, camelCase | `getName()`, `computeTotalWidth()` |
| Test method | `feature_scenario_expected()`; later parts optional | `parse_emptyLine_exceptionThrown()` |

- Abbreviations inside a name are not fully uppercase: `exportHtmlSource()`, `openDvdPlayer()`, `stripBom()`.
- All names in English (international readers).
- Wide scope → longer name. Tiny scope (a few lines) → short name is fine (`i`, `j`, `n`, `c`).
- Booleans sound like booleans: `isSet`, `isVisible`, `hasData`, `wasOpen`; methods `hasLicense()`, `canEvaluate()`. Prefer `is` / `has` / `was` so tools can check. Boolean setter: `void setFound(boolean isFound)`.
- Collections use plural names: `Collection<Point> points`, `int[] values`.
- Loop index `i`; nested loops `j`, `k`.
- Related constants share a prefix so they sort together: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.

## Layout

- Indent **4 spaces** (not tabs).
- Soft line-length limit **110**; hard limit **120**. Wrap for readability, not only because the IDE wrapped.
- Continuation indent is **8 spaces** more than the parent line.

```java
setText("Long line split"
        + "into two parts.");
someMethodWithALongName(
        int anArg, Object anotherArg);
totalSum = a + b + c
        + d + e;
```

Wrap **after** a comma, **before** an operator (including `.`, type-bound `&`, catch `|`). Keep the method/constructor name on the same line as `(`. Prefer a higher-level break over splitting inside parentheses.

Ternary:

```java
alpha = (aLongBooleanExpression) ? beta : gamma;
alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

K&R braces:

```java
while (!done) {
    doSomething();
    done = moreToDo();
}
```

Method / `if` / `else` / `for` / `while` / `do-while` / `try-catch-finally` / `switch` use the forms on the SE-EDU page (opening `{` on the same line as the header).

`switch` (classic): include `// Fallthrough` when a `case` does not `break`/`return`/`throw`. Arrow `switch` is allowed.

Whitespace: spaces around operators; space after `if` / `while` / `for` / `catch`; space after commas; spaces around `:` in a ternary (not in `case X:`); space after `;` in `for`.

Separate logical units in a block with one blank line.

## Statements

### Packages and imports

- Every class is in a package.
- No star imports.
- Import order is consistent. In this project:

```text
static imports

java.*

javax.*

org.* / com.* / other third-party

wodan.*
```

Blank line between those groups; alphabetical within a group.

### Types and variables

- `int[] a`, not `int a[]`.
- Initialize where declared; smallest scope. If no honest initial value exists, leave it uninitialized rather than using a fake one.
- No `public` non-constant fields unless the class is a data holder with no behavior.

### Loops and conditionals

Always braces, even for one statement. Never `if (isDone) doCleanup();`. Put the condition on its own line so a debugger can stop on it.

## Comments

- English, American spelling, no local slang.
- Indent comments with the surrounding code. Trailing comments are allowed.
- Javadoc on every class and every public method, except getters/setters, exact overrides, and tests.
- Opening `/**` on its own line; `*` aligned; space after `*`; no blank line between the block and the declaration.
- First sentence is the summary (Javadoc uses it in the table). Methods start with `Returns …` / `Shows …` / `Creates …` / `Adds …`, not `Return`.
- Blank line before `@param` / `@return` / `@throws`. Period after each tag description.
- `@return` optional if void or already obvious. `@param`: all parameters or none.
- `{@inheritDoc}` when an override reuses the parent comment and optionally adds a difference.
- Fields may use a one-line Javadoc: `/** Number of connections to this database */`

## Google Java Style (fallback only)

Use when SE-EDU does not specify:

- Modifier order: `public protected private abstract default static sealed non-sealed final transient volatile synchronized native strictfp`.
- Diamond operator: `new ArrayList<>()`.
- One statement per line; no unused imports; UTF-8 source files.
