---
name: seedu-java-coding-standard
description: Apply the se-education.org Java coding standard (basic + intermediate levels) to Java code in this project. Use whenever writing, modifying, or reviewing code under src/main/java or src/test/java, and before treating any such change as done. Trigger even if the user only says "check the style", "clean this up", or "does this follow conventions".
---

# seedu-java-coding-standard

This project follows the **se-education.org Java coding standard** at the
**intermediate** level, which includes every rule from the **basic** level.

- Basic: https://se-education.org/guides/conventions/java/basic.html
- Intermediate: https://se-education.org/guides/conventions/java/intermediate.html

All Java under `src/main/java` and `src/test/java` must comply. When you add or
change code, walk the checklist below for the lines you touched, then fix any
older violation you can see in the same file while you are there (opportunistic
cleanup, not a mandate to reformat the whole file).

## How to apply

1. Write the code to match the rules from the start -- they are not
   reformatting steps, they are how the code should look.
2. Before finishing, check the changed lines against the checklist. Pay
   special attention to the mechanical rules that are easy to miss:
   line length, wrapped-line indentation, braces on every `if`/loop body,
   explicit imports, and a Javadoc header on every class.
3. Line length is quick to verify:
   ```bash
   awk 'length > 120 { printf "%s:%d (%d chars)\n", FILENAME, FNR, length }' $(git ls-files 'src/**/*.java')
   ```
   Aim for <= 110 (soft limit); 120 is the hard limit -- never exceed it,
   comments and Javadoc included.

## Naming

- **Packages**: all lowercase, `projectname.logicalgroup` style
  (this project uses `steph` and `steph.task`). No `edu.nus.comp.*`.
- **Classes / enums**: nouns in `PascalCase` (`Deadline`, `TaskList`).
- **Methods**: verbs in `camelCase` (`parseCommand()`, `computeTotalWidth()`).
- **Variables**: `camelCase` (`taskIndex`, `audioSystem`).
- **Constants** (`static final`): `UPPERCASE_WITH_UNDERSCORES` (`MAX_ITERATIONS`).
  Associated constants share a prefix (`COLOR_RED`, `COLOR_GREEN`).
- **Booleans**: read like a yes/no question -- prefix `is`, `has`, `was`,
  `can`, `should` (`isDone`, `hasNext`, `canEvaluate()`). Setter form is
  `void setFound(boolean isFound)`.
- **Collections**: plural (`List<Task> tasks`).
- **Loop counters / scratch**: short names `i`, `j`, `k` for ints (nested loops
  use `j`, `k`), `c`, `d` for chars. Bigger scope => longer, fuller name.
- **Abbreviations / acronyms** are not all-caps inside a name:
  `exportHtmlSource()` / `openDvdPlayer()`, not `exportHTMLSource()` /
  `openDVDPlayer()`.
- **Test methods** (JUnit): `featureUnderTest_scenario_expectedBehavior()`,
  e.g. `parseTaskIndex_zero_exceptionThrown()`.
- All names in **English**.

## Layout and formatting

- **Indent 4 spaces. Never tabs.**
- **Wrapped lines indent 8 spaces** (double the normal indent), so a
  continuation is visually distinct from the next statement.
- **Line length <= 120 chars** (hard limit), <= 110 preferred. Applies to
  code, comments, and Javadoc.
- **K&R ("Egyptian") braces**: opening brace on the same line as the
  statement, closing brace on its own line.
  ```java
  while (!done) {
      doSomething();
  }
  ```
  not
  ```java
  while (!done)
  {
      doSomething();
  }
  ```
- **Every `if` / `else` / `for` / `while` / `do` body is wrapped in braces**,
  even a single statement. The condition goes on its own line.
  ```java
  if (isDone) {
      doCleanup();
  }
  ```
  not `if (isDone) doCleanup();`
- **`else` / `catch` / `finally` / `while` (of `do`)** sit on the same line as
  the preceding closing brace: `} else {`, `} catch (IOException e) {`.
- **`switch`**: `case` labels at the **same indentation as `switch`** (not
  indented one level in). Mark an intentional missing `break` with a
  `// Fallthrough` comment. Arrow syntax (`case X -> ...`) and switch
  expressions are fine.
- **Whitespace**:
  - binary operators surrounded by spaces: `a = (b + c) * d;`
  - keyword then space then paren: `if (`, `while (`, `for (`, `switch (`,
    `catch (`
  - space after every comma: `doSomething(a, b, c);`
  - space after each `;` in a `for` header
  - no space between a method name and its `(`
- **Blank lines**: one blank line between logical units inside a method; one
  or more between methods. No blank line between a Javadoc block and the thing
  it documents.
- **Line breaks in long expressions**: break after a comma, or before an
  operator (`.`, `+`, `&&`, the `|` in a multi-catch). Prefer a break at a
  higher syntactic level over a lower one. A ternary is either one line or
  fully three lines.

## Statements

- **Every class is in a package.**
- **Imports are explicit** -- never `import java.util.*;`. Keep the import
  list minimal and current (let the IDE prune it).
- **Import order is consistent**: static imports first, then `java`, `javax`,
  `org`, `com`, then project / framework groups. This project's files use:
  `java.*`, blank line, `steph.*` -- follow that.
- **Array brackets attach to the type**: `int[] a`, not `int a[]`.
- **Declare a variable in the smallest scope** that works, and **initialize it
  where it is declared** where practical.
- **Fields are `private`** (widen only with a concrete reason). A field is
  `public` only in a pure data class with no behaviour; `public static final`
  constants are the exception. Prefer `final` for a field assigned once.

## Comments and Javadoc

- All comments in **English**, American spelling, no slang.
- **Every public class and public/protected method has a Javadoc header**,
  except: plain getters/setters, methods whose meaning is fully obvious, an
  `@Override` whose parent Javadoc already says everything, and test
  classes / test methods.
- Javadoc shape:
  - `/**` on its own line; each following `*` aligned under the first, with a
    space after it; closing `*/` on its own line.
  - First sentence is a **short summary** ending in a period -- it is what
    shows in the method index. Phrase it as a statement: *"Returns the ..."*,
    *"Parses the ..."*, not *"Return the ..."* / *"Parse the ..."*.
  - Blank line between the description and the first `@` tag.
  - `@param` / `@return` / `@throws` descriptions end with punctuation.
  - Give `@param` for **all** parameters or none; omit `@return` when the
    method is `void` or the return is obvious from the summary.
  - Overriding method: `{@inheritDoc}` rather than a copy.
- A one-line member Javadoc is allowed:
  `/** Horizontal rule printed around each response block. */`
- Comments are indented to match the code they describe. A short trailing
  comment is fine: `process(dummy); // a throwaway value`.

## Anything not covered here

Fall back to the Google Java Style Guide
(https://google.github.io/styleguide/javaguide.html), then to the
surrounding code's existing style.
