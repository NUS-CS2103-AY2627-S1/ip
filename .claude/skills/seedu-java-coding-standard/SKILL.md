---
name: seedu-java-coding-standard
description: Use when writing, editing, or reviewing ANY Java code in this project - the SE-EDU Java coding standard (intermediate level) that all code here must follow. Covers naming, layout, braces, imports, statements, and Javadoc rules. Invoke before writing Java, and again before claiming Java work is complete.
---

# SE-EDU Java Coding Standard (Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

All Java in this repository — `src/main/java` and `src/test/java` alike — must follow
these rules. When a rule below conflicts with your default style, this file wins.

## Checklist

Run through this before claiming any Java work is done:

- [ ] Every class is in a package; no wildcard imports
- [ ] Imports grouped and ordered consistently: static → `java.*` → `javax.*` → `org.*` → `com.*` → project (`amadeus.*`)
- [ ] 4-space indent, no tabs; wrapped lines indented 8 spaces
- [ ] No line over 120 chars (aim for 110)
- [ ] K&R braces; every `if`/`for`/`while` body braced, even one-liners
- [ ] Names follow the table below
- [ ] Every non-private class and method has a Javadoc header
- [ ] Javadoc first sentence is a summary; `@param` all-or-none per method
- [ ] No public fields
- [ ] `./gradlew build` passes

## Naming

| Kind | Rule | Good | Bad |
|---|---|---|---|
| Package | all lowercase | `amadeus.parser` | `amadeus.Parser` |
| Class / enum | noun, PascalCase | `TaskDateTime` | `taskDateTime` |
| Method | verb, camelCase | `parseTodo()`, `getName()` | `Parse_todo()` |
| Variable | camelCase | `audioSystem` | `audio_system` |
| Constant | UPPER_SNAKE_CASE | `MAX_ITERATIONS` | `maxIterations` |
| Boolean | sounds like a boolean | `isDone`, `hasData`, `wasOpen` | `done`, `flag` |
| Collection | plural | `List<Task> tasks` | `List<Task> taskList` |

More naming rules:

- Abbreviations are **not** uppercase inside a name: `exportHtmlSource()`, not `exportHTMLSource()`.
- All names in English.
- Scope drives length: `i`, `j`, `k` are fine as loop counters; a field needs a real name.
- Associated constants share a prefix: `COLOR_RED`, `COLOR_GREEN`.

**Test methods** use `featureUnderTest_testScenario_expectedBehavior()`:

```java
parseTaskIndex_numberAboveTaskCount_throwsAmadeusException()
sortList_emptyList_exceptionThrown()
```

## Layout

- **Indent** 4 spaces. Never tabs.
- **Line length** max 120 chars, soft limit 110.
- **Wrapped lines** indent 8 spaces (twice normal).
- Break *after* commas, *before* operators. Keep a method name attached to its `(`.
- Separate logical units within a block with one blank line.

**K&R braces** — opening brace on the same line:

```java
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
```

**Braces are never optional.** Both of these are wrong:

```java
if (isDone) doCleanup();          // conditional must be on its own line, and braced
for (int i = 0; i < 5; i++) x++;  // loop body must be braced
```

**Whitespace**: operators surrounded by spaces (`a = (b + c) * d;`), space after
reserved words (`while (true) {`), space after commas (`f(a, b, c);`).

## Statements

- Put every class in a package.
- List imports explicitly — **no** `import java.util.*;`.
- Array specifier attaches to the type: `int[] a`, not `int a[]`.
- Initialize variables where declared; declare them in the smallest scope that works.
- Never declare a field `public` unless the class is a pure data class with no behaviour.
  (`protected` for subclass access is fine.)

## Javadoc

Write a header comment for **all non-private classes and methods**. May be omitted for
getters/setters, overridden methods where the parent's doc still applies, and test
classes/methods.

Format rules:

- `/**` on its own line, subsequent `*` aligned, space after each `*`.
- **First sentence is a short summary** — it is what appears in the generated method
  table. Never start a block with a tag.
- Method summaries start with a verb: `Returns ...`, `Creates ...`, `Adds ...`.
- Blank line between the description and the tag section.
- Punctuation after each parameter description.
- No blank line between the Javadoc block and the thing it documents.
- `@return` may be omitted when the method returns nothing or the summary already says it.
- **`@param` is all-or-none per method** — document every parameter or none. Never some.
- Single-line form is fine for fields: `/** Number of connections to this database. */`

```java
/**
 * Returns the todo described by a line of the form {@code todo <description>}.
 *
 * @param input the whole line the user typed, e.g. "todo read book".
 * @return a new todo, not yet done.
 * @throws AmadeusException if the description is missing.
 */
public static Todo parseTodo(String input) throws AmadeusException {
```

Comments are in English, indented to match the code around them.

## Verifying

```bash
./gradlew build      # compiles + runs tests
./gradlew javadoc    # generates docs; warnings about omitted @return/@param
                     # are the tool being stricter than this standard - ignore those
```
