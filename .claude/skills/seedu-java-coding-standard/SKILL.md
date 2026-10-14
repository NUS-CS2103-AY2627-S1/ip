---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (basic + intermediate rules) required by this project. Use whenever writing, editing, or reviewing any .java file in this repo - naming, formatting/layout, imports, control-flow style, and comments.
---

# SE-EDU Java Coding Standard

This project (per [A-CodingStandard](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html)) must
follow the **basic and intermediate** rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Apply this to every `.java` file under `src/main/java` and `src/test/java` -
new code and edits to existing code alike.

## Naming

* Packages: all lowercase, logically grouped (e.g. `verity.task`, `verity.command`).
* Classes/enums: nouns in PascalCase (e.g. `Task`, `TaskList`).
* Variables: camelCase (e.g. `line`, `taskList`).
* Constants: `SCREAMING_SNAKE_CASE` (e.g. `MAX_ITERATIONS`).
* Methods: verbs in camelCase (e.g. `getName()`, `computeTotalWidth()`).
* Test methods: `featureUnderTest_testScenario_expectedBehavior()` (parts may be omitted).
* Abbreviations/acronyms are not upper-cased within a name: `exportHtmlSource()`, not `exportHTMLSource()`.
* English only.
* Scope-based length: larger scope -> longer, more descriptive name; short names (`i`, `j`, `k`) are fine only for
  tight loop-counter scopes (`j`/`k` only for nested loops).
* Booleans: prefix `is`/`has`/`was`/`can`/`should` (e.g. `isVisible`, `hasLicense()`); boolean setters take the form
  `void setFound(boolean isFound)`.
* Collections: plural names (e.g. `Collection<Point> points`).
* Related constants share a common prefix (e.g. `COLOR_RED`, `COLOR_GREEN`).

## Layout

* 4-space indentation, never tabs.
* Line length: soft limit 110 chars, hard limit 120.
* Wrapped lines: indent 8 spaces (double the normal indent).
* Break after commas; break before operators (including `.`, type-bound `&`, catch-clause `|`).
* A method/constructor name stays attached to its opening `(`.
* Prefer breaking outside parenthesized expressions over breaking inside them.
* K&R/Egyptian braces: opening brace on the same line as the statement.
* One blank line between logical units within a block.
* Whitespace: around binary operators (`a = (b + c) * d`), after reserved words (`while (true)`), after commas,
  around colons in binary/ternary contexts.

### Control-flow templates

```java
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
```

```java
for (initialization; condition; update) {
    statements;
}

while (condition) {
    statements;
}

do {
    statements;
} while (condition);
```

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
The `// Fallthrough` comment is **required** on any `case` that has no `break` (including a deliberate fall-through
to `default`). The modern arrow form (`case ABC -> ...`) is also acceptable and never needs a fallthrough comment,
since it can't fall through.

```java
try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
```

```java
public void someMethod() throws SomeException {
    ...
}
```

Ternary expressions:
```java
alpha = (aLongBooleanExpression) ? beta : gamma;

alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

* Loop/conditional bodies are always wrapped in `{ }`, even for a single statement.
* The conditional test goes on its own line, separate from the body.

## Imports & packages

* Every class belongs to a package (no default-package classes).
* No wildcard imports - list every class explicitly.
* Imports grouped and ordered consistently: static imports, then `java`, `javax`, `org`, `com`, `javafx`.

## Types & variables

* Array brackets attach to the type, not the variable: `String[] tasks`, not `String tasks[]`.
* Initialize variables where declared, in the smallest scope possible.
* Don't declare a class field `public` unless the class is a pure data class with no behavior; constants
  (`public static final`) are the exception.

## Comments / Javadoc

* English only, American spelling.
* Header (Javadoc) comments required for every non-private class and method, and every non-trivial private method.
  Exceptions: simple getters/setters, overridden methods whose parent Javadoc already applies via `@inheritDoc`,
  and test classes/methods.
* `/**` opens on its own line; continuation lines align their `*` with the first one, with a space after each `*`.
* First sentence is a short standalone summary (Javadoc uses it in the summary table). For a **method**, it starts
  with a third-person verb: "Returns ...", "Sends ...", "Adds ..." (not "Return ..." or "Returning ...").
* Blank line between the description and the `@param`/`@return`/`@throws` block.
* Every `@param` line ends with a period.
* `@return` may be omitted if the method is `void` or the return value is obvious from the description.
* `@param` is all-or-nothing per method: document every parameter, or none (only skip them all if every name is
  self-explanatory or already covered in the main description).
* Use `{@inheritDoc}` for an overridden method whose contract only slightly differs from its parent's.
* A class member (field) may use the single-line form: `/** Number of connections to this database */`.
* No blank line between the closing `*/` and the declaration it documents.
